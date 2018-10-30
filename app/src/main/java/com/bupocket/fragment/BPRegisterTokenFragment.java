package com.bupocket.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bupocket.BPApplication;
import com.bupocket.R;
import com.bupocket.base.BaseFragment;
import com.bupocket.common.Constants;
import com.bupocket.model.RegisterTokenInfo;
import com.bupocket.utils.RSAUtil;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import java.util.Scanner;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class BPRegisterTokenFragment extends BaseFragment {
    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.registerConfirmBtn)
    QMUIRoundButton mRegisterConfirmBtn;
    @BindView(R.id.registerCancelBtn)
    QMUIRoundButton mRegisterCancelBtn;
    @BindView(R.id.tokenNameTv)
    TextView mTokenNameTv;
    @BindView(R.id.tokenCodeTv)
    TextView mTokenCodeTv;
    @BindView(R.id.tokenAmountTv)
    TextView mTokenAmountTv;
    @BindView(R.id.regisgerFeeTv)
    TextView mRegisgerFeeTv;
    @BindView(R.id.issueTypeTv)
    TextView mIssueTypeTv;

    private Socket mSocket;
    private String tokenName;
    private String tokenCode;
    private String issueAmount;
    private String decimals;
    private String desc;
    private String issueType;

    public BPRegisterTokenFragment(){
        super();
    }

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_register_token_confirm, null);
        ButterKnife.bind(this, root);
        QMUIStatusBarHelper.setStatusBarLightMode(getBaseFragmentActivity());
        initTopbar();
        initdata();

        setListener();
        return root;
    }

    private void initdata() {
        Bundle bundle = getArguments();
        String data = bundle.getString("tokenData");
        RegisterTokenInfo registerTokenInfo = RegisterTokenInfo.objectFromData(data);
        tokenName = registerTokenInfo.getName();
        tokenCode = registerTokenInfo.getCode();
        issueAmount = registerTokenInfo.getAmount();
        decimals = registerTokenInfo.getDecimals();
        desc = registerTokenInfo.getDesc();
        issueType = registerTokenInfo.getType();
        mTokenNameTv.setText(tokenName);
        mTokenCodeTv.setText(tokenCode);
        mTokenAmountTv.setText(issueAmount);
        mRegisgerFeeTv.setText("0.01");
        switch (issueType){
            case "0":{
                mIssueTypeTv.setText(getString(R.string.issue_type_disposable_txt));
                break;
            }
            case "1":{
                mIssueTypeTv.setText(getString(R.string.issue_type_increment_txt));
                break;
            }
            case "2":{
                mIssueTypeTv.setText(getString(R.string.issue_type_unlimited_txt));
                break;
            }
        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        final String uuID = bundle.getString("uuID");

        BPApplication application = (BPApplication)getActivity().getApplication();
        mSocket = application.getSocket();
        mSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                System.out.println("socket id :" + mSocket.id());
                mSocket.emit("token.register.join",uuID);
//                mSocket.disconnect();
            }

        }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... args) {}

        });
        mSocket.emit("token.register.scanSuccess","");
        mSocket.connect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
    }

    private void setListener() {
        mRegisterConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPasswordComfirmDialog();
            }
        });
        mRegisterCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSocket.emit("token.register.cancel","");
                startFragment(new BPAssetsHomeFragment());
            }
        });
    }

    private void showPasswordComfirmDialog() {
        final QMUIDialog qmuiDialog = new QMUIDialog(getContext());
        qmuiDialog.setCanceledOnTouchOutside(false);
        qmuiDialog.setContentView(R.layout.password_comfirm_layout);
        qmuiDialog.show();
        QMUIRoundButton mPasswordConfirmBtn = qmuiDialog.findViewById(R.id.passwordConfirmBtn);
        ImageView mPasswordConfirmCloseBtn = qmuiDialog.findViewById(R.id.passwordConfirmCloseBtn);
        TextView mPasswordConfirmNotice = qmuiDialog.findViewById(R.id.passwordConfirmNotice);
        TextView mPpasswordConfirmTitle = qmuiDialog.findViewById(R.id.passwordConfirmTitle);

        mPasswordConfirmNotice.setText(getString(R.string.register_token_password_confirm_txt));
        mPpasswordConfirmTitle.setText(getString(R.string.password_comfirm_dialog_title));

        mPasswordConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qmuiDialog.dismiss();
                startFragment(new BPRegisterTokenStatusFragment());
            }
        });

        mPasswordConfirmCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qmuiDialog.dismiss();
            }
        });
    }

    private void initTopbar() {
        mTopBar.addLeftImageButton(R.mipmap.icon_tobar_left_arrow, R.id.topbar_left_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QMUIStatusBarHelper.setStatusBarDarkMode(getBaseFragmentActivity());
                popBackStack();
            }
        });
        mTopBar.setTitle(getString(R.string.register_token_page_title));
    }
}
