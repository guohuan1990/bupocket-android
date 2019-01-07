package com.bupocket.fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.session.MediaSession;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bupocket.R;
import com.bupocket.activity.CaptureActivity;
import com.bupocket.adaptor.TokensAdapter;
import com.bupocket.base.BaseFragment;
import com.bupocket.common.Constants;
import com.bupocket.enums.BackupTipsStateEnum;
import com.bupocket.enums.BumoNodeEnum;
import com.bupocket.enums.CurrencyTypeEnum;
import com.bupocket.enums.MnemonicWordBackupStateEnum;
import com.bupocket.enums.TokenActionTypeEnum;
import com.bupocket.fragment.components.AssetsListView;
import com.bupocket.http.api.RetrofitFactory;
import com.bupocket.http.api.TokenService;
import com.bupocket.http.api.dto.resp.ApiResult;
import com.bupocket.http.api.dto.resp.GetTokensRespDto;
import com.bupocket.model.IssueTokenInfo;
import com.bupocket.model.RegisterTokenInfo;
import com.bupocket.utils.AddressUtil;
import com.bupocket.utils.CommonUtil;
import com.bupocket.utils.QRCodeUtil;
import com.bupocket.utils.SharedPreferencesHelper;
import com.bupocket.wallet.Wallet;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUIEmptyView;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import io.bumo.encryption.key.PublicKey;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BPAssetsHomeFragment extends BaseFragment {

    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;
    @BindView(R.id.assetsHomeEmptyView)
    QMUIEmptyView mAssetsHomeEmptyView;
    @BindView(R.id.tokenListLv)
    AssetsListView mTokenListLv;
    @BindString(R.string.qr_copy_success_message)
    String copySuccessMessage;
    @BindView(R.id.totalAssetsValueTv)
    TextView mTotalAssetsValueTv;
    @BindView(R.id.assetsSv)
    ScrollView assetsSv;
    @BindView(R.id.currencyTypeTv)
    TextView mCurrencyTypeTv;
    @BindView(R.id.currentTestNetTipsTv)
    TextView mCurrentTestNetTipsTv;
    @BindView(R.id.assetLinearLayout)
    LinearLayout mAssetLinearLayout;
    @BindView(R.id.homeScanLl)
    LinearLayout mHomeScanLl;
    @BindView(R.id.receiveLl)
    LinearLayout mReceiveLl;
    @BindView(R.id.addTokenLl)
    LinearLayout mAddTokenLl;
    @BindView(R.id.immediatelyBackupBtn)
    QMUIRoundButton mImmediatelyBackupBtn;
    @BindView(R.id.notBackupBtn)
    QMUIRoundButton mNotBackupBtn;
    @BindView(R.id.safetyTipsLl)
    LinearLayout mSafetyTipsLl;

    protected SharedPreferencesHelper sharedPreferencesHelper;
    private TokensAdapter mTokensAdapter;
    private String currentAccAddress;
    private String currentAccNick;
    private MaterialHeader mMaterialHeader;
    List<GetTokensRespDto.TokenListBean> mLocalTokenList = new ArrayList<>();

    private String tokenBalance;
    private String currencyType;
    private Integer bumoNodeType;
    private String localTokenListSharedPreferenceKey;

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_assets_home, null);
        ButterKnife.bind(this, root);
        initData();
        initWalletInfoView();
        setListeners();
        backupState();
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        initBackground();
    }

    private void setListeners() {
        mHomeScanLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startScan();
            }
        });
        mReceiveLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAccountAddressView();
            }
        });
        mAddTokenLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFragment(new BPAssetsAddFragment());
            }
        });
        mImmediatelyBackupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle argz = new Bundle();
                argz.putString("accName",currentAccNick);
                BPUserInfoFragment bpUserInfoFragment = new BPUserInfoFragment();
                bpUserInfoFragment.setArguments(argz);
                startFragment(bpUserInfoFragment);
            }
        });
        mNotBackupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSafetyTipsLl.setVisibility(View.GONE);
                sharedPreferencesHelper.put("backupTipsState",BackupTipsStateEnum.HIDE.getCode());
            }
        });
    }

    private void backupState() {
        String backupState = sharedPreferencesHelper.getSharedPreference("mnemonicWordBackupState","").toString();
        String backupTipsState = sharedPreferencesHelper.getSharedPreference("backupTipsState","").toString();
        if(MnemonicWordBackupStateEnum.ALREADYBACKUP.getCode().equals(backupState) || BackupTipsStateEnum.HIDE.getCode().equals(backupTipsState)){
            mSafetyTipsLl.setVisibility(View.GONE);
        }
    }

    private void showAccountAddressView() {
        final QMUIBottomSheet qmuiBottomSheet = new QMUIBottomSheet(getContext());
        qmuiBottomSheet.setContentView(qmuiBottomSheet.getLayoutInflater().inflate(R.layout.show_address_layout,null));
        TextView accountAddressTv = qmuiBottomSheet.findViewById(R.id.printAccAddressTv);
        accountAddressTv.setText(currentAccAddress);

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

    private Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            mTotalAssetsValueTv.setText("≈" + msg.getData().get("assetValuation").toString());
        };
    };

    private void initWalletInfoView() {
        String shortCurrentAccAddress = AddressUtil.anonymous(currentAccAddress);
    }

    private void initTokensView() {
        mMaterialHeader = (MaterialHeader)refreshLayout.getRefreshHeader();

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(final RefreshLayout refreshlayout) {
                refreshlayout.getLayout().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshData();
                        refreshlayout.finishRefresh();
                        refreshlayout.setNoMoreData(false);
                    }
                }, 400);

            }
        });
        mCurrencyTypeTv.setText(getCurrencyTypeSymbol(currencyType));
    }

    private String getCurrencyTypeSymbol(String currencyType) {
        for(CurrencyTypeEnum currencyTypeEnum : CurrencyTypeEnum.values()){
            if(currencyTypeEnum.getName().equals(currencyType)){
                return currencyTypeEnum.getSymbol();
            }
        }
        return null;
    }

    private void loadAssetList() {
        tokenBalance = sharedPreferencesHelper.getSharedPreference("tokenBalance","0").toString();
        Runnable getBalanceRunnable = new Runnable() {
            @Override
            public void run() {
                tokenBalance = Wallet.getInstance().getAccountBUBalance(currentAccAddress);
                if(!CommonUtil.isNull(tokenBalance)){
                    sharedPreferencesHelper.put("tokenBalance",tokenBalance);
                }
            }
        };
        new Thread(getBalanceRunnable).start();


        TokenService tokenService = RetrofitFactory.getInstance().getRetrofit().create(TokenService.class);
        if(BumoNodeEnum.TEST.getCode() == bumoNodeType){
            localTokenListSharedPreferenceKey = BumoNodeEnum.TEST.getLocalTokenListSharedPreferenceKey();
        }else if(BumoNodeEnum.MAIN.getCode() == bumoNodeType){
            localTokenListSharedPreferenceKey = BumoNodeEnum.MAIN.getLocalTokenListSharedPreferenceKey();
        }
        GetTokensRespDto getTokensRespDto = JSON.parseObject(sharedPreferencesHelper.getSharedPreference(localTokenListSharedPreferenceKey, "").toString(), GetTokensRespDto.class);
        if(getTokensRespDto != null){
            mLocalTokenList = getTokensRespDto.getTokenList();
        }
        String currencyType = sharedPreferencesHelper.getSharedPreference("currencyType","CNY").toString();
        Map<String, Object> parmasMap = new HashMap<>();
        parmasMap.put("address",currentAccAddress);
        parmasMap.put("currencyType",currencyType);
        parmasMap.put("tokenList", mLocalTokenList);
        Call<ApiResult<GetTokensRespDto>> call = tokenService.getTokens(parmasMap);
        call.enqueue(new Callback<ApiResult<GetTokensRespDto>>() {
            @Override
            public void onResponse(Call<ApiResult<GetTokensRespDto>> call, Response<ApiResult<GetTokensRespDto>> response) {
                mAssetsHomeEmptyView.show(null,null);
                ApiResult<GetTokensRespDto> respDtoApiResult = response.body();

                if(respDtoApiResult != null){
                    sharedPreferencesHelper.put("tokensInfoCache", JSON.toJSONString(respDtoApiResult.getData()));
                    if(isAdded()){
                        handleTokens(respDtoApiResult.getData());
                    }
                }else {
                    mAssetsHomeEmptyView.show(getResources().getString(R.string.emptyView_mode_desc_no_data), null);
                }
            }

            @Override
            public void onFailure(Call<ApiResult<GetTokensRespDto>> call, Throwable t) {
                t.printStackTrace();
                if(isAdded()){
                    mAssetsHomeEmptyView.show(getResources().getString(R.string.emptyView_mode_desc_fail_title), null);
                }
            }
        });
    }

    private void handleTokens(GetTokensRespDto tokensRespDto) {
        List<GetTokensRespDto.TokenListBean> mTokenList;
        if(tokensRespDto != null){
            if(tokensRespDto.getTokenList() == null || tokensRespDto.getTokenList().size() == 0){
                mAssetsHomeEmptyView.show(getResources().getString(R.string.emptyView_mode_desc_no_data), null);
                return;
            }else{
                mAssetsHomeEmptyView.show("","");
            }

            Message msg = Message.obtain();
            Bundle data = new Bundle();
            data.putString("assetValuation", tokensRespDto.getTotalAmount());
            msg.setData(data);
            handler.sendMessage(msg);
            assetsSv.smoothScrollTo(0,0);
            mTokenList = tokensRespDto.getTokenList();

        }else{
            mAssetsHomeEmptyView.show(getResources().getString(R.string.emptyView_mode_desc_fail_title), null);
            return;
        }

        mTokensAdapter = new TokensAdapter(mTokenList,getContext());
        mTokenListLv.setAdapter(mTokensAdapter);
        mTokenListLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle argz = new Bundle();
                GetTokensRespDto.TokenListBean tokenInfo = (GetTokensRespDto.TokenListBean) mTokensAdapter.getItem(position);
                argz.putString("assetCode",tokenInfo.getAssetCode());
                argz.putString("icon",tokenInfo.getIcon());
                argz.putString("amount",tokenInfo.getAmount());
                argz.putString("price",tokenInfo.getPrice());
                argz.putString("issuer",tokenInfo.getIssuer());
                argz.putString("decimals",tokenInfo.getDecimals()+"");
                argz.putString("tokenType",tokenInfo.getType()+"");
                argz.putString("assetAmount",tokenInfo.getAssetAmount());
                BPAssetsDetailFragment bpAssetsDetailFragment = new BPAssetsDetailFragment();
                bpAssetsDetailFragment.setArguments(argz);
                startFragment(bpAssetsDetailFragment);
            }
        });

    }

    private void initData() {
        QMUIStatusBarHelper.setStatusBarDarkMode(getBaseFragmentActivity());
//        QMUIStatusBarHelper.translucent(getActivity());
        sharedPreferencesHelper = new SharedPreferencesHelper(getContext(), "buPocket");
        currentAccNick = sharedPreferencesHelper.getSharedPreference("currentAccNick", "").toString();
        currentAccAddress = sharedPreferencesHelper.getSharedPreference("currentAccAddr", "").toString();
        currencyType = sharedPreferencesHelper.getSharedPreference("currencyType","CNY").toString();
        bumoNodeType = sharedPreferencesHelper.getInt("bumoNode",Constants.DEFAULT_BUMO_NODE);
        GetTokensRespDto tokensCache = JSON.parseObject(sharedPreferencesHelper.getSharedPreference("tokensInfoCache", "").toString(), GetTokensRespDto.class);
        if(tokensCache != null){
            handleTokens(tokensCache);
        }
//        initBackground();
        initTokensView();
        refreshLayout.autoRefresh();
    }

    private void initBackground() {
        if(SharedPreferencesHelper.getInstance().getInt("bumoNode",Constants.DEFAULT_BUMO_NODE)== BumoNodeEnum.TEST.getCode()){
            mCurrentTestNetTipsTv.setText(getString(R.string.current_test_message_txt));
            mAssetLinearLayout.setBackgroundResource(R.drawable.bg_asset_home_test_net);
        }
    }

    private void refreshData(){
        loadAssetList();
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
                if(!PublicKey.isAddressValid(result.getContents())){
                    if(CommonUtil.checkIsBase64(result.getContents())){
                        String jsonStr = null;
                        try {
                            jsonStr = new String(Base64.decode(result.getContents().getBytes("UTF-8"), Base64.DEFAULT));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        Object object = JSON.parseObject(jsonStr);
                        String action = ((JSONObject) object).getString("action");
                        String uuID = ((JSONObject) object).getString("uuID");
                        String tokenData = ((JSONObject) object).getString("data");
                        Bundle argz = new Bundle();
                        argz.putString("uuID",uuID);
                        argz.putString("tokenData",tokenData);
                        argz.putString("buBalance",tokenBalance);
                        if(action.equals(TokenActionTypeEnum.ISSUE.getCode())){
                            BPIssueTokenFragment bpIssueTokenFragment = new BPIssueTokenFragment();
                            bpIssueTokenFragment.setArguments(argz);
                            startFragment(bpIssueTokenFragment);
                        }else if(action.equals(TokenActionTypeEnum.REGISTER.getCode())){
                            BPRegisterTokenFragment bpRegisterTokenFragment = new BPRegisterTokenFragment();
                            bpRegisterTokenFragment.setArguments(argz);
                            startFragment(bpRegisterTokenFragment);
                        }
                    }else {
                        Toast.makeText(getActivity(), R.string.error_qr_message_txt, Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Bundle argz = new Bundle();
                    argz.putString("destAddress",result.getContents());
                    argz.putString("tokenCode","BU");
                    argz.putString("tokenDecimals","8");
                    argz.putString("tokenIssuer","");
                    argz.putString("tokenBalance",tokenBalance);
                    argz.putString("tokenType","0");
                    BPSendTokenFragment sendTokenFragment = new BPSendTokenFragment();
                    sendTokenFragment.setArguments(argz);
                    startFragment(sendTokenFragment);
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
