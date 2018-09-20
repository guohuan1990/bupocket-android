package com.bupocket.fragment;

import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.EditText;
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
    private SharedPreferencesHelper sharedPreferencesHelper;
    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_change_pwd, null);
        ButterKnife.bind(this, root);
        QMUIStatusBarHelper.setStatusBarLightMode(getBaseFragmentActivity());
        initTopBar();
        initData();
        buildWatcher();


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

                                startFragmentAndDestroyCurrent(new HomeFragment());

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

    private void buildWatcher() {
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mNextChangePwdBtn.setEnabled(false);
                mNextChangePwdBtn.setBackgroundColor(getResources().getColor(R.color.disabled_btn_color));
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mNextChangePwdBtn.setEnabled(false);
                mNextChangePwdBtn.setBackgroundColor(getResources().getColor(R.color.disabled_btn_color));
            }

            @Override
            public void afterTextChanged(Editable s) {
                boolean signOldPassword = mOldPasswordET.getText().length() > 0;
                boolean signNewPassword = mNewPasswordET.getText().length() > 0;
                boolean signNewPasswordConfirm = mNewPasswordConfirmET.getText().length() > 0;
                if(signOldPassword && signNewPassword && signNewPasswordConfirm){
                    mNextChangePwdBtn.setEnabled(true);
                    mNextChangePwdBtn.setBackgroundColor(getResources().getColor(R.color.app_btn_color_blue));
                }else {
                    mNextChangePwdBtn.setEnabled(false);
                    mNextChangePwdBtn.setBackgroundColor(getResources().getColor(R.color.disabled_btn_color));
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
