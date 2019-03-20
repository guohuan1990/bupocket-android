package com.assetMarket.fragment;

import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.assetMarket.BPApplication;
import com.assetMarket.R;
import com.assetMarket.base.BaseFragment;
import com.assetMarket.enums.BumoNodeEnum;
import com.assetMarket.enums.HiddenFunctionStatusEnum;
import com.assetMarket.utils.AddressUtil;
import com.assetMarket.utils.CommonUtil;
import com.assetMarket.utils.SharedPreferencesHelper;
import com.assetMarket.wallet.Wallet;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.popup.QMUIPopup;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import org.bitcoinj.crypto.MnemonicCode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.assetMarket.R.string.user_info_logout_notice;

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
    @BindView(R.id.tipsIv)
    ImageView mTipsIv;


    private int mCurrentDialogStyle = com.qmuiteam.qmui.R.style.QMUI_Dialog;
    private SharedPreferencesHelper sharedPreferencesHelper;
    private List<String> mnemonicCodeList;
    private QMUIPopup identityIdExplainPopup;
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
        mIdentityIdTv.setText(AddressUtil.anonymous(identityId));
    }

    private void eventListeners() {
        mUserInfoBackupWalletTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final QMUIDialog qmuiDialog = new QMUIDialog(getContext());
                qmuiDialog.setCanceledOnTouchOutside(false);
                qmuiDialog.setContentView(R.layout.password_comfirm_layout);
                qmuiDialog.show();
                QMUIRoundButton mPasswordConfirmBtn = qmuiDialog.findViewById(R.id.passwordConfirmBtn);

                ImageView mPasswordConfirmCloseBtn = qmuiDialog.findViewById(R.id.passwordConfirmCloseBtn);
                TextView mPasswordConfirmNotice = qmuiDialog.findViewById(R.id.passwordConfirmNotice);
                mPasswordConfirmNotice.setText(R.string.wallet_password_confirm_txt1);

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

        mTipsIv.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
//                initEasyPopup();
                initPopup();
                identityIdExplainPopup.setAnimStyle(QMUIPopup.ANIM_GROW_FROM_CENTER);
                identityIdExplainPopup.setPreferredDirection(QMUIPopup.DIRECTION_BOTTOM);
                identityIdExplainPopup.show(v);
                ImageView arrowUp = identityIdExplainPopup.getDecorView().findViewById(R.id.arrow_up);
                arrowUp.setImageDrawable(getResources().getDrawable(R.mipmap.triangle));
            }
        });
    }

    private void initPopup() {
        if (identityIdExplainPopup == null) {
            identityIdExplainPopup = new QMUIPopup(getContext(), QMUIPopup.DIRECTION_NONE);
            TextView textView = new TextView(getContext());
            textView.setLayoutParams(identityIdExplainPopup.generateLayoutParam(
                    QMUIDisplayHelper.dp2px(getContext(), 250),
                    WRAP_CONTENT
            ));
            textView.setLineSpacing(QMUIDisplayHelper.dp2px(getContext(), 4), 1.0f);
            int padding = QMUIDisplayHelper.dp2px(getContext(), 20);
            textView.setPadding(padding, padding, padding, padding);
            textView.setText(getString(R.string.identity_id_explain_txt));
            textView.setTextColor(ContextCompat.getColor(getContext(), R.color.app_color_white));
            textView.setBackgroundColor(getResources().getColor(R.color.popup_background_color));
            identityIdExplainPopup.setContentView(textView);
        }
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
        mPasswordConfirmNotice.setText(R.string.user_info_logout_warning);
        mPasswordConfirmNotice.setTextColor(getResources().getColor(R.color.qmui_config_color_red));

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
                            sharedPreferencesHelper.clear();
                            SharedPreferencesHelper.getInstance().save("hiddenFunctionStatus",HiddenFunctionStatusEnum.DISABLE.getCode());
                            SharedPreferencesHelper.getInstance().save("bumoNode", BumoNodeEnum.MAIN.getCode());
                            BPApplication.switchNetConfig(BumoNodeEnum.MAIN.getName());
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
