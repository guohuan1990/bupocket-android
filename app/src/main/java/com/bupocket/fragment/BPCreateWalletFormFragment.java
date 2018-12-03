package com.bupocket.fragment;

import android.os.Build;
import android.os.Bundle;
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
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;

import com.alibaba.fastjson.JSON;
import com.bupocket.R;
import com.bupocket.base.BaseFragment;
import com.bupocket.http.api.AccountService;
import com.bupocket.http.api.RetrofitFactory;
import com.bupocket.http.api.dto.resp.ApiResult;
import com.bupocket.http.api.dto.resp.GetUserTokenRespDto;
import com.bupocket.utils.CommonUtil;
import com.bupocket.utils.SharedPreferencesHelper;
import com.bupocket.wallet.Wallet;
import com.bupocket.wallet.enums.CreateWalletStepEnum;
import com.bupocket.wallet.enums.ExceptionEnum;
import com.bupocket.wallet.exception.WalletException;
import com.bupocket.wallet.model.WalletBPData;
import com.bupocket.wallet.model.WalletSignData;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BPCreateWalletFormFragment extends BaseFragment {
    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;

    @BindView(R.id.create_wallet_identity_name_et)
    EditText mSetIdentityNameEt;
    @BindView(R.id.create_wallet_set_pwd_et)
    EditText mSetPwdEt;
    @BindView(R.id.create_wallet_repeat_pwd_et)
    EditText mRepeatPwdEt;

    @BindView(R.id.recoverShowPwdIv)
    ImageView mPwdShow;

    @BindView(R.id.recoverShowConfirmPwdIv)
    ImageView mConfirmPwdShow;

    @BindView(R.id.createWalletSubmitBtn)
    QMUIRoundButton mCreateWalletSubmitBtn;
    private boolean isPwdHideFirst = false;
    private boolean isConfirmPwdHideFirst = false;
    private SharedPreferencesHelper sharedPreferencesHelper;
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_create_wallet_form, null);
        ButterKnife.bind(this, root);

        initData();
        onSubmitBtnListener();
        buildWatcher();
        eventListeners();
        return root;
    }

    private void eventListeners() {
        mPwdShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isPwdHideFirst) {
                    mPwdShow.setImageDrawable(ContextCompat.getDrawable(getContext(),R.mipmap.icon_open_eye));
                    mSetPwdEt.setInputType(InputType.TYPE_CLASS_TEXT);
                    mSetPwdEt.setTransformationMethod(HideReturnsTransformationMethod.getInstance ());
                    mSetPwdEt.setSelection(mSetPwdEt.getText().length());
                    isPwdHideFirst = true;
                } else {
                    mPwdShow.setImageDrawable(ContextCompat.getDrawable(getContext(),R.mipmap.icon_close_eye));
                    mSetPwdEt.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    mSetPwdEt.setTransformationMethod(PasswordTransformationMethod.getInstance ());
                    mSetPwdEt.setSelection(mSetPwdEt.getText().length());
                    isPwdHideFirst = false;
                }
            }
        });
        mConfirmPwdShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isConfirmPwdHideFirst) {
                    mConfirmPwdShow.setImageDrawable(ContextCompat.getDrawable(getContext(),R.mipmap.icon_open_eye));
                    mRepeatPwdEt.setInputType(InputType.TYPE_CLASS_TEXT);
                    mRepeatPwdEt.setTransformationMethod(HideReturnsTransformationMethod.getInstance ());
                    mRepeatPwdEt.setSelection(mRepeatPwdEt.getText().length());
                    isConfirmPwdHideFirst = true;
                } else {
                    mConfirmPwdShow.setImageDrawable(ContextCompat.getDrawable(getContext(),R.mipmap.icon_close_eye));
                    mRepeatPwdEt.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    mRepeatPwdEt.setTransformationMethod(PasswordTransformationMethod.getInstance ());
                    mRepeatPwdEt.setSelection(mRepeatPwdEt.getText().length());
                    isConfirmPwdHideFirst = false;
                }
            }
        });
    }

    private void initData(){
        QMUIStatusBarHelper.setStatusBarLightMode(getBaseFragmentActivity());
        sharedPreferencesHelper = new SharedPreferencesHelper(getContext(), "buPocket");

        initCreateWalletPromptView();
        initTopBar();
    }


    private boolean validateData(){
        String indntityName = mSetIdentityNameEt.getText().toString().trim();
        if("".equals(indntityName)){
            Toast.makeText(getActivity(), R.string.wallet_create_form_input_identity_name_empty,Toast.LENGTH_SHORT).show();
            return false;
        }

        if(!CommonUtil.validateNickname(indntityName)){
            Toast.makeText(getActivity(), R.string.wallet_create_form_error4,Toast.LENGTH_SHORT).show();
            return false;
        }

        String password = mSetPwdEt.getText().toString().trim();

        if("".equals(password)){
            Toast.makeText(getActivity(), R.string.wallet_create_form_input_password_empty,Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getActivity(), R.string.wallet_create_form_input_rePassword_empty,Toast.LENGTH_SHORT).show();
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
                            walletBPData = Wallet.getInstance().create(accountPwd,getContext());
                            sharedPreferencesHelper.put("skey", walletBPData.getSkey());
                            sharedPreferencesHelper.put("currentAccNick", mSetIdentityNameEt.getText().toString());
                            sharedPreferencesHelper.put("BPData", JSON.toJSONString(walletBPData.getAccounts()));
                            sharedPreferencesHelper.put("identityId", walletBPData.getAccounts().get(0).getAddress());
                            sharedPreferencesHelper.put("currentAccAddr", walletBPData.getAccounts().get(1).getAddress());
                            sharedPreferencesHelper.put("createWalletStep", CreateWalletStepEnum.CREATE_MNEONIC_CODE.getCode());


                            generateUserToken(walletBPData.getAccounts().get(1).getAddress(), accountPwd);
                            BPBackupWalletFragment backupWalletFragment = new BPBackupWalletFragment();
                            Bundle argz = new Bundle();
                            argz.putStringArrayList("mneonicCodeList", (ArrayList<String>) walletBPData.getMnemonicCodes());
                            backupWalletFragment.setArguments(argz);
                            Thread.sleep(1500);
                            startFragment(backupWalletFragment);


                            tipDialog.dismiss();
                        } catch (WalletException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), R.string.create_wallet_fail,Toast.LENGTH_SHORT).show();
                            return;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
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

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void buildWatcher(){
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mCreateWalletSubmitBtn.setEnabled(false);
                mCreateWalletSubmitBtn.setBackground(getResources().getDrawable(R.drawable.radius_button_disable_bg));
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCreateWalletSubmitBtn.setEnabled(false);
                mCreateWalletSubmitBtn.setBackground(getResources().getDrawable(R.drawable.radius_button_disable_bg));
            }

            @Override
            public void afterTextChanged(Editable s) {
                boolean signIdentityName = mSetIdentityNameEt.getText().length() > 0;
                boolean signSetPwd = mSetPwdEt.getText().length() > 0;
                boolean signRepeatPwd = mRepeatPwdEt.getText().length() >0;
                if(signIdentityName && signSetPwd && signRepeatPwd){
                    mCreateWalletSubmitBtn.setEnabled(true);
                    mCreateWalletSubmitBtn.setBackground(getResources().getDrawable(R.drawable.radius_button_able_bg));
                }else {
                    mCreateWalletSubmitBtn.setEnabled(false);
                    mCreateWalletSubmitBtn.setBackground(getResources().getDrawable(R.drawable.radius_button_disable_bg));
                }
            }
        };
        mSetIdentityNameEt.addTextChangedListener(watcher);
        mSetPwdEt.addTextChangedListener(watcher);
        mRepeatPwdEt.addTextChangedListener(watcher);
    }

    private void generateUserToken(String currentAccAddr, String accountPwd) {
        String accountBPData = getAccountBPData();
        String currentAccAddress = currentAccAddr;
        try {
            final WalletSignData walletSignData = Wallet.getInstance().signData(accountPwd,currentAccAddress,accountBPData);
            AccountService accountService = RetrofitFactory.getInstance().getRetrofit().create(AccountService.class);
            Map<String, Object> parmasMap = new HashMap<>();
            parmasMap.put("publicKey",walletSignData.getPublicKey());
            parmasMap.put("signData", walletSignData.getSignData());
            retrofit2.Call<ApiResult<GetUserTokenRespDto>> call = accountService.getUserToken(parmasMap);
            call.enqueue(new retrofit2.Callback<ApiResult<GetUserTokenRespDto>>() {

                @Override
                public void onResponse(Call<ApiResult<GetUserTokenRespDto>> call, retrofit2.Response<ApiResult<GetUserTokenRespDto>> response) {
                    ApiResult<GetUserTokenRespDto> respDto = response.body();
                    if (com.bupocket.enums.ExceptionEnum.SUCCESS.getCode().equals(respDto.getErrCode())) {
                        sharedPreferencesHelper.put("userToken",respDto.getData().getUserToken());
                    }
                }

                @Override
                public void onFailure(Call<ApiResult<GetUserTokenRespDto>> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private String getAccountBPData(){
        String data = sharedPreferencesHelper.getSharedPreference("BPData", "").toString();
        return data;
    }
}
