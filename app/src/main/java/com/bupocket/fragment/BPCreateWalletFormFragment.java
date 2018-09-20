package com.bupocket.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.alibaba.fastjson.JSON;
import com.bupocket.R;
import com.bupocket.base.BaseFragment;
import com.bupocket.utils.CommonUtil;
import com.bupocket.utils.SecureRandomUtils;
import com.bupocket.utils.SharedPreferencesHelper;
import com.bupocket.wallet.*;
import com.bupocket.wallet.enums.CreateWalletStepEnum;
import com.bupocket.wallet.exception.WalletException;
import com.bupocket.wallet.model.WalletBPData;
import com.bupocket.wallet.utils.KeyStore;
import com.bupocket.wallet.utils.keystore.BaseKeyStoreEntity;
import com.bupocket.wallet.utils.keystore.KeyStoreEntity;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import io.bumo.encryption.utils.hex.HexFormat;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class BPCreateWalletFormFragment extends BaseFragment {
    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;

    @BindView(R.id.create_wallet_identity_name_et)
    EditText mSetIdentityNameEt;
    @BindView(R.id.create_wallet_set_pwd_et)
    EditText mSetPwdEt;
    @BindView(R.id.create_wallet_repeat_pwd_et)
    EditText mRepeatPwdEt;

    @BindView(R.id.createWalletSubmitBtn)
    QMUIRoundButton mCreateWalletSubmitBtn;
    private SharedPreferencesHelper sharedPreferencesHelper;
    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_create_wallet_form, null);
        ButterKnife.bind(this, root);

        initData();
        onSubmitBtnListener();
        buildWatcher();
        return root;
    }

    private void initData(){
        QMUIStatusBarHelper.setStatusBarLightMode(getBaseFragmentActivity());
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        sharedPreferencesHelper = new SharedPreferencesHelper(getContext(), "buPocket");

        initCreateWalletPromptView();
        initTopBar();
    }


    private boolean validateData(){
        String indntityName = mSetIdentityNameEt.getText().toString().trim();
        if("".equals(indntityName)){
            Toast.makeText(getActivity(), R.string.hint_wallet_create_form_input_identity_name,Toast.LENGTH_SHORT).show();
            return false;
        }

        if(!CommonUtil.validateNickname(indntityName)){
            Toast.makeText(getActivity(), R.string.wallet_create_form_error4,Toast.LENGTH_SHORT).show();
            return false;
        }

        String password = mSetPwdEt.getText().toString().trim();

        if("".equals(password)){
            Toast.makeText(getActivity(), R.string.hint_wallet_create_form_input_password,Toast.LENGTH_SHORT).show();
            return false;
        }
        if(password.length() < 8){
            Toast.makeText(getActivity(), R.string.wallet_create_form_error3,Toast.LENGTH_SHORT).show();
            return false;
        }
        if(password.length() > 20){
            Toast.makeText(getActivity(), R.string.wallet_create_form_error2,Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!CommonUtil.validatePassword(password)){
            Toast.makeText(getActivity(), R.string.wallet_create_form_error5,Toast.LENGTH_SHORT).show();
            return false;
        }

        String repeatPassword = mRepeatPwdEt.getText().toString().trim();

        if("".equals(repeatPassword)){
            Toast.makeText(getActivity(), R.string.hint_wallet_create_form_input_rePassword,Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!repeatPassword.equals(password)){
            Toast.makeText(getActivity(), R.string.wallet_create_form_error1,Toast.LENGTH_SHORT).show();
            return false;
        }








        return true;
    }


    private void onSubmitBtnListener(){
        mCreateWalletSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Boolean flag = validateData();
               if(!flag){
                   return;
               }
                final QMUITipDialog tipDialog = new QMUITipDialog.Builder(getContext())
                        .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                        .setTipWord(getResources().getString(R.string.wallet_create_creating_txt))
                        .create();
                tipDialog.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String accountPwd = mSetPwdEt.getText().toString().trim();

                        WalletBPData walletBPData = null;
                        try {
                            walletBPData = Wallet.getInstance().create(accountPwd);
                            sharedPreferencesHelper.put("skey", walletBPData.getSkey());
                            sharedPreferencesHelper.put("currentAccNick", mSetIdentityNameEt.getText().toString());
                            sharedPreferencesHelper.put("BPData", JSON.toJSONString(walletBPData.getAccounts()));
                            sharedPreferencesHelper.put("identityId", walletBPData.getAccounts().get(0).getAddress());
                            sharedPreferencesHelper.put("currentAccAddr", walletBPData.getAccounts().get(1).getAddress());
                            sharedPreferencesHelper.put("createWalletStep", CreateWalletStepEnum.CREATE_MNEONIC_CODE.getCode());
                            BPBackupWalletFragment backupWalletFragment = new BPBackupWalletFragment();
                            Bundle argz = new Bundle();
                            argz.putStringArrayList("mneonicCodeList", (ArrayList<String>) walletBPData.getMnemonicCodes());
                            backupWalletFragment.setArguments(argz);

                            startFragment(backupWalletFragment);
                            tipDialog.dismiss();
                        } catch (WalletException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), R.string.create_wallet_fail,Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                }).start();


            }
        });
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

    private void initCreateWalletPromptView(){
        final QMUIDialog qmuiDialog = new QMUIDialog(getContext());
        qmuiDialog.setCanceledOnTouchOutside(false);
        qmuiDialog.setContentView(R.layout.create_wallet_prompt_layout);
        qmuiDialog.show();
        QMUIRoundButton mreateWalletPromptConfirmBtn = qmuiDialog.findViewById(R.id.createWalletPromptConfirmBtn);

        mreateWalletPromptConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qmuiDialog.dismiss();
            }
        });
    }

    private void buildWatcher(){
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mCreateWalletSubmitBtn.setEnabled(false);
                mCreateWalletSubmitBtn.setBackgroundColor(getResources().getColor(R.color.disabled_btn_color));
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCreateWalletSubmitBtn.setEnabled(false);
                mCreateWalletSubmitBtn.setBackgroundColor(getResources().getColor(R.color.disabled_btn_color));
            }

            @Override
            public void afterTextChanged(Editable s) {
                boolean signIdentityName = mSetIdentityNameEt.getText().length() > 0;
                boolean signSetPwd = mSetPwdEt.getText().length() > 0;
                boolean signRepeatPwd = mRepeatPwdEt.getText().length() >0;
                if(signIdentityName && signSetPwd && signRepeatPwd){
                    mCreateWalletSubmitBtn.setEnabled(true);
                    mCreateWalletSubmitBtn.setBackgroundColor(getResources().getColor(R.color.app_btn_color_blue));
                }else {
                    mCreateWalletSubmitBtn.setEnabled(false);
                    mCreateWalletSubmitBtn.setBackgroundColor(getResources().getColor(R.color.disabled_btn_color));
                }
            }
        };
        mSetIdentityNameEt.addTextChangedListener(watcher);
        mSetPwdEt.addTextChangedListener(watcher);
        mRepeatPwdEt.addTextChangedListener(watcher);
    }

}
