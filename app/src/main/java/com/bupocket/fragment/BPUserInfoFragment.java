package com.bupocket.fragment;

import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bupocket.R;
import com.bupocket.base.BaseFragment;
import com.bupocket.utils.CommonUtil;
import com.bupocket.utils.SharedPreferencesHelper;
import com.bupocket.wallet.Wallet;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.QMUIWindowInsetLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import org.bitcoinj.crypto.MnemonicCode;

import java.util.ArrayList;
import java.util.List;

import static com.bupocket.R.string.user_info_logout_notice;

public class BPUserInfoFragment extends BaseFragment {

    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;

    @BindView(R.id.userInfoBackupWalletTv)
    TextView mUserInfoBackupWalletTv;

    @BindView(R.id.userInfoLogoutWalletTv)
    TextView mUserInfoLogoutWalletTv;

    @BindView(R.id.userInfoAccNameTv)
    TextView mUserInfoAccNameTv;

    @BindView(R.id.identityIdTv)
    TextView mIdentityIdTv;

    @BindView(R.id.testLayout)
    QMUIWindowInsetLayout mTestLayout;

    private int mCurrentDialogStyle = com.qmuiteam.qmui.R.style.QMUI_Dialog;
    private SharedPreferencesHelper sharedPreferencesHelper;
    private List<String> mnemonicCodeList;

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_user_info, null);
        ButterKnife.bind(this, root);
        QMUIStatusBarHelper.setStatusBarLightMode(getBaseFragmentActivity());
        initTopBar();
        initData();
        eventListeners();
        return root;
    }

    private void initData() {
        sharedPreferencesHelper = new SharedPreferencesHelper(getContext(), "buPocket");
        String identityId = sharedPreferencesHelper.getSharedPreference("identityId", "").toString();
        String accName = getArguments().getString("accName");
        mUserInfoAccNameTv.setText(accName);
        mIdentityIdTv.setText(identityId);
    }

    private void eventListeners() {
        mUserInfoBackupWalletTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                startFragment(new BPBackupWalletFragment());
                final QMUIDialog qmuiDialog = new QMUIDialog(getContext());
                qmuiDialog.setCanceledOnTouchOutside(false);
                qmuiDialog.setContentView(R.layout.password_comfirm_layout);
                qmuiDialog.show();
                QMUIRoundButton mPasswordConfirmBtn = qmuiDialog.findViewById(R.id.passwordConfirmBtn);

                ImageView mPasswordConfirmCloseBtn = qmuiDialog.findViewById(R.id.passwordConfirmCloseBtn);
                TextView mPasswordConfirmNotice = qmuiDialog.findViewById(R.id.passwordConfirmNotice);
                mPasswordConfirmNotice.setText(R.string.user_info_logout_warning);

                mPasswordConfirmCloseBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        qmuiDialog.dismiss();
                    }
                });

                mPasswordConfirmBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 检查合法性
                        EditText mPasswordConfirmEt = qmuiDialog.findViewById(R.id.passwordConfirmEt);
                        final String password = mPasswordConfirmEt.getText().toString().trim();
                        final QMUITipDialog tipDialog = new QMUITipDialog.Builder(getContext())
                                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                                .setTipWord(getResources().getString(R.string.user_info_backup_loading))
                                .create();
                        tipDialog.show();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                String ciphertextSkeyData = getSkeyStr();
                                try {
                                    byte[] skeyByte = Wallet.getInstance().getSkey(password,ciphertextSkeyData);
                                    mnemonicCodeList = new MnemonicCode().toMnemonic(skeyByte);
                                    tipDialog.dismiss();

                                    go2BPCreateWalletShowMneonicCodeFragment();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Looper.prepare();
                                    Toast.makeText(getActivity(), R.string.checking_password_error, Toast.LENGTH_SHORT).show();
                                    tipDialog.dismiss();
                                    Looper.loop();
                                    return;
                                }
                            }
                        }).start();
                        qmuiDialog.dismiss();

                    }
                });
            }
        });

        mUserInfoLogoutWalletTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMessagePositiveDialog();
            }
        });
    }

    private void go2BPCreateWalletShowMneonicCodeFragment(){
        BPCreateWalletShowMneonicCodeFragment createWalletShowMneonicCodeFragment = new BPCreateWalletShowMneonicCodeFragment();
        Bundle argz = new Bundle();
        argz.putStringArrayList("mneonicCodeList", (ArrayList<String>) mnemonicCodeList);
        createWalletShowMneonicCodeFragment.setArguments(argz);
        startFragment(createWalletShowMneonicCodeFragment);

    }

    private String getSkeyStr(){
        return sharedPreferencesHelper.getSharedPreference("skey","").toString();
    }

    private void initTopBar() {
        mTopBar.setBackgroundDividerEnabled(false);
        mTopBar.addLeftImageButton(R.mipmap.icon_tobar_left_arrow, R.id.topbar_left_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QMUIStatusBarHelper.setStatusBarDarkMode(getBaseFragmentActivity());
                popBackStack();
            }
        });
    }

    private void showMessagePositiveDialog() {
        new QMUIDialog.MessageDialogBuilder(getActivity())
                .setTitle(R.string.user_info_logout)
                .setMessage(user_info_logout_notice)
                .addAction(R.string.common_dialog_cancel, new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                })
                .addAction(R.string.common_dialog_confirm, new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                        showPasswordComfirmDialog();
                    }
                })
                .create(mCurrentDialogStyle).show();
    }

    private void showPasswordComfirmDialog() {
        final QMUIDialog qmuiDialog = new QMUIDialog(getContext());
        qmuiDialog.setCanceledOnTouchOutside(false);
        qmuiDialog.setContentView(R.layout.password_comfirm_layout);
        qmuiDialog.show();
        QMUIRoundButton mPasswordConfirmBtn = qmuiDialog.findViewById(R.id.passwordConfirmBtn);
        ImageView mPasswordConfirmCloseBtn = qmuiDialog.findViewById(R.id.passwordConfirmCloseBtn);
        TextView mPasswordConfirmNotice = qmuiDialog.findViewById(R.id.passwordConfirmNotice);
        TextView mPpasswordConfirmTitle = qmuiDialog.findViewById(R.id.passwordConfirmTitle);
        mPasswordConfirmNotice.setText(R.string.user_info_logout_warning);
        mPpasswordConfirmTitle.setText(R.string.common_dialog_input_pwd);

        mPasswordConfirmCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qmuiDialog.dismiss();
            }
        });
        mPasswordConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText mPasswordConfirmEt = qmuiDialog.findViewById(R.id.passwordConfirmEt);
                final String password = mPasswordConfirmEt.getText().toString().trim();
                if(CommonUtil.isNull(password)){
                    final QMUITipDialog tipDialog = new QMUITipDialog.Builder(getContext())
                            .setTipWord(getResources().getString(R.string.common_dialog_input_pwd))
                            .create();
                    tipDialog.show();
                    mPasswordConfirmEt.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            tipDialog.dismiss();
                        }
                    }, 1500);
                    return;
                }
                final QMUITipDialog tipDialog = new QMUITipDialog.Builder(getContext())
                        .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                        .setTipWord(getResources().getString(R.string.user_info_logout_loading))
                        .create();
                tipDialog.show();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String ciphertextSkeyData = getSkeyStr();
                        try {
                            Wallet.getInstance().checkPwd(password,ciphertextSkeyData);
                            sharedPreferencesHelper.put("isFirstCreateWallet","");
                            sharedPreferencesHelper.put("createWalletStep","");
                            tipDialog.dismiss();
                            startFragment(new BPCreateWalletFragment());
                        } catch (Exception e) {
                            e.printStackTrace();
                            Looper.prepare();
                            Toast.makeText(getActivity(), R.string.checking_password_error, Toast.LENGTH_SHORT).show();
                            tipDialog.dismiss();
                            Looper.loop();
                            return;
                        }
                    }
                }).start();
                qmuiDialog.dismiss();
            }
        });

    }
}
