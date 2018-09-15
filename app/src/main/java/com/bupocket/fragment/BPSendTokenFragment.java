package com.bupocket.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.bupocket.R;
import com.bupocket.base.BaseFragment;
import com.bupocket.utils.SharedPreferencesHelper;
import com.bupocket.wallet.Wallet;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;

public class BPSendTokenFragment extends BaseFragment {
    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.accountAvailableBalanceTv)
    TextView mAccountAvailableBalanceTv;

    private String currentAccAddress;
    protected SharedPreferencesHelper sharedPreferencesHelper;
    @BindView(R.id.destAccountAddressEt)
    EditText addressET;
    @BindView(R.id.sendAmountEt)
    EditText sendAmountET;
    @BindView(R.id.sendFormNoteEt)
    EditText sendFormNoteEt;
    @BindView(R.id.sendFormTxFeeEt)
    EditText sendFormTxFeeEt;
    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_send, null);
        ButterKnife.bind(this, root);
        QMUIStatusBarHelper.setStatusBarLightMode(getBaseFragmentActivity());

        initData();
        confirmSendInfo(root);
        initTopBar();
        setDestAddress();
        return root;
    }
    private void initData(){
        sharedPreferencesHelper = new SharedPreferencesHelper(getContext(), "buPocket");
        currentAccAddress = sharedPreferencesHelper.getSharedPreference("currentAccAddr", "").toString();
        mAccountAvailableBalanceTv.setText(getAccountBUBalance());

    }
    private void initTopBar() {
        mTopBar.addLeftImageButton(R.mipmap.icon_tobar_left_arrow, R.id.topbar_left_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        });
    }

    private String getAccountBUBalance(){
        String buBalance = Wallet.getInstance().getAccountBUBalance(currentAccAddress);
        if(buBalance == null){
            return "0";
        }
        return buBalance;
    }

    private void confirmSendInfo(View view){

        view.findViewById(R.id.completeMnemonicCodeBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String address = addressET.getText().toString();

                String sendAmount = sendAmountET.getText().toString();

                String note = sendFormNoteEt.getText().toString();

                String txFee = sendFormTxFeeEt.getText().toString();

                final QMUIBottomSheet sheet = new QMUIBottomSheet(getContext());
                sheet.setContentView(R.layout.send_confirm_layout);

                TextView addressTxt = sheet.findViewById(R.id.sendTargetAddress);
                addressTxt.setText(address);

                TextView amountTxt = sheet.findViewById(R.id.sendAmount);
                amountTxt.setText(sendAmount);

                TextView estimateCostTxt = sheet.findViewById(R.id.sendEstimateCost);
                estimateCostTxt.setText(txFee);

                TextView remarkTxt = sheet.findViewById(R.id.sendRemark);
                remarkTxt.setText(note);

                sheet.findViewById(R.id.sendConfirmCloseBtn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sheet.dismiss();
                    }
                });

                sheet.show();
            }
        });
    }

    private void setDestAddress(){
        Bundle bundle = new Bundle();
        String destAddress = bundle.getString("destAddress");
        addressET.setText(destAddress);
    }
}
