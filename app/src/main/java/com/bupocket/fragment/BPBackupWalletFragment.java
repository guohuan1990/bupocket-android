package com.bupocket.fragment;

import android.os.Bundle;
import android.os.Looper;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bupocket.R;
import com.bupocket.base.BaseFragment;
import com.bupocket.utils.SharedPreferencesHelper;
import com.bupocket.wallet.MnemonicCodeTool;
import com.bupocket.wallet.Wallet;
import com.bupocket.wallet.enums.CreateWalletStepEnum;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import org.bitcoinj.crypto.MnemonicCode;

import java.util.ArrayList;
import java.util.List;

public class BPBackupWalletFragment extends BaseFragment {
    @BindView(R.id.backupWalletBtn)
    QMUIRoundButton mBackupWalletBtn;

    private List<String> mnemonicCodeList;
    private SharedPreferencesHelper sharedPreferencesHelper;
    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_backup_wallet, null);
        ButterKnife.bind(this, root);
        QMUIStatusBarHelper.setStatusBarLightMode(getBaseFragmentActivity());
        initData();

        mBackupWalletBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mnemonicCodeList == null){

                    final QMUIDialog qmuiDialog = new QMUIDialog(getContext());
                    qmuiDialog.setCanceledOnTouchOutside(false);
                    qmuiDialog.setContentView(R.layout.password_comfirm_layout);
                    qmuiDialog.show();
                    QMUIRoundButton mPasswordConfirmBtn = qmuiDialog.findViewById(R.id.passwordConfirmBtn);

                    ImageView mPasswordConfirmCloseBtn = qmuiDialog.findViewById(R.id.passwordConfirmCloseBtn);

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
                                    .setTipWord(getResources().getString(R.string.wallet_create_creating_txt))
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
                }else{
                    go2BPCreateWalletShowMneonicCodeFragment();
                }
            }
        });
        return root;
    }

    private void initData(){
        sharedPreferencesHelper = new SharedPreferencesHelper(getContext(), "buPocket");
        if(getArguments() != null){
            mnemonicCodeList = getArguments().getStringArrayList("mneonicCodeList");
        }
    }

    private String getSkeyStr(){
        return sharedPreferencesHelper.getSharedPreference("skey","").toString();
    }

    private void go2BPCreateWalletShowMneonicCodeFragment(){
        BPCreateWalletShowMneonicCodeFragment createWalletShowMneonicCodeFragment = new BPCreateWalletShowMneonicCodeFragment();
        Bundle argz = new Bundle();
        argz.putStringArrayList("mneonicCodeList", (ArrayList<String>) mnemonicCodeList);
        createWalletShowMneonicCodeFragment.setArguments(argz);
        startFragment(createWalletShowMneonicCodeFragment);

    }


    private void getMneonicCode(String password){
        String skey = sharedPreferencesHelper.getSharedPreference("skey", "").toString();
    }
}
