package com.bupocket.fragment;

import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
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
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import java.util.ArrayList;
import java.util.List;

public class BPBackupWalletFragment extends BaseFragment {
    @BindView(R.id.backupWalletBtn)
    QMUIRoundButton mBackupWalletBtn;
    private List<String> mnemonicCodeList;
    private SharedPreferencesHelper sharedPreferencesHelper;
    private int mCurrentDialogStyle = com.qmuiteam.qmui.R.style.QMUI_Dialog;
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
                    final QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(getActivity());
                    builder.setTitle(R.string.wallet_password_confirm_title)
                            .setPlaceholder(R.string.hint_wallet_passwrod_confirm_txt1)
                            .setInputType(InputType.TYPE_CLASS_TEXT)
                            .addAction("取消", new QMUIDialogAction.ActionListener() {
                                @Override
                                public void onClick(QMUIDialog dialog, int index) {
                                    dialog.dismiss();
                                }
                            })
                            .addAction("确定", new QMUIDialogAction.ActionListener() {
                                @Override
                                public void onClick(QMUIDialog dialog, int index) {
                                    CharSequence text = builder.getEditText().getText();
                                    if (text != null && text.length() > 0) {

                                        // TODO 校验密码是否匹配
                                        String password = text.toString();
                                        String ciphertextSkeyData = getSkeyStr();
                                        try {
                                            byte[] skeyByte = Wallet.getInstance().getSkey(password,ciphertextSkeyData);
                                            MnemonicCodeTool mnemonicCodeTool = new MnemonicCodeTool(getContext().getAssets().open("english.txt"));
                                            mnemonicCodeList = mnemonicCodeTool.toMnemonic(skeyByte);
                                            go2BPCreateWalletShowMneonicCodeFragment();
                                        } catch (Exception e) {
                                            Toast.makeText(getActivity(), R.string.checking_password_error, Toast.LENGTH_SHORT).show();
                                            e.printStackTrace();
                                            return;
                                        }
                                        dialog.dismiss();
                                    } else {
                                        Toast.makeText(getActivity(), R.string.wallet_password_confirm_txt1, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            })
                            .create()
                            .show();
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
