package com.bupocket.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.bupocket.R;
import com.bupocket.base.BaseFragment;
import com.bupocket.utils.AddressUtil;
import com.bupocket.utils.SharedPreferencesHelper;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

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

    private SharedPreferencesHelper sharedPreferencesHelper;
    private String currentIdentityWalletName;
    private String currentIdentityWalletAddress;

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
    }

    private void init() {
        initUI();
        setListener();
    }

    private void initData() {
        sharedPreferencesHelper = new SharedPreferencesHelper(getContext(), "buPocket");
        currentIdentityWalletAddress = sharedPreferencesHelper.getSharedPreference("currentAccAddr","").toString();
        currentIdentityWalletName = sharedPreferencesHelper.getSharedPreference("currentIdentityWalletName","Wallet-1").toString();

    }

    private void initUI() {
        QMUIStatusBarHelper.setStatusBarLightMode(getBaseFragmentActivity());
        initCurrentIdentityView();
        initTopBar();
    }

    private void initCurrentIdentityView() {
        mCurrentIdentityWalletNameTv.setText(currentIdentityWalletName);
        mCurrentIdentityWalletAddressTv.setText(AddressUtil.anonymous(currentIdentityWalletAddress));
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
    }
}
