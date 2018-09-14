package com.bupocket.fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
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
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BPAssetsFragment extends BaseFragment {
    @BindView(R.id.user_nick)
    TextView mUserNickTextView;

    private List<TokenTxInfo> tokenTxInfoList;
    private MyTokenTxAdapter myTokenTxAdapter;
    @BindView(R.id.my_token_tx_lv)
    ListView tokenTxsListView;
    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;

    @BindView(R.id.accountBUBalanceTv)
    TextView mAccountBuBalanceTv;
    private SharedPreferencesHelper sharedPreferencesHelper;
    private String pageSize = "10";
    private String pageStart = "1";
    private String currentAccAddress;
    private String currentAccNick;

    @BindView(R.id.wallet_scan_btn)
    QMUIRoundButton mWalletScanBtn;
    @BindView(R.id.showMyAddressLv)
    LinearLayout mShowMyaddressL;
    @BindView(R.id.walletSendBtn)
    QMUIRoundButton mSendBtn;
    @BindString(R.string.qr_copy_success_message)
    String copySuccessMessage;
    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_assets, null);
        ButterKnife.bind(this, root);
        initData();
        loadMyTxList(pageSize, pageStart, "-1", currentAccAddress);
        initWalletInfoView();
        initMyTxListViews();
        showMyAddress(currentAccAddress);

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
        tokenTxInfoList = new ArrayList<>();

        mAccountBuBalanceTv.setText(getAccountBUBalance());

    }

    private String getAccountBUBalance(){
        String buBalance = Wallet.getAccountBUBalance(currentAccAddress);
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
            public void onRefresh(RefreshLayout refreshlayout) {
                loadMyTxList(pageSize, pageStart, "-1", currentAccAddress);
                refreshlayout.finishRefresh(500);
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                loadMyTxList(pageSize, (Integer.parseInt(pageStart)+1) + "", "-1", currentAccAddress);
                refreshlayout.finishLoadMore(500);
            }
        });
    }

    private void initAssetsGroupListView(GetMyTxsRespDto getMyTxsRespDto){

        if(getMyTxsRespDto != null){
            for (GetMyTxsRespDto.TxRecordBean obj : getMyTxsRespDto.getTxRecord()) {

                String txAccountAddress = AddressUtil.anonymous((obj.getOutinType() == 0) ? obj.getToAddress() : obj.getFromAddress());
                String amountStr = (obj.getOutinType() == 0) ? "-" + obj.getAmount() : "+" + obj.getAmount();

                DecimalFormat decimalFormat = new DecimalFormat("###,###.00000000");
                amountStr = decimalFormat.format(Double.parseDouble(amountStr));
                String txStartStr = (obj.getTxStatus() == 0) ? "成功" : "失败";
                tokenTxInfoList.add(new TokenTxInfo(txAccountAddress, TimeUtil.getDateDiff(obj.getTxTime()), amountStr, txStartStr));
            }

        }

        myTokenTxAdapter = new MyTokenTxAdapter(tokenTxInfoList, getContext());
        tokenTxsListView.setAdapter(myTokenTxAdapter);
        tokenTxsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TokenTxInfo currentItem = (TokenTxInfo) myTokenTxAdapter.getItem(position);
                Toast.makeText(getActivity(), currentItem.getTxAmount() + " is Clicked", Toast.LENGTH_SHORT).show();
                startFragment(new BPAssetsTxDetailFragment());
            }
        });
    }



    private void loadMyTxList(String pageSize,String pageStart,String pageTotal,String currentAccAddr) {
        TxService txService = RetrofitFactory.getInstance().getRetrofit().create(TxService.class);
        Map<String, Object> parmasMap = new HashMap<>();
        parmasMap.put("walletAddress",currentAccAddr);
        parmasMap.put("startPage", pageStart);
        parmasMap.put("pageSize", pageSize);
        System.out.println(JSON.toJSONString(parmasMap));
        Call<ApiResult<GetMyTxsRespDto>> call = txService.getMyTxs(parmasMap);
        call.enqueue(new Callback<ApiResult<GetMyTxsRespDto>>() {
            @Override
            public void onResponse(Call<ApiResult<GetMyTxsRespDto>> call, Response<ApiResult<GetMyTxsRespDto>> response) {

                ApiResult<GetMyTxsRespDto> respDto = response.body();
                initAssetsGroupListView(respDto.getData());
            }

            @Override
            public void onFailure(Call<ApiResult<GetMyTxsRespDto>> call, Throwable t) {
                t.printStackTrace();
            }
        });

    }

    private void showMyAddress(final String currentAccAddress) {
        mShowMyaddressL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createQMUIDialog(currentAccAddress);
            }
        });
    }


    private void createQMUIDialog(final String currentAccAddress){
        final QMUIDialog dialog = new QMUIDialog.CustomDialogBuilder(getContext())
                .setLayout(R.layout.fragment_show_qr)
                .create();

        TextView address_text = dialog.findViewById(R.id.qr_address_text);
        address_text.setText(currentAccAddress);

        Bitmap mBitmap = QRCodeUtil.createQRCodeBitmap(currentAccAddress, 356, 356);
        ImageView mImageView = dialog.findViewById(R.id.qr_pocket_address_image);
        mImageView.setImageBitmap(mBitmap);

        dialog.findViewById(R.id.address_copy_btn).setOnClickListener(new View.OnClickListener() {
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

        dialog.findViewById(R.id.cancel_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void go2SendTokenFragment(){
        startFragment(new BPSendTokenFragment());
    }
}
