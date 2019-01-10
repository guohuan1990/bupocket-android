package com.bupocket.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bupocket.R;
import com.bupocket.base.BaseFragment;
import com.bupocket.utils.AddressUtil;
import com.bupocket.utils.SharedPreferencesHelper;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BPWalletsHomeFragment extends BaseFragment {

    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.currentIdentityWalletNameTv)
    TextView mCurrentIdentityWalletNameTv;
    @BindView(R.id.currentIdentityWalletAddressTv)
    TextView mCurrentIdentityWalletAddressTv;
    @BindView(R.id.manageIdentityWalletBtn)
    QMUIRoundButton mManageIdentityWalletBtn;
    @BindView(R.id.currentIdentityWalletSignTv)
    TextView mCurrentIdentityWalletSignTv;
    @BindView(R.id.importSmallWalletBtnIv)
    ImageView mImportSmallWalletBtnIv;
    @BindView(R.id.importBigWalletBtn)
    QMUIRoundButton mImportBigWalletBtn;
    @BindView(R.id.importWalletsLv)
    ListView mImportWalletsLv;

    private SharedPreferencesHelper sharedPreferencesHelper;
    private String currentIdentityWalletName;
    private String currentIdentityWalletAddress;
    private String currentWalletAddress;
    private List<String> importedWallets = new ArrayList<>();

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_wallets_home, null);
        ButterKnife.bind(this, root);
        init();
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        initData();
        initUI();
        setListener();
    }

    private void init() {
        initTopBar();
    }

    private void initData() {
        sharedPreferencesHelper = new SharedPreferencesHelper(getContext(), "buPocket");
        currentIdentityWalletAddress = sharedPreferencesHelper.getSharedPreference("currentAccAddr","").toString();
        currentIdentityWalletName = sharedPreferencesHelper.getSharedPreference("currentIdentityWalletName","Wallet-1").toString();
        currentWalletAddress = sharedPreferencesHelper.getSharedPreference("currentWalletAddress","").toString();
        importedWallets = JSONObject.parseArray(sharedPreferencesHelper.getSharedPreference("importedWallets","").toString(),String.class);
    }

    private void initUI() {
        QMUIStatusBarHelper.setStatusBarLightMode(getBaseFragmentActivity());
        initCurrentIdentityView();
        initImportedWalletView();
    }

    private void initImportedWalletView() {
        if(importedWallets == null || importedWallets.size() == 0){
            mImportSmallWalletBtnIv.setVisibility(View.GONE);
            mImportWalletsLv.setVisibility(View.GONE);
            mImportBigWalletBtn.setVisibility(View.VISIBLE);
        }else{

        }
    }

    private void initCurrentIdentityView() {
        mCurrentIdentityWalletNameTv.setText(currentIdentityWalletName);
        mCurrentIdentityWalletAddressTv.setText(AddressUtil.anonymous(currentIdentityWalletAddress));
        if(currentWalletAddress.equals(currentIdentityWalletAddress)){
            mCurrentIdentityWalletSignTv.setVisibility(View.VISIBLE);
        }
    }

    private void initTopBar() {
        mTopBar.setBackgroundDividerEnabled(false);
        mTopBar.addLeftImageButton(R.mipmap.icon_tobar_left_arrow, R.id.topbar_left_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        });
    }

    private void setListener() {
        mManageIdentityWalletBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle argz = new Bundle();
                argz.putString("walletPublicAddress",currentIdentityWalletAddress);
                BPWalletManageFragment bpWalletManageFragment = new BPWalletManageFragment();
                bpWalletManageFragment.setArguments(argz);
                startFragment(bpWalletManageFragment);
            }
        });

        mImportSmallWalletBtnIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                importWallet();
            }
        });

        mImportBigWalletBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                importWallet();
            }
        });
    }

    private void importWallet() {
        startFragment(new BPWalletImportFragment());
    }
}
