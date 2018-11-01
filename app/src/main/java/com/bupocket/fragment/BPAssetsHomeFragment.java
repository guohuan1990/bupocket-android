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
    @BindView(R.id.addTokenIv)
    ImageView mAddTokenIv;
    @BindView(R.id.userNick)
    TextView mUserNick;
    @BindView(R.id.assetBackupWalletBtn)
    QMUIRoundButton mAssetBackupWalletBtn;
    @BindView(R.id.showMyAddressLv)
    LinearLayout mShowMyAddressLv;
    @BindString(R.string.qr_copy_success_message)
    String copySuccessMessage;
    @BindView(R.id.userBcAddress)
    TextView mUserBcAddress;
    @BindView(R.id.totalAssetsValueTv)
    TextView mTotalAssetsValueTv;
    @BindView(R.id.assetsAvatarIv)
    QMUIRadiusImageView mAssetsAvatarIv;
    @BindView(R.id.userNickAndBackupBtnLt)
    LinearLayout mUserNickAndBackupBtnLt;
    @BindView(R.id.homeScanBtn)
    ImageView mHomeScanBtn;

    protected SharedPreferencesHelper sharedPreferencesHelper;
    private TokensAdapter mTokensAdapter;
    private String totalAssets = "~~";
    private String currentAccAddress;
    private String currentAccNick;
    private MaterialHeader mMaterialHeader;
    private static boolean isFirstEnter = true;
    List<GetTokensRespDto.TokenListBean> mLocalTokenList = new ArrayList<>();

    private String assetCode = "BU";
    private String decimals = "8";
    private String issuer = "";
    private String tokenBalance;
    private String tokenType = "0";

    @BindView(R.id.assetsSv)
    ScrollView assetsSv;

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

    private void setListeners() {
        mAddTokenIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFragment(new BPAssetsAddFragment());
            }
        });
        mAssetBackupWalletBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle argz = new Bundle();
                argz.putString("accName",currentAccNick);
                BPUserInfoFragment bpUserInfoFragment = new BPUserInfoFragment();
                bpUserInfoFragment.setArguments(argz);
                startFragment(bpUserInfoFragment);
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
        mShowMyAddressLv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAccountAddressView();
            }
        });
        mHomeScanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startScan();
            }
        });
    }

    private void backupState() {
        String state = sharedPreferencesHelper.getSharedPreference("mnemonicWordBackupState","").toString();
        if(state.equals("0")){
            mUserNickAndBackupBtnLt.removeView(mAssetBackupWalletBtn);
        }
    }

    private void showAccountAddressView() {
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

    private Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            mTotalAssetsValueTv.setText("≈" + msg.getData().get("assetValuation").toString());
        };
    };

    private void initWalletInfoView() {
        mUserNick.setText(currentAccNick);
        String shortCurrentAccAddress = AddressUtil.anonymous(currentAccAddress);
        mUserBcAddress.setText(shortCurrentAccAddress);

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
//                        initData();
                    }
                }, 400);

            }
        });
    }

    private void loadAssetList() {
        tokenBalance = sharedPreferencesHelper.getSharedPreference("tokenBalance","0").toString();
        Runnable getBalanceRunnable = new Runnable() {
            @Override
            public void run() {
                tokenBalance = Wallet.getInstance().getAccountBUBalance(currentAccAddress);
                sharedPreferencesHelper.put("tokenBalance",tokenBalance);
            }
        };
        new Thread(getBalanceRunnable).start();


        TokenService tokenService = RetrofitFactory.getInstance().getRetrofit().create(TokenService.class);
        if(JSON.parseObject(sharedPreferencesHelper.getSharedPreference("myTokens", "").toString(), GetTokensRespDto.class) != null){
            mLocalTokenList = JSON.parseObject(sharedPreferencesHelper.getSharedPreference("myTokens", "").toString(), GetTokensRespDto.class).getTokenList();
        }
        Map<String, Object> parmasMap = new HashMap<>();
        parmasMap.put("address",currentAccAddress);
        parmasMap.put("tokenList", mLocalTokenList);
        Call<ApiResult<GetTokensRespDto>> call = tokenService.getTokens(parmasMap);
        call.enqueue(new Callback<ApiResult<GetTokensRespDto>>() {
            @Override
            public void onResponse(Call<ApiResult<GetTokensRespDto>> call, Response<ApiResult<GetTokensRespDto>> response) {
                mAssetsHomeEmptyView.show(null,null);
                ApiResult<GetTokensRespDto> respDtoApiResult = response.body();
                // 更新缓存
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

            totalAssets = getResources().getString(R.string.prefix_total_asset) + CommonUtil.formatDouble(tokensRespDto.getTotalAmount());
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
        sharedPreferencesHelper = new SharedPreferencesHelper(getContext(), "buPocket");
        currentAccNick = sharedPreferencesHelper.getSharedPreference("currentAccNick", "").toString();
        currentAccAddress = sharedPreferencesHelper.getSharedPreference("currentAccAddr", "").toString();

        GetTokensRespDto tokensCache = JSON.parseObject(sharedPreferencesHelper.getSharedPreference("tokensInfoCache", "").toString(), GetTokensRespDto.class);
        if(tokensCache != null){
            handleTokens(tokensCache);
        }

        initTokensView();
        refreshLayout.autoRefresh();
//        refreshData();
    }

    private void refreshData(){
//        if(isFirstEnter){
//            isFirstEnter = false;
//            initTokensView();
//            refreshLayout.autoRefresh();
//        }
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
                if(!result.getContents().substring(0,2).equals("bu")){
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
                    argz.putString("tokenCode",assetCode);
                    argz.putString("tokenDecimals",decimals);
                    argz.putString("tokenIssuer",issuer);
                    argz.putString("tokenBalance",tokenBalance);
                    argz.putString("tokenType",tokenType);
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
