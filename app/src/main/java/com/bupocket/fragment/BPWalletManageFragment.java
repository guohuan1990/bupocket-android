package com.bupocket.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bupocket.R;
import com.bupocket.base.BaseFragment;
import com.bupocket.utils.AddressUtil;
import com.bupocket.utils.CommonUtil;
import com.bupocket.utils.SharedPreferencesHelper;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BPWalletManageFragment extends BaseFragment {

    @BindView(R.id.topbar)
    QMUITopBar mTopBar;
    @BindView(R.id.walletNameTv)
    TextView mWalletNameTv;
    @BindView(R.id.walletAddressTv)
    TextView mWalletAddressTv;
    @BindView(R.id.deleteWalletBtn)
    QMUIRoundButton mDeleteWalletBtn;

    @BindView(R.id.walletInfoLl)
    LinearLayout mWalletInfoLl;

    private String walletPublicAddress;
    private String walletName;
    private SharedPreferencesHelper sharedPreferencesHelper;

    private Boolean whetherIdentityWallet;

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_wallet_manage, null);
        ButterKnife.bind(this, root);
        init();
        return root;
    }

    private void init() {
        initData();
        initUI();
        setListener();
    }

    private void setListener() {
        mWalletInfoLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final QMUIDialog qmuiDialog = new QMUIDialog(getContext());
                qmuiDialog.setCanceledOnTouchOutside(false);
                qmuiDialog.setContentView(R.layout.change_wallet_name_layout);
                qmuiDialog.show();

                TextView cancelTv = qmuiDialog.findViewById(R.id.changeNameCancel);
                TextView confirmTv = qmuiDialog.findViewById(R.id.changeNameConfirm);


                cancelTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        qmuiDialog.dismiss();
                    }
                });

                confirmTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText walletNewNameEt = qmuiDialog.findViewById(R.id.walletNewNameEt);
                        String walletNewName = walletNewNameEt.getText().toString().trim();
                        if(CommonUtil.isNull(walletNewName)){
                            Toast.makeText(getActivity(), R.string.error_wallet_name_empty_message_txt, Toast.LENGTH_SHORT).show();
                        }
                        if(whetherIdentityWallet){
                            sharedPreferencesHelper.put("currentIdentityWalletName",walletNewName);
                            refreshIdentityWalletName();
                        }

                        qmuiDialog.dismiss();
                    }
                });
            }
        });
    }

    private void initData() {
        Bundle argz = getArguments();
        walletPublicAddress = argz.getString("walletPublicAddress");
        sharedPreferencesHelper = new SharedPreferencesHelper(getContext(), "buPocket");
        if(sharedPreferencesHelper.getSharedPreference("currentAccAddr","").toString().equals(walletPublicAddress)){
            walletName = sharedPreferencesHelper.getSharedPreference("currentIdentityWalletName","Wallet-1").toString();
            whetherIdentityWallet = true;
        }
    }

    private void initUI() {
        initTopBar();
        initView();
    }

    private void initView() {
        mWalletNameTv.setText(walletName);
        mWalletAddressTv.setText(AddressUtil.anonymous(walletPublicAddress));
        if(whetherIdentityWallet){
            mDeleteWalletBtn.setVisibility(View.GONE);
        }
    }

    private void refreshIdentityWalletName() {
        walletName = sharedPreferencesHelper.getSharedPreference("currentIdentityWalletName","Wallet-1").toString();
        mWalletNameTv.setText(walletName);
    }

    private void initTopBar() {
        mTopBar.addLeftImageButton(R.mipmap.icon_tobar_left_arrow, R.id.topbar_left_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        });
        mTopBar.setTitle(getString(R.string.manage_txt));
    }
}
