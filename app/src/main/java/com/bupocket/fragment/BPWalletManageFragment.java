package com.bupocket.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bupocket.R;
import com.bupocket.base.BaseFragment;
import com.bupocket.utils.AddressUtil;
import com.bupocket.utils.CommonUtil;
import com.bupocket.utils.SharedPreferencesHelper;
import com.bupocket.wallet.Wallet;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
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
    @BindView(R.id.exportKeystoreRl)
    RelativeLayout mExportKeystoreRl;
    @BindView(R.id.exportPrivateRl)
    RelativeLayout mExportPrivateRl;

    private String walletAddress;
    private String walletName;
    private String keystoreStr;
    private String privateKeyStr;
    private SharedPreferencesHelper sharedPreferencesHelper;

    private Boolean whetherIdentityWallet;

    public QMUITipDialog exportingTipDialog;

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

        mExportKeystoreRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final QMUIDialog qmuiDialog = new QMUIDialog(getContext());
                qmuiDialog.setCanceledOnTouchOutside(false);
                qmuiDialog.setContentView(R.layout.password_comfirm_layout);
                qmuiDialog.show();
                QMUIRoundButton mPasswordConfirmBtn = qmuiDialog.findViewById(R.id.passwordConfirmBtn);
                ImageView mPasswordConfirmCloseBtn = qmuiDialog.findViewById(R.id.passwordConfirmCloseBtn);
                TextView mPasswordConfirmNotice = qmuiDialog.findViewById(R.id.passwordConfirmNotice);
                TextView mPasswordConfirmTitle = qmuiDialog.findViewById(R.id.passwordConfirmTitle);

                mPasswordConfirmNotice.setText(getString(R.string.export_keystore_password_confirm_txt));
                mPasswordConfirmTitle.setText(getString(R.string.password_comfirm_dialog_title));

                mPasswordConfirmBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText mPasswordConfirmEt = qmuiDialog.findViewById(R.id.passwordConfirmEt);
                        final String password = mPasswordConfirmEt.getText().toString().trim();

                        exportingTipDialog = new QMUITipDialog.Builder(getContext())
                                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                                .setTipWord(getResources().getString(R.string.send_tx_handleing_txt))
                                .create();
                        exportingTipDialog.show();
                        exportingTipDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                            @Override
                            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

                                if(event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
                                    return true;
                                }
                                return false;
                            }
                        });

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    String bpData = getAccountBPData();
                                    keystoreStr = Wallet.getInstance().exportKeyStore(password,bpData,walletAddress);
                                    exportingTipDialog.dismiss();
                                    Bundle argz = new Bundle();
                                    argz.putString("keystoreStr",keystoreStr);
                                    BPWalletExportKeystoreFragment bpWalletExportKeystoreFragment = new BPWalletExportKeystoreFragment();
                                    bpWalletExportKeystoreFragment.setArguments(argz);
                                    startFragment(bpWalletExportKeystoreFragment);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Looper.prepare();
                                    Toast.makeText(getActivity(), R.string.checking_password_error, Toast.LENGTH_SHORT).show();
                                    exportingTipDialog.dismiss();
                                    Looper.loop();
                                }
                            }
                        }).start();
                        qmuiDialog.dismiss();
                    }
                });

                mPasswordConfirmCloseBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        qmuiDialog.dismiss();
                    }
                });

            }
        });

        mExportPrivateRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final QMUIDialog qmuiDialog = new QMUIDialog(getContext());
                qmuiDialog.setCanceledOnTouchOutside(false);
                qmuiDialog.setContentView(R.layout.password_comfirm_layout);
                qmuiDialog.show();
                QMUIRoundButton mPasswordConfirmBtn = qmuiDialog.findViewById(R.id.passwordConfirmBtn);
                ImageView mPasswordConfirmCloseBtn = qmuiDialog.findViewById(R.id.passwordConfirmCloseBtn);
                TextView mPasswordConfirmNotice = qmuiDialog.findViewById(R.id.passwordConfirmNotice);
                TextView mPasswordConfirmTitle = qmuiDialog.findViewById(R.id.passwordConfirmTitle);

                mPasswordConfirmNotice.setText(getString(R.string.export_private_password_confirm_txt));
                mPasswordConfirmTitle.setText(getString(R.string.password_comfirm_dialog_title));

                mPasswordConfirmBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText mPasswordConfirmEt = qmuiDialog.findViewById(R.id.passwordConfirmEt);
                        final String password = mPasswordConfirmEt.getText().toString().trim();

                        exportingTipDialog = new QMUITipDialog.Builder(getContext())
                                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                                .setTipWord(getResources().getString(R.string.send_tx_handleing_txt))
                                .create();
                        exportingTipDialog.show();
                        exportingTipDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                            @Override
                            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

                                if(event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
                                    return true;
                                }
                                return false;
                            }
                        });

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                String bpData = getAccountBPData();
                                try {
                                    privateKeyStr = Wallet.getInstance().exportPrivateKey(password,bpData,walletAddress);
                                    exportingTipDialog.dismiss();
                                    Bundle argz = new Bundle();
                                    argz.putString("address",walletAddress);
                                    argz.putString("privateKey",privateKeyStr);
                                    BPWalletExportPrivateFragment bpWalletExportPrivateFragment = new BPWalletExportPrivateFragment();
                                    bpWalletExportPrivateFragment.setArguments(argz);
                                    startFragment(bpWalletExportPrivateFragment);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Looper.prepare();
                                    Toast.makeText(getActivity(), R.string.checking_password_error, Toast.LENGTH_SHORT).show();
                                    exportingTipDialog.dismiss();
                                    Looper.loop();
                                }
                            }
                        }).start();

                        qmuiDialog.dismiss();
                    }
                });

                mPasswordConfirmCloseBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        qmuiDialog.dismiss();
                    }
                });
            }
        });
    }

    private String getAccountBPData() {
        String accountBPData = null;
        if(whetherIdentityWallet) {
            accountBPData = sharedPreferencesHelper.getSharedPreference("BPData", "").toString();
        }else {
            accountBPData = sharedPreferencesHelper.getSharedPreference(walletAddress+ "-BPdata", "").toString();
        }
        return accountBPData;
    }

    private void initData() {
        Bundle argz = getArguments();
        walletAddress = argz.getString("walletAddress");
        sharedPreferencesHelper = new SharedPreferencesHelper(getContext(), "buPocket");
        if(sharedPreferencesHelper.getSharedPreference("currentAccAddr","").toString().equals(walletAddress)){
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
        mWalletAddressTv.setText(AddressUtil.anonymous(walletAddress));
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
