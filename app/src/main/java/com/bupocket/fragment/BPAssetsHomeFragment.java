package com.bupocket.fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.alibaba.fastjson.JSON;
import com.bupocket.R;
import com.bupocket.adaptor.TokensAdapter;
import com.bupocket.base.BaseFragment;
import com.bupocket.fragment.components.AssetsListView;
import com.bupocket.http.api.dto.resp.GetTokensRespDto;
import com.bupocket.utils.AddressUtil;
import com.bupocket.utils.CommonUtil;
import com.bupocket.utils.QRCodeUtil;
import com.bupocket.utils.SharedPreferencesHelper;
import com.qmuiteam.qmui.widget.QMUIEmptyView;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.List;

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

    protected SharedPreferencesHelper sharedPreferencesHelper;
    private TokensAdapter mTokensAdapter;
    private String totalAssets = "~~";
    private String currentAccAddress;
    private String currentAccNick;
    private MaterialHeader mMaterialHeader;
    private static boolean isFirstEnter = true;

    @BindView(R.id.assetsSv)
    ScrollView assetsSv;

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_assets_home, null);
        ButterKnife.bind(this, root);
        initData();
        initTokensView();
        initWalletInfoView();
        setListeners();
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

    private void initWalletInfoView() {
        mUserNick.setText(currentAccNick);
        String shortCurrentAccAddress = AddressUtil.anonymous(currentAccAddress);
        mUserBcAddress.setText(shortCurrentAccAddress);
        mTotalAssetsValueTv.setText(totalAssets);
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
                        initData();
                    }
                }, 1500);

            }
        });
    }

    private void loadAssetList() {
//        TokenService tokenService = RetrofitFactory.getInstance().getRetrofit().create(TokenService.class);
//        List<GetTokensRespDto.TokenListBean> mLocalTokenList = sharedPreferencesHelper.getDataList("tokens");
//        Map<String, Object> parmasMap = new HashMap<>();
//        currentAccAddress = "buQcFKaRBmvmvUCC5WQxwaBQVSbkarBPLCv5";
//        parmasMap.put("address",currentAccAddress);
//        parmasMap.put("tokenList", mLocalTokenList);
//        Call<ApiResult<GetTokensRespDto>> call = tokenService.getTokens(parmasMap);
//        call.enqueue(new Callback<ApiResult<GetTokensRespDto>>() {
//            @Override
//            public void onResponse(Call<ApiResult<GetTokensRespDto>> call, Response<ApiResult<GetTokensRespDto>> response) {
//                mAssetsHomeEmptyView.show(null,null);
//                ApiResult<GetTokensRespDto> respDtoApiResult = response.body();
//                handleTokens(respDtoApiResult.getData());
//            }
//
//            @Override
//            public void onFailure(Call<ApiResult<GetTokensRespDto>> call, Throwable t) {
//                t.printStackTrace();
//                mAssetsHomeEmptyView.show(getResources().getString(R.string.emptyView_mode_desc_fail_title), null);
//            }
//        });
        String jsonStr = "{\"tokenList\":[{\"amount\":\"210.32752256\",\"assetCode\":\"BU\",\"icon\":\"data:image/png;base64,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\",\"issuer\":\"\",\"price\":\"1.4812068\",\"type\":1},{\"amount\":\"0\",\"assetCode\":\"MMDA\",\"icon\":\"data:image/png;base64,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\",\"issuer\":\"buQprdKdbzjrZd5V7hNXucUCzgGtk6kw6kof\",\"price\":\"~\",\"type\":1},{\"amount\":\"7\",\"assetCode\":\"issue\",\"icon\":\"data:image/jpeg;base64,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\",\"issuer\":\"buQnc3AGCo6ycWJCce516MDbPHKjK7ywwkuo\",\"price\":\"~\",\"type\":1},{\"amount\":\"0\",\"assetCode\":\"BTC\",\"icon\":\"\",\"issuer\":\"buQnc3AGCo6ycWJCce516MDbPHKjK7ywwkuo\",\"price\":\"~\",\"type\":1},{\"amount\":\"0\",\"assetCode\":\"HWH\",\"icon\":\"data:image/jpeg;base64,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\",\"issuer\":\"buQnbj8rTiVikekMjE1dFbdszmzravbjx9Wf\",\"price\":\"~\",\"type\":1},{\"amount\":\"805\",\"assetCode\":\"ABC\",\"icon\":\"data:image/jpeg;base64,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\",\"issuer\":\"buQWESXjdgXSFFajEZfkwi5H4fuAyTGgzkje\",\"price\":\"~\",\"type\":1},{\"amount\":\"1\",\"assetCode\":\"QWE\",\"icon\":\"data:image/jpeg;base64,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\",\"issuer\":\"buQZf3Uz8HzjCtZBBwK9ce9gkbj9G4Ew4grT\",\"price\":\"~\",\"type\":1}],\"totalAssets\":\"311.538543701171875\"}";
        handleTokens(JSON.parseObject(jsonStr, GetTokensRespDto.class));
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

            totalAssets = getResources().getString(R.string.prefix_total_asset) + CommonUtil.formatDouble(tokensRespDto.getTotalAssets());
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
                BPWalletHomeFragment bpWalletHomeFragment = new BPWalletHomeFragment();
                bpWalletHomeFragment.setArguments(argz);
                startFragment(bpWalletHomeFragment);
            }
        });

    }

    private void initData() {
        sharedPreferencesHelper = new SharedPreferencesHelper(getContext(), "buPocket");
        currentAccNick = sharedPreferencesHelper.getSharedPreference("currentAccNick", "").toString();
        currentAccAddress = sharedPreferencesHelper.getSharedPreference("currentAccAddr", "").toString();
        assetsSv.smoothScrollTo(0,0);
        refreshData();
    }

    private void refreshData(){
        if(isFirstEnter){
            isFirstEnter = false;
            refreshLayout.autoRefresh();
        }
        loadAssetList();
    }
}
