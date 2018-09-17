package com.bupocket.fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.alibaba.fastjson.JSON;
import com.bupocket.R;
import com.bupocket.adaptor.MyTokenTxAdapter;
import com.bupocket.base.BaseFragment;
import com.bupocket.dto.resp.ApiResult;
import com.bupocket.dto.resp.GetMyTxsRespDto;
import com.bupocket.http.api.RetrofitFactory;
import com.bupocket.http.api.TxService;
import com.bupocket.model.TokenTxInfo;
import com.bupocket.utils.AddressUtil;
import com.bupocket.utils.QRCodeUtil;
import com.bupocket.utils.SharedPreferencesHelper;
import com.bupocket.utils.TimeUtil;
import com.bupocket.wallet.Wallet;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.qmuiteam.qmui.widget.QMUIEmptyView;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private String pageSize = "5";
    private Integer pageStart = 1;
    private String pageTotal = "-1";
    Integer queryTxSize = 0;
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
                IntentIntegrator intentIntegrator = IntentIntegrator.forSupportFragment(getParentFragment());
                intentIntegrator.setBeepEnabled(true);
                intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                intentIntegrator.setPrompt("请将二维码置于取景框内扫描");
                // 开始扫描
                intentIntegrator.initiateScan();


            }
        });
        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                go2SendTokenFragment();
            }
        });
        return root;
    }

    private void initData(){
        sharedPreferencesHelper = new SharedPreferencesHelper(getContext(), "buPocket");
        currentAccNick = sharedPreferencesHelper.getSharedPreference("currentAccNick", "").toString();
        currentAccAddress = sharedPreferencesHelper.getSharedPreference("currentAccAddr", "").toString();

        mAccountBuBalanceTv.setText(getAccountBUBalance());
        mUserBcAddressTv.setText(AddressUtil.anonymous(currentAccAddress));
        refreshData();
    }

    private String getAccountBUBalance(){
        String buBalance = Wallet.getInstance().getAccountBUBalance(currentAccAddress);
        if(buBalance == null){
            return "0";
        }
        return buBalance;
    }

    private void initWalletInfoView(){
        mUserNickTextView.setText(currentAccNick);
    }

    private void initMyTxListViews(){
        refreshLayout.setEnableAutoLoadMore(true);
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
            public void onLoadMore(RefreshLayout refreshlayout) {
            if(pageStart == myTokenTxAdapter.getPage().getStart()){
                loadMoreData();
                refreshlayout.finishLoadMore(500);
            }else{
                refreshLayout.finishLoadMoreWithNoMoreData();
            }
            }
        });
//        refreshLayout.autoRefresh();
    }


    private void refreshData(){
        loadMyTxList(pageSize, pageStart, pageTotal, currentAccAddress,queryTxSize);
    }

    private void loadMoreData(){
        pageStart ++;
        loadMyTxList(pageSize, pageStart++, pageTotal, currentAccAddress, queryTxSize);
    }

    private void handleMyTxs(GetMyTxsRespDto getMyTxsRespDto){

        if(getMyTxsRespDto != null){
            for (GetMyTxsRespDto.TxRecordBean obj : getMyTxsRespDto.getTxRecord()) {

                String txAccountAddress = AddressUtil.anonymous((obj.getOutinType() == 0) ? obj.getToAddress() : obj.getFromAddress());
                String amountStr = (obj.getOutinType() == 0) ? "-" + obj.getAmount() : "+" + obj.getAmount();
                String txStartStr = (obj.getTxStatus() == 0) ? getResources().getString(R.string.tx_status_success_txt) : getResources().getString(R.string.tx_status_fail_txt);
                if(!tokenTxInfoMap.containsKey(obj.getTxHash())){
                    TokenTxInfo tokenTxInfo = new TokenTxInfo(txAccountAddress, TimeUtil.getDateDiff(obj.getTxTime()), amountStr, txStartStr);
                    tokenTxInfo.setTxHash(obj.getTxHash());
                    tokenTxInfoMap.put(obj.getTxHash(), tokenTxInfo);
                    tokenTxInfoList.add(tokenTxInfo);
                }
            }

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
                BPAssetsTxDetailFragment bpAssetsTxDetailFragment = new BPAssetsTxDetailFragment();
                bpAssetsTxDetailFragment.setArguments(argz);
                startFragment(bpAssetsTxDetailFragment);
            }
        });
    }



    private void loadMyTxList(String pageSize,Integer pageStart,String pageTotal,String currentAccAddr,Integer queryTxSize) {
        TxService txService = RetrofitFactory.getInstance().getRetrofit().create(TxService.class);
        Map<String, Object> parmasMap = new HashMap<>();
        parmasMap.put("walletAddress",currentAccAddr);
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

    public Integer getQueryTxSize() {
        return queryTxSize;
    }

    public void setQueryTxSize(Integer queryTxSize) {
        this.queryTxSize = queryTxSize;
    }

    private void go2SendTokenFragment(){
        startFragment(new BPSendTokenFragment());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 获取解析结果
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(getActivity(), "取消扫描", Toast.LENGTH_LONG).show();
            } else {
//                Toast.makeText(getActivity(), "扫描内容:" + result.getContents(), Toast.LENGTH_LONG).show();
                Bundle argz = new Bundle();
                argz.putString("destAddress",currentAccAddress);
                BPSendTokenFragment sendTokenFragment = new BPSendTokenFragment();
                sendTokenFragment.setArguments(argz);
                startFragment(sendTokenFragment);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);

        }
    }
}
