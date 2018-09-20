package com.bupocket.fragment;

import android.annotation.SuppressLint;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.alibaba.fastjson.JSON;
import com.bupocket.R;
import com.bupocket.base.BaseFragment;
import com.bupocket.fragment.home.HomeFragment;
import com.bupocket.utils.SharedPreferencesHelper;
import com.bupocket.wallet.Wallet;
import com.bupocket.wallet.enums.CreateWalletStepEnum;
import com.bupocket.wallet.model.WalletBPData;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import org.bitcoinj.crypto.MnemonicCode;

import java.util.Arrays;
import java.util.List;

import static com.bupocket.R.color.qmui_btn_blue_bg;
import static com.bupocket.R.color.recover_btn_grey;

public class BPRecoverWalletFormFragment extends BaseFragment {
    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;

    @BindView(R.id.recoverShowPwdIv)
    ImageView mPwdShow;

    @BindView(R.id.recoverShowConfirmPwdIv)
    ImageView mConfirmPwdShow;

    @BindView(R.id.recoverMneonicCodeEt)
    EditText mMneonicCodeEt;

    @BindView(R.id.recoverWalletNameEt)
    EditText mWalletNameEt;

    @BindView(R.id.recoverPwdEt)
    EditText mPwdEt;

    @BindView(R.id.recoverConfirmPwdEt)
    EditText mConfirmPwdEt;

    @BindView(R.id.recoverWalletSubmitBtn)
    QMUIRoundButton recoverSubmit;

    private boolean isPwdHideFirst = false;
    private boolean isConfirmPwdHideFirst = false;
    private SharedPreferencesHelper sharedPreferencesHelper;
    @Override
    protected View onCreateView() {

        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_recover_wallet_form, null);
        ButterKnife.bind(this, root);
        initTopBar();
        initData();
        eventListeners();
        QMUIStatusBarHelper.setStatusBarLightMode(getBaseFragmentActivity());
        return root;
    }

    private void initData(){
        sharedPreferencesHelper = new SharedPreferencesHelper(getContext(), "buPocket");
    }

    private void initTopBar() {

        mTopBar.addLeftImageButton(R.mipmap.icon_tobar_left_arrow, R.id.topbar_left_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//
                QMUIStatusBarHelper.setStatusBarDarkMode(getBaseFragmentActivity());
                startFragmentAndDestroyCurrent(new BPCreateWalletFragment());
            }
        });
    }

    @SuppressLint("ResourceAsColor")
    private boolean validateData(){
        if ("".equals(mMneonicCodeEt.getText().toString().trim())) {
            Toast.makeText(getActivity(), R.string.recover_edit_mneonic_code_hint,Toast.LENGTH_SHORT).show();
            recoverSubmit.setBackgroundColor(recover_btn_grey);
//            recoverSubmit.setStroke
            return false;
        } else if ("".equals(mWalletNameEt.getText().toString().trim())){
            Toast.makeText(getActivity(), R.string.recover_edit_new_wallet_name_hint,Toast.LENGTH_SHORT).show();
            recoverSubmit.setBackgroundColor(recover_btn_grey);
            return false;
        }else if("".equals(mPwdEt.getText().toString().trim())){
            Toast.makeText(getActivity(), R.string.recover_set_pwd_hint,Toast.LENGTH_SHORT).show();
            recoverSubmit.setBackgroundColor(recover_btn_grey);
            return false;
        }else if("".equals(mConfirmPwdEt.getText().toString().trim())){
            Toast.makeText(getActivity(), R.string.recover_confirm_pwd_hint,Toast.LENGTH_SHORT).show();
            recoverSubmit.setBackgroundColor(recover_btn_grey);
            return false;
        }
        recoverSubmit.setBackgroundColor(qmui_btn_blue_bg);
        return true;
    }

    private boolean mneonicFlag () {
        String mneonic = mMneonicCodeEt.getText().toString().trim();
        String regex = "[a-zA-Z\\s]+";
        if ("".equals(mneonic)) {
            Toast.makeText(getActivity(), R.string.recover_edit_mneonic_code_hint,Toast.LENGTH_SHORT).show();
            return false;
        } else if (!mneonic.matches(regex)) {
            Toast.makeText(getActivity(), R.string.recover_mneonic_input_error,Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean walletNameFlag () {
        String walletName = mWalletNameEt.getText().toString().trim();
//        ^(?!_)(?!.*?_$)[a-zA-Z0-9_\u4e00-\u9fa5]+$ 不以下划线开头
        String regex = "[a-zA-Z0-9_\\u4e00-\\u9fa5]{1,20}";
        if ("".equals(walletName)) {
            Toast.makeText(getActivity(), R.string.recover_edit_new_wallet_name_hint,Toast.LENGTH_SHORT).show();
            return false;
        } else if (!walletName.matches(regex)) {
            Toast.makeText(getActivity(), R.string.recover_input_wallet_name_error,Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean pwdFlag () {
        String pwd = mPwdEt.getText().toString().trim();
        String regex = ".{8,20}";
        if ("".equals(pwd)) {
            Toast.makeText(getActivity(), R.string.recover_set_pwd_hint,Toast.LENGTH_SHORT).show();
            return false;
        } else if (!pwd.matches(regex)) {
            Toast.makeText(getActivity(), R.string.recover_set_pwd_error,Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean confirmPwdFlag () {
        String pwd = mPwdEt.getText().toString().trim();
        String confirmPwd = mConfirmPwdEt.getText().toString().trim();
        String regex = ".{8,20}";
        if ("".equals(confirmPwd)) {
            Toast.makeText(getActivity(), R.string.recover_confirm_pwd_hint,Toast.LENGTH_SHORT).show();
            return false;
        } else if (!confirmPwd.matches(regex)) {
            Toast.makeText(getActivity(), R.string.recover_set_pwd_error,Toast.LENGTH_SHORT).show();
            return false;
        } else if (!confirmPwd.equals(pwd)) {
            Toast.makeText(getActivity(), R.string.recover_confirm_pwd_error,Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void eventListeners() {
        mPwdShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isPwdHideFirst) {
                    mPwdShow.setImageDrawable(ContextCompat.getDrawable(getContext(),R.mipmap.icon_open_eye));
                    mPwdEt.setInputType(InputType.TYPE_CLASS_TEXT);
                    mPwdEt.setTransformationMethod(HideReturnsTransformationMethod.getInstance ());
                    isPwdHideFirst = true;
                } else {
                    mPwdShow.setImageDrawable(ContextCompat.getDrawable(getContext(),R.mipmap.icon_close_eye));
                    mPwdEt.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    mPwdEt.setTransformationMethod(PasswordTransformationMethod.getInstance ());
                    isPwdHideFirst = false;
                }
            }
        });
        mConfirmPwdShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isConfirmPwdHideFirst) {
                    mConfirmPwdShow.setImageDrawable(ContextCompat.getDrawable(getContext(),R.mipmap.icon_open_eye));
                    mConfirmPwdEt.setInputType(InputType.TYPE_CLASS_TEXT);
                    mConfirmPwdEt.setTransformationMethod(HideReturnsTransformationMethod.getInstance ());
                    isConfirmPwdHideFirst = true;
                } else {
                    mConfirmPwdShow.setImageDrawable(ContextCompat.getDrawable(getContext(),R.mipmap.icon_close_eye));
                    mConfirmPwdEt.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    mConfirmPwdEt.setTransformationMethod(PasswordTransformationMethod.getInstance ());
                    isConfirmPwdHideFirst = false;
                }
            }
        });

//        提交
        recoverSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if (!mneonicFlag()) {
                   return;
               } else if (!walletNameFlag()) {
                   return;
               } else if (!pwdFlag()) {
                   return;
               } else if (!confirmPwdFlag()) {
                   return;
               }
                final String password = mPwdEt.getText().toString().trim();
                final QMUITipDialog tipDialog = new QMUITipDialog.Builder(getContext())
                        .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                        .setTipWord(getResources().getString(R.string.recover_wallet_reloading))
                        .create();
                tipDialog.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            List<String> mnemonicCodes = getMnemonicCode();
                            WalletBPData walletBPData = Wallet.getInstance().importMnemonicCode(mnemonicCodes,password);
                            sharedPreferencesHelper.put("skey", walletBPData.getSkey());
                            sharedPreferencesHelper.put("currentAccNick", mWalletNameEt.getText().toString());
                            sharedPreferencesHelper.put("BPData", JSON.toJSONString(walletBPData.getAccounts()));
                            sharedPreferencesHelper.put("currentAccAddr", walletBPData.getAccounts().get(1).getAddress());
                            sharedPreferencesHelper.put("identityId", walletBPData.getAccounts().get(0).getAddress());
                            sharedPreferencesHelper.put("createWalletStep", CreateWalletStepEnum.BACKUPED_MNEONIC_CODE.getCode());
                            tipDialog.dismiss();
                            startFragmentAndDestroyCurrent(new HomeFragment());
                        } catch (Exception e) {
                            e.printStackTrace();
                            Looper.prepare();
                            Toast.makeText(getActivity(),R.string.recover_wallet_error_tip,Toast.LENGTH_SHORT).show();
                            tipDialog.dismiss();
                            Looper.loop();
                            return;
                        }
                    }
                }).start();


            }
        });
    }

    private List<String> getMnemonicCode(){
        String inputMneonicCodeStr =  mMneonicCodeEt.getText().toString().trim();
        String regex = "\\s+";
        String []mneonicCodeArr = inputMneonicCodeStr.replaceAll(regex," ").split(" ");
        return Arrays.asList(mneonicCodeArr);

    }

}
