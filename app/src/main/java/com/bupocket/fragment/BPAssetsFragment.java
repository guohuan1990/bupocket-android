package com.bupocket.fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bupocket.R;
import com.bupocket.activity.CaptureActivity;
import com.bupocket.adaptor.MyTokenTxAdapter;
import com.bupocket.base.BaseFragment;
import com.bupocket.enums.TxStatusEnum;
import com.bupocket.http.api.RetrofitFactory;
import com.bupocket.http.api.TxService;
import com.bupocket.http.api.dto.resp.ApiResult;
import com.bupocket.http.api.dto.resp.GetMyTxsRespDto;
import com.bupocket.model.TokenTxInfo;
import com.bupocket.utils.AddressUtil;
import com.bupocket.utils.QRCodeUtil;
import com.bupocket.utils.SharedPreferencesHelper;
import com.bupocket.utils.TimeUtil;
import com.bupocket.wallet.Wallet;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUIEmptyView;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BPAssetsFragment extends BaseFragment {
    @BindView(R.id.user_nick)
    TextView mUserNickTextView;
    private Map<String, TokenTxInfo> tokenTxInfoMap = new HashMap<>();
    private List<TokenTxInfo> tokenTxInfoList = new ArrayList<>();
    private MyTokenTxAdapter myTokenTxAdapter;
    @BindView(R.id.my_token_tx_lv)
    ListView tokenTxsListView;
    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;

    @BindView(R.id.accountBUBalanceTv)
    TextView mAccountBuBalanceTv;
    protected SharedPreferencesHelper sharedPreferencesHelper;
    private String pageSize = "50";
    private Integer pageStart = 1;
    private String currentAccAddress;
    private String currentAccNick;

    @BindView(R.id.walletScanBtn)
    QMUIRoundButton mWalletScanBtn;
    @BindView(R.id.showMyAddressLv)
    LinearLayout mShowMyaddressL;
    @BindView(R.id.walletSendBtn)
    QMUIRoundButton mSendBtn;
    @BindString(R.string.qr_copy_success_message)
    String copySuccessMessage;
    @BindView(R.id.userBcAddress)
    TextView mUserBcAddressTv;
    @BindView(R.id.emptyView)
    QMUIEmptyView mEmptyView;

    @BindView(R.id.assetsAvatarIv)
    QMUIRadiusImageView mAssetsAvatarIv;
    private String buBalance = "-";

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_assets, null);
        ButterKnife.bind(this, root);
        initData();
        initWalletInfoView();
        initMyTxListViews();
        showMyAddress();

        mWalletScanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startScan();
            }
        });
        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                go2SendTokenFragment();
            }
        });
        mAssetsAvatarIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle argz = new Bundle();
                argz.putString("accName",currentAccNick);
                BPUserInfoFragment bpUserInfoFragment = new BPUserInfoFragment();
                bpUserInfoFragment.setArguments(argz);
                startFragment(bpUserInfoFragment);
            }
        });

        return root;
    }

    private void initData(){
        sharedPreferencesHelper = new SharedPreferencesHelper(getContext(), "buPocket");
        currentAccNick = sharedPreferencesHelper.getSharedPreference("currentAccNick", "").toString();
        currentAccAddress = sharedPreferencesHelper.getSharedPreference("currentAccAddr", "").toString();
        QMUIStatusBarHelper.setStatusBarDarkMode(getBaseFragmentActivity());
        getAccountBUBalance();
        mAccountBuBalanceTv.setText(buBalance);
        mUserBcAddressTv.setText(AddressUtil.anonymous(currentAccAddress));
        refreshData();
    }

    private Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            mAccountBuBalanceTv.setText(msg.getData().get("buBalance").toString());
        };
    };

    private void getAccountBUBalance(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                buBalance = Wallet.getInstance().getAccountBUBalance(currentAccAddress);
                if(buBalance == null){
                    buBalance = "0";
                }
                Message msg = Message.obtain();
                Bundle data = new Bundle();
                data.putString("buBalance", buBalance);
                msg.setData(data);
                handler.sendMessage(msg);
            }
        }).start();
    }

    private void initWalletInfoView(){
        mUserNickTextView.setText(currentAccNick);
    }

    private void initMyTxListViews(){
        refreshLayout.setEnableLoadMore(false);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(final RefreshLayout refreshlayout) {
                refreshlayout.getLayout().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshData();
                        refreshlayout.finishRefresh();
                        refreshLayout.setNoMoreData(false);
                        initData();
                    }
                }, 500);

            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(final RefreshLayout refreshlayout) {
                refreshlayout.getLayout().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(myTokenTxAdapter == null){
                            refreshlayout.finishRefresh();
                            return;
                        }
                        if(pageStart == myTokenTxAdapter.getPage().getStart()){
                            loadMoreData();
                            refreshlayout.finishLoadMore(500);
                        }else{
                            refreshLayout.finishLoadMoreWithNoMoreData();
                        }
                    }
                }, 500);
            }
        });
    }


    private void refreshData(){
        pageStart = 1;
        tokenTxInfoMap.clear();
        tokenTxInfoList.clear();

        loadMyTxList();
    }

    private void loadMoreData(){
        pageStart ++;
        loadMyTxList();
    }

    private void handleMyTxs(GetMyTxsRespDto getMyTxsRespDto){

        if(getMyTxsRespDto != null || getMyTxsRespDto.getTxRecord() != null){
            if(getMyTxsRespDto.getTxRecord().size() == 0){
                mEmptyView.show(getResources().getString(R.string.emptyView_mode_desc_no_data), null);
                return;
            }

            refreshLayout.setEnableLoadMore(true);

            for (GetMyTxsRespDto.TxRecordBean obj : getMyTxsRespDto.getTxRecord()) {

                String txAccountAddress = AddressUtil.anonymous((obj.getOutinType() == 0) ? obj.getToAddress() : obj.getFromAddress());
                String amountStr = null;
                String txStartStr = null;
                if(obj.getOutinType() == 0){
                    amountStr = "-" + obj.getAmount() + " BU";
                }else {
                    amountStr = "+" + obj.getAmount() + " BU";
                }

                if(TxStatusEnum.SUCCESS.getCode().equals(obj.getTxStatus())){
                    txStartStr = TxStatusEnum.SUCCESS.getName();
                }else{
                    txStartStr = TxStatusEnum.FAIL.getName();
                }

                if(!tokenTxInfoMap.containsKey(obj.getTxHash())){
                    TokenTxInfo tokenTxInfo = new TokenTxInfo(txAccountAddress, TimeUtil.getDateDiff(obj.getTxTime()), amountStr, txStartStr);
                    tokenTxInfo.setTxHash(obj.getTxHash());
                    tokenTxInfo.setOutinType(obj.getOutinType());
                    tokenTxInfoMap.put(obj.getTxHash(), tokenTxInfo);
                    tokenTxInfoList.add(tokenTxInfo);
                }
            }

        }else{
            mEmptyView.show(getResources().getString(R.string.emptyView_mode_desc_fail_title), null);
            return;
        }

        myTokenTxAdapter = new MyTokenTxAdapter(tokenTxInfoList, getContext());
        myTokenTxAdapter.setPage(getMyTxsRespDto.getPage());
        tokenTxsListView.setAdapter(myTokenTxAdapter);
        tokenTxsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            TokenTxInfo currentItem = (TokenTxInfo) myTokenTxAdapter.getItem(position);
            Bundle argz = new Bundle();
            argz.putString("txHash", currentItem.getTxHash());
            argz.putInt("outinType",currentItem.getOutinType());
            BPAssetsTxDetailFragment bpAssetsTxDetailFragment = new BPAssetsTxDetailFragment();
            bpAssetsTxDetailFragment.setArguments(argz);
            startFragment(bpAssetsTxDetailFragment);
            }
        });
    }



    private void loadMyTxList() {
        TxService txService = RetrofitFactory.getInstance().getRetrofit().create(TxService.class);
        Map<String, Object> parmasMap = new HashMap<>();
        parmasMap.put("walletAddress",currentAccAddress);
        parmasMap.put("startPage", pageStart);
        parmasMap.put("pageSize", pageSize);
        Call<ApiResult<GetMyTxsRespDto>> call = txService.getMyTxs(parmasMap);
        call.enqueue(new Callback<ApiResult<GetMyTxsRespDto>>() {
            @Override
            public void onResponse(Call<ApiResult<GetMyTxsRespDto>> call, Response<ApiResult<GetMyTxsRespDto>> response) {

                ApiResult<GetMyTxsRespDto> respDto = response.body();
                Log.d("GetMyTxsRespDto:", JSON.toJSONString(respDto));
                handleMyTxs(respDto.getData());
            }

            @Override
            public void onFailure(Call<ApiResult<GetMyTxsRespDto>> call, Throwable t) {
                t.printStackTrace();
                mEmptyView.show(getResources().getString(R.string.emptyView_mode_desc_fail_title), null);
            }
        });

    }

    private void showMyAddress() {
        mShowMyaddressL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAccountAddressView();
            }
        });
    }

    private void showAccountAddressView(){
        final QMUIBottomSheet qmuiBottomSheet = new QMUIBottomSheet(getContext());
        qmuiBottomSheet.setContentView(qmuiBottomSheet.getLayoutInflater().inflate(R.layout.show_address_layout,null));
        TextView accountAddresTv = qmuiBottomSheet.findViewById(R.id.printAccAddressTv);
        accountAddresTv.setText(currentAccAddress);

        Bitmap mBitmap = QRCodeUtil.createQRCodeBitmap(currentAccAddress, 356, 356);
        ImageView mImageView = qmuiBottomSheet.findViewById(R.id.qr_pocket_address_image);
        mImageView.setImageBitmap(mBitmap);

        qmuiBottomSheet.findViewById(R.id.addressCopyBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager cm = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData mClipData = ClipData.newPlainText("Label", currentAccAddress);
                cm.setPrimaryClip(mClipData);
                final QMUITipDialog copySuccessDiglog = new QMUITipDialog.Builder(getContext())
                        .setIconType(QMUITipDialog.Builder.ICON_TYPE_SUCCESS)
                        .setTipWord(copySuccessMessage)
                        .create();
                copySuccessDiglog.show();
                getView().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        copySuccessDiglog.dismiss();
                    }
                }, 1500);
            }
        });
        qmuiBottomSheet.findViewById(R.id.closeBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qmuiBottomSheet.dismiss();
            }
        });
        qmuiBottomSheet.show();
    }

    private void go2SendTokenFragment(){
        startFragment(new BPSendTokenFragment());
    }

    private void startScan(){
        IntentIntegrator intentIntegrator = IntentIntegrator.forSupportFragment(this);
        intentIntegrator.setBeepEnabled(true);
        intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        intentIntegrator.setPrompt(getResources().getString(R.string.wallet_scan_notice));
        intentIntegrator.setCaptureActivity(CaptureActivity.class);
        // 开始扫描
        intentIntegrator.initiateScan();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(getActivity(), R.string.wallet_scan_cancel, Toast.LENGTH_LONG).show();
            } else {
//                Toast.makeText(getActivity(), "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                Bundle argz = new Bundle();
                argz.putString("destAddress",result.getContents());
                BPSendTokenFragment sendTokenFragment = new BPSendTokenFragment();
                sendTokenFragment.setArguments(argz);
                startFragment(sendTokenFragment);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);

        }
    }

}
