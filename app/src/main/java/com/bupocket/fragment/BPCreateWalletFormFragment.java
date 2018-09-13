package com.bupocket.fragment;

import android.os.Bundle;
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
import com.bupocket.utils.SecureRandomUtils;
import com.bupocket.utils.SharedPreferencesHelper;
import com.bupocket.wallet.Account;
import com.bupocket.wallet.BPData;
import com.bupocket.wallet.Constants;
import com.bupocket.wallet.MnemonicCodeTool;
import com.bupocket.wallet.enums.CreateWalletStepEnum;
import com.bupocket.wallet.utils.KeyStore;
import com.bupocket.wallet.utils.keystore.BaseKeyStoreEntity;
import com.bupocket.wallet.utils.keystore.KeyStoreEntity;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
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
        QMUIStatusBarHelper.setStatusBarLightMode(getBaseFragmentActivity());
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        sharedPreferencesHelper = new SharedPreferencesHelper(getContext(), "buPocket");
        initTopBar();
        onSubmitBtnListener();
        return root;
    }


    private boolean validateData(){
        if("".equals(mSetIdentityNameEt.getText().toString().trim())){
            Toast.makeText(getActivity(), R.string.hint_wallet_create_form_input_identity_name,Toast.LENGTH_SHORT).show();
            return false;
        }else if("".equals(mSetPwdEt.getText().toString().trim())){
            Toast.makeText(getActivity(), R.string.hint_wallet_create_form_input_password,Toast.LENGTH_SHORT).show();
            return false;
        }else if("".equals(mRepeatPwdEt.getText().toString().trim())){
            Toast.makeText(getActivity(), R.string.hint_wallet_create_form_input_rePassword,Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    private void handleCreateWalletSubmitBtnStatus(){
        if(mSetIdentityNameEt.getText() != null && mSetPwdEt.getText() != null && mRepeatPwdEt.getText() != null){
            mCreateWalletSubmitBtn.setBackgroundColor(0x36B3FF);
        }
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
                        .setTipWord("创建中")
                        .create();
                tipDialog.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String accountPwd = mSetPwdEt.getText().toString().trim();
                        // build mnumonic code
                        MnemonicCodeTool mnemonicCodeTool = null;
                        List<String> mnemonicCodeList = null;
                        try {
                            mnemonicCodeTool = new MnemonicCodeTool(getContext().getAssets().open("english.txt"));
                            mnemonicCodeList = mnemonicCodeTool.toMnemonic(randomEntropy());
                        } catch (Exception e) {
                            Toast.makeText(getActivity(), R.string.create_wallet_fail,Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }

                        // 存储钱包信息
                        String skey = HexFormat.byteToHex(randomEntropy());
                        try {
                            BaseKeyStoreEntity baseKeyStoreEntity =  KeyStore.encryptMsg(accountPwd, skey,Constants.WALLET_STORE_N, Constants.WALLET_STORE_R,Constants.WALLET_STORE_P, 1);
                            sharedPreferencesHelper.put("skey", JSON.toJSONString(baseKeyStoreEntity));
                        } catch (Exception e) {
                            Toast.makeText(getActivity(), R.string.create_wallet_fail,Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }

                        sharedPreferencesHelper.put("currentAccNick", mSetIdentityNameEt.getText().toString());

                        BPData bpData = new BPData();
                        try {
                            List<String> hdPaths = new ArrayList<>();
                            hdPaths.add("M/44/80/0/0/0");
                            hdPaths.add("M/44/80/0/0/1");
                            List<Account> accounts = mnemonicCodeTool.toPKs(mnemonicCodeList,hdPaths);

                            List<BPData.AccountsBean> accountsBeans = new ArrayList<>();
                            BPData.AccountsBean account = null;
                            for (Account acc:accounts) {
                                account = new BPData.AccountsBean();
                                account.setAddress(acc.getAddress());
                                KeyStoreEntity keyStoreEntity = KeyStore.generateKeyStore(accountPwd, acc.getPrivateKey(), Constants.WALLET_STORE_N, Constants.WALLET_STORE_R,Constants.WALLET_STORE_P, 1);
                                account.setSecret(JSON.toJSONString(keyStoreEntity));
                                accountsBeans.add(account);
                            }
                            bpData.setAccounts(accountsBeans);
                            System.out.println(JSON.toJSONString(bpData));
                            sharedPreferencesHelper.put("BPData", JSON.toJSONString(bpData));
                        } catch (Exception e) {
                            Toast.makeText(getActivity(), R.string.create_wallet_fail,Toast.LENGTH_SHORT).show();
                            e.printStackTrace();

                        }
                        sharedPreferencesHelper.put("currentAccAddr", bpData.getAccounts().get(1).getAddress());
                        sharedPreferencesHelper.put("createWalletStep", CreateWalletStepEnum.CREATE_MNEONIC_CODE.getCode());
                        BPBackupWalletFragment backupWalletFragment = new BPBackupWalletFragment();
                        Bundle argz = new Bundle();
                        argz.putStringArrayList("mneonicCodeList", (ArrayList<String>) mnemonicCodeList);
                        backupWalletFragment.setArguments(argz);

                        startFragment(backupWalletFragment);
                        tipDialog.dismiss();
                    }
                }).start();


            }
        });
    }

    private byte[] randomEntropy(){
        SecureRandom secureRandom = SecureRandomUtils.secureRandom();
        byte[] initialEntropy = new byte[16];
        secureRandom.nextBytes(initialEntropy);
        return initialEntropy;
    }

    private void initTopBar() {
//        QMUIStatusBarHelper.setStatusBarDarkMode(getBaseFragmentActivity());
        mTopBar.addLeftImageButton(R.mipmap.icon_tobar_left_arrow, R.id.topbar_left_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        });
    }


}
