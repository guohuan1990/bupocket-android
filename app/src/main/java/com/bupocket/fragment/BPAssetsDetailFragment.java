package com.bupocket.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.alibaba.fastjson.JSON;
import com.bupocket.R;
import com.bupocket.activity.CaptureActivity;
import com.bupocket.adaptor.MyTokenTxAdapter;
import com.bupocket.base.BaseFragment;
import com.bupocket.enums.TxStatusEnum;
import com.bupocket.http.api.RetrofitFactory;
import com.bupocket.http.api.TokenService;
import com.bupocket.http.api.TxService;
import com.bupocket.http.api.dto.resp.ApiResult;
import com.bupocket.http.api.dto.resp.GetMyTxsRespDto;
import com.bupocket.model.TokenTxInfo;
import com.bupocket.utils.*;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUIEmptyView;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView;
import com.qmuiteam.qmui.widget.QMUITopBar;
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


public class BPAssetsDetailFragment extends BaseFragment {
    @BindView(R.id.topbar)
    QMUITopBar mTopBar;
    @BindView(R.id.assetIconIv)
    QMUIRadiusImageView mAssetIconIv;
    @BindView(R.id.assetAmountTv)
    TextView mAssetAmountTv;
    @BindView(R.id.assetValueTv)
    TextView mAssetValueTv;
    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;
    @BindView(R.id.emptyView)
    QMUIEmptyView mEmptyView;
    @BindView(R.id.myTokenTxLv)
    ListView mMyTokenTxLv;
    @BindView(R.id.walletScanBtn)
    QMUIRoundButton mWalletScanBtn;
    @BindView(R.id.walletSendBtn)
    QMUIRoundButton mWalletSendBtn;

    protected SharedPreferencesHelper sharedPreferencesHelper;
    private String assetCode;
    private Map<String, TokenTxInfo> tokenTxInfoMap = new HashMap<>();
    private List<TokenTxInfo> tokenTxInfoList = new ArrayList<>();
    private MyTokenTxAdapter myTokenTxAdapter;
    private GetMyTxsRespDto.PageBean page;
    private String pageSize = "10";
    private Integer pageStart = 1;
    private String currentAccAddress;
    private String issuer;
    private String decimals;
    private String tokenBalance;
    private String tokenType;

    @Override
    protected View onCreateView() {
        QMUIStatusBarHelper.setStatusBarLightMode(getBaseFragmentActivity());
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_assets_detail, null);
        ButterKnife.bind(this, root);
        initData();
        initTopBar();
        initTxListView();
        setListener();
        return root;
    }

    private void setListener() {
        mWalletScanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startScan();
            }
        });

        mWalletSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle argz = new Bundle();
                argz.putString("tokenType",tokenType);
                argz.putString("tokenCode",assetCode);
                argz.putString("tokenDecimals",decimals);
                argz.putString("tokenIssuer",issuer);
                argz.putString("tokenBalance",tokenBalance);
                BPSendTokenFragment sendTokenFragment = new BPSendTokenFragment();
                sendTokenFragment.setArguments(argz);
                startFragment(sendTokenFragment);
            }
        });
    }

    private void initTxListView() {
        mEmptyView.show(true);
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

                        loadMoreData();
                        refreshlayout.finishLoadMore(500);

                        if(!page.isNextFlag()){
                            refreshLayout.finishLoadMoreWithNoMoreData();
                        }
                    }
                }, 500);
            }
        });
    }

    private void refreshData() {
        pageStart = 1;
        tokenTxInfoMap.clear();
        tokenTxInfoList.clear();
        loadMyTxList();
    }

    private void loadMoreData() {
        if(page.isNextFlag()){
            pageStart ++;
            loadMyTxList();
        }
    }

    private void loadMyTxList() {
        TokenService tokenService = RetrofitFactory.getInstance().getRetrofit().create(TokenService.class);
        TxService txService = RetrofitFactory.getInstance().getRetrofit().create(TxService.class);
        Call<ApiResult<GetMyTxsRespDto>> call;
        if(assetCode.equals("BU") && issuer.equals("")){
            Map<String, Object> parmasMap = new HashMap<>();
            parmasMap.put("walletAddress",currentAccAddress);
            parmasMap.put("startPage", pageStart);
            parmasMap.put("pageSize", pageSize);
            call = txService.getMyTxs(parmasMap);
        }else {
            Map<String, Object> parmasMap = new HashMap<>();
            parmasMap.put("assetCode",assetCode);
            parmasMap.put("issuer",issuer);
            parmasMap.put("address",currentAccAddress);
            parmasMap.put("startPage", pageStart);
            parmasMap.put("pageSize", pageSize);
            call = tokenService.getTokenTxs(parmasMap);
        }
        call.enqueue(new Callback<ApiResult<GetMyTxsRespDto>>() {
            @Override
            public void onResponse(Call<ApiResult<GetMyTxsRespDto>> call, Response<ApiResult<GetMyTxsRespDto>> response) {
                ApiResult<GetMyTxsRespDto> respDto = response.body();
                Log.d("GetMyTxsRespDto:", JSON.toJSONString(respDto));
                mEmptyView.show(null,null);
                handleMyTxs(respDto.getData());
            }

            @Override
            public void onFailure(Call<ApiResult<GetMyTxsRespDto>> call, Throwable t) {
                t.printStackTrace();
                mEmptyView.show(getResources().getString(R.string.emptyView_mode_desc_fail_title), null);
            }
        });
    }

    private void handleMyTxs(GetMyTxsRespDto getMyTxsRespDto) {

        if(getMyTxsRespDto != null || getMyTxsRespDto.getTxRecord() != null){
            page = getMyTxsRespDto.getPage();
            if(getMyTxsRespDto.getTxRecord() == null || getMyTxsRespDto.getTxRecord().size() == 0) {
                mEmptyView.show(getResources().getString(R.string.emptyView_mode_desc_no_data), null);
                return;
            }else{
                mEmptyView.show(null, null);
            }

            refreshLayout.setEnableLoadMore(true);

            mAssetAmountTv.setText(getMyTxsRespDto.getTokenBalance() + " " + assetCode);
            for (GetMyTxsRespDto.TxRecordBean obj : getMyTxsRespDto.getTxRecord()) {

                String txAccountAddress = AddressUtil.anonymous((obj.getOutinType() == 0) ? obj.getToAddress() : obj.getFromAddress());
                String amountStr = null;
                String txStartStr = null;
                if(obj.getOutinType() == 0){
                    amountStr = "-" + CommonUtil.addSuffix(obj.getAmount(),assetCode);
                }else {
                    amountStr = "+" + CommonUtil.addSuffix(obj.getAmount(),assetCode);
                }

                if(TxStatusEnum.SUCCESS.getCode().equals(obj.getTxStatus())){
                    txStartStr = TxStatusEnum.SUCCESS.getName();
                }else{
                    txStartStr = TxStatusEnum.FAIL.getName();
                }

                if(!tokenTxInfoMap.containsKey(obj.getTxHash())){
                    TokenTxInfo tokenTxInfo = new TokenTxInfo(txAccountAddress, TimeUtil.getDateDiff(obj.getTxTime(),getContext()), amountStr, txStartStr);
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
        mMyTokenTxLv.setAdapter(myTokenTxAdapter);
        mMyTokenTxLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TokenTxInfo currentItem = (TokenTxInfo) myTokenTxAdapter.getItem(position);
                Bundle argz = new Bundle();
                argz.putString("txHash", currentItem.getTxHash());
                argz.putInt("outinType",currentItem.getOutinType());
                argz.putString("assetCode",assetCode);
                BPAssetsTxDetailFragment bpAssetsTxDetailFragment = new BPAssetsTxDetailFragment();
                bpAssetsTxDetailFragment.setArguments(argz);
                startFragment(bpAssetsTxDetailFragment);
            }
        });
    }

    private void initData() {
        Bundle bundle = getArguments();
        assetCode = bundle.getString("assetCode");
        issuer = bundle.getString("issuer");
        decimals = bundle.getString("decimals");
        tokenBalance = bundle.get("amount").toString();
        tokenType = bundle.getString("tokenType");
        if(!CommonUtil.isNull(bundle.getString("icon"))){
            mAssetIconIv.setImageBitmap(CommonUtil.base64ToBitmap(bundle.getString("icon")));
        }else{
            mAssetIconIv.setBackgroundResource(R.mipmap.icon_token_default_icon);
        }
        mAssetAmountTv.setText(tokenBalance + " " + assetCode);
        if(!bundle.get("price").toString().equals("~")){
            mAssetValueTv.setText("≈￥" + AmountUtil.amountMultiplyAmount(tokenBalance,bundle.get("price").toString()));
        }else {
            mAssetValueTv.setText("~");
        }
        sharedPreferencesHelper = new SharedPreferencesHelper(getContext(), "buPocket");
        currentAccAddress = sharedPreferencesHelper.getSharedPreference("currentAccAddr", "").toString();
        refreshData();
    }

    private void initTopBar() {
        mTopBar.addLeftImageButton(R.mipmap.icon_tobar_left_arrow, R.id.topbar_left_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        });
        mTopBar.setTitle(assetCode);
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
                argz.putString("tokenCode",assetCode);
                argz.putString("tokenDecimals",decimals);
                argz.putString("tokenIssuer",issuer);
                argz.putString("tokenBalance",tokenBalance);
                BPSendTokenFragment sendTokenFragment = new BPSendTokenFragment();
                sendTokenFragment.setArguments(argz);
                startFragment(sendTokenFragment);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);

        }
    }
}
