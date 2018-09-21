package com.bupocket.fragment;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.alibaba.fastjson.JSON;
import com.bupocket.R;
import com.bupocket.base.BaseFragment;
import com.bupocket.fragment.home.HomeFragment;
import com.bupocket.utils.CommonUtil;
import com.bupocket.utils.SharedPreferencesHelper;
import com.bupocket.wallet.Wallet;
import com.bupocket.wallet.exception.WalletException;
import com.bupocket.wallet.model.WalletBPData;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

public class BPChangePwdFragment extends BaseFragment{
    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.oldPasswordET)
    EditText mOldPasswordET;
    @BindView(R.id.newPasswordET)
    EditText mNewPasswordET;
    @BindView(R.id.newPasswordConfirmET)
    EditText mNewPasswordConfirmET;
    @BindView(R.id.nextChangePwdBtn)
    QMUIRoundButton mNextChangePwdBtn;
    @BindView(R.id.oldPasswordIv)
    ImageView mOldPasswordIv;
    @BindView(R.id.newPasswordIv)
    ImageView mNewPasswordIv;
    @BindView(R.id.newPasswordConfirmIv)
    ImageView mNewPasswordConfirmIv;

    private SharedPreferencesHelper sharedPreferencesHelper;
    private boolean isNewPwdHideFirst = false;
    private boolean isOldPwdHideFirst = false;
    private boolean isConfirmPwdHideFirst = false;
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)


    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_change_pwd, null);
        ButterKnife.bind(this, root);
        QMUIStatusBarHelper.setStatusBarLightMode(getBaseFragmentActivity());
        initTopBar();
        initData();
        buildWatcher();
        eventListeners();



        mNextChangePwdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateData()){
                    final QMUITipDialog tipDialog = new QMUITipDialog.Builder(getContext())
                            .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                            .setTipWord(getResources().getString(R.string.change_pwd_changing_txt))
                            .create();
                    tipDialog.show();
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            String oldPwd = mOldPasswordET.getText().toString().trim();
                            String newPwd = mNewPasswordET.getText().toString().trim();
                            try {
                                WalletBPData walletBPData = Wallet.getInstance().updateAccountPassword(oldPwd,newPwd,sharedPreferencesHelper.getSharedPreference("skey", "").toString());

                                sharedPreferencesHelper.put("skey", walletBPData.getSkey());
                                sharedPreferencesHelper.put("BPData", JSON.toJSONString(walletBPData.getAccounts()));

                                tipDialog.dismiss();

                                startFragment(new HomeFragment());

                            } catch (WalletException e) {
                                e.printStackTrace();
                                Looper.prepare();
                                Toast.makeText(getActivity(), R.string.change_pwd_form_error6,Toast.LENGTH_SHORT).show();
                                tipDialog.dismiss();
                                Looper.loop();
                            }finally {
                                tipDialog.dismiss();
                            }
                        }
                    }).start();


                }
            }
        });

        return root;
    }

    private void eventListeners() {
        mOldPasswordIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isOldPwdHideFirst) {
                    mOldPasswordIv.setImageDrawable(ContextCompat.getDrawable(getContext(),R.mipmap.icon_open_eye));
                    mOldPasswordET.setInputType(InputType.TYPE_CLASS_TEXT);
                    mOldPasswordET.setTransformationMethod(HideReturnsTransformationMethod.getInstance ());
                    isOldPwdHideFirst = true;
                } else {
                    mOldPasswordIv.setImageDrawable(ContextCompat.getDrawable(getContext(),R.mipmap.icon_close_eye));
                    mOldPasswordET.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    mOldPasswordET.setTransformationMethod(PasswordTransformationMethod.getInstance ());
                    isOldPwdHideFirst = false;
                }
            }
        });
        mNewPasswordIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isNewPwdHideFirst) {
                    mNewPasswordIv.setImageDrawable(ContextCompat.getDrawable(getContext(),R.mipmap.icon_open_eye));
                    mNewPasswordET.setInputType(InputType.TYPE_CLASS_TEXT);
                    mNewPasswordET.setTransformationMethod(HideReturnsTransformationMethod.getInstance ());
                    isNewPwdHideFirst = true;
                } else {
                    mNewPasswordIv.setImageDrawable(ContextCompat.getDrawable(getContext(),R.mipmap.icon_close_eye));
                    mNewPasswordET.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    mNewPasswordET.setTransformationMethod(PasswordTransformationMethod.getInstance ());
                    isNewPwdHideFirst = false;
                }
            }
        });
        mNewPasswordConfirmIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isConfirmPwdHideFirst) {
                    mNewPasswordConfirmIv.setImageDrawable(ContextCompat.getDrawable(getContext(),R.mipmap.icon_open_eye));
                    mNewPasswordConfirmET.setInputType(InputType.TYPE_CLASS_TEXT);
                    mNewPasswordConfirmET.setTransformationMethod(HideReturnsTransformationMethod.getInstance ());
                    isConfirmPwdHideFirst = true;
                } else {
                    mNewPasswordConfirmIv.setImageDrawable(ContextCompat.getDrawable(getContext(),R.mipmap.icon_close_eye));
                    mNewPasswordConfirmET.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    mNewPasswordConfirmET.setTransformationMethod(PasswordTransformationMethod.getInstance ());
                    isConfirmPwdHideFirst = false;
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void buildWatcher() {
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mNextChangePwdBtn.setEnabled(false);
                mNextChangePwdBtn.setBackground(getResources().getDrawable(R.drawable.radius_button_disable_bg));
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mNextChangePwdBtn.setEnabled(false);
                mNextChangePwdBtn.setBackground(getResources().getDrawable(R.drawable.radius_button_disable_bg));
            }

            @Override
            public void afterTextChanged(Editable s) {
                boolean signOldPassword = mOldPasswordET.getText().length() > 0;
                boolean signNewPassword = mNewPasswordET.getText().length() > 0;
                boolean signNewPasswordConfirm = mNewPasswordConfirmET.getText().length() > 0;
                if(signOldPassword && signNewPassword && signNewPasswordConfirm){
                    mNextChangePwdBtn.setEnabled(true);
                    mNextChangePwdBtn.setBackground(getResources().getDrawable(R.drawable.radius_button_able_bg));
                }else {
                    mNextChangePwdBtn.setEnabled(false);
                    mNextChangePwdBtn.setBackground(getResources().getDrawable(R.drawable.radius_button_disable_bg));
                }
            }
        };
        mOldPasswordET.addTextChangedListener(watcher);
        mNewPasswordET.addTextChangedListener(watcher);
        mNewPasswordConfirmET.addTextChangedListener(watcher);
    }

    private void initData(){
        sharedPreferencesHelper = new SharedPreferencesHelper(getContext(), "buPocket");
    }

    private boolean validateData(){
        final QMUITipDialog tipDialog;

        String oldPwd = mOldPasswordET.getText().toString().trim();
        String newPwd = mNewPasswordET.getText().toString().trim();
        String newPasswordConfirm = mNewPasswordConfirmET.getText().toString().trim();


        if(CommonUtil.isNull(oldPwd)){
            tipDialog = new QMUITipDialog.Builder(getContext())
                    .setTipWord(getResources().getString(R.string.change_pwd_err1))
                    .create();
            tipDialog.show();
            mOldPasswordET.postDelayed(new Runnable() {
                @Override
                public void run() {
                    tipDialog.dismiss();
                }
            }, 1500);
            return false;
        }

        if(CommonUtil.isNull(newPwd)){
            tipDialog = new QMUITipDialog.Builder(getContext())
                    .setTipWord(getResources().getString(R.string.change_pwd_err2))
                    .create();
            tipDialog.show();
            mNewPasswordET.postDelayed(new Runnable() {
                @Override
                public void run() {
                    tipDialog.dismiss();
                }
            }, 1500);
            return false;
        }

        if(newPwd.length() < 8){
            tipDialog = new QMUITipDialog.Builder(getContext())
                    .setTipWord(getResources().getString(R.string.change_pwd_form_error3))
                    .create();
            tipDialog.show();
            mNewPasswordET.postDelayed(new Runnable() {
                @Override
                public void run() {
                    tipDialog.dismiss();
                }
            }, 1500);
            return false;
        }
        if(newPwd.length() > 20){
            tipDialog = new QMUITipDialog.Builder(getContext())
                    .setTipWord(getResources().getString(R.string.change_pwd_form_error2))
                    .create();
            tipDialog.show();
            mNewPasswordET.postDelayed(new Runnable() {
                @Override
                public void run() {
                    tipDialog.dismiss();
                }
            }, 1500);
            return false;
        }
        if(!CommonUtil.validatePassword(newPwd)){
            tipDialog = new QMUITipDialog.Builder(getContext())
                    .setTipWord(getResources().getString(R.string.change_pwd_form_error5))
                    .create();
            tipDialog.show();
            mNewPasswordET.postDelayed(new Runnable() {
                @Override
                public void run() {
                    tipDialog.dismiss();
                }
            }, 1500);
            return false;
        }


        if(CommonUtil.isNull(newPasswordConfirm)){
            tipDialog = new QMUITipDialog.Builder(getContext())
                    .setTipWord(getResources().getString(R.string.change_pwd_err3))
                    .create();
            tipDialog.show();
            mNewPasswordConfirmET.postDelayed(new Runnable() {
                @Override
                public void run() {
                    tipDialog.dismiss();
                }
            }, 1500);
            return false;
        }
        if(!newPasswordConfirm.equals(newPwd)){
            tipDialog = new QMUITipDialog.Builder(getContext())
                    .setTipWord(getResources().getString(R.string.change_pwd_form_error1))
                    .create();
            tipDialog.show();
            mNewPasswordConfirmET.postDelayed(new Runnable() {
                @Override
                public void run() {
                    tipDialog.dismiss();
                }
            }, 1500);
            return false;
        }
        return true;
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
}
