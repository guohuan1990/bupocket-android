package com.bupocket.fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bupocket.BPApplication;
import com.bupocket.R;
import com.bupocket.base.BaseFragment;
import com.bupocket.enums.AssetTypeEnum;
import com.bupocket.enums.TxStatusEnum;
import com.bupocket.model.RegisterStatusInfo;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;


import butterknife.BindView;
import butterknife.ButterKnife;

public class BPRegisterTokenStatusFragment extends BaseFragment {
    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.registerStatusIv)
    ImageView mRegisterStatusIv;
    @BindView(R.id.registerStatusTv)
    TextView mRegisterStatusTv;
    @BindView(R.id.tokenNameTv)
    TextView mTokenNameTv;
    @BindView(R.id.tokenCodeTv)
    TextView mTokenCodeTv;
    @BindView(R.id.issueTypeTv)
    TextView mIssueTypeTv;
    @BindView(R.id.tokenAmountRl)
    RelativeLayout mTokenAmountRl;
    @BindView(R.id.tokenAmountTv)
    TextView mTokenAmountTv;
    @BindView(R.id.tokenDecimalsTv)
    TextView mTokenDecimalsTv;
    @BindView(R.id.tokenVersionTv)
    TextView mTokenVersionTv;
    @BindView(R.id.tokenDescTv)
    TextView mTokenDescTv;
    @BindView(R.id.txInfoLl)
    LinearLayout mTxInfoLl;
    @BindView(R.id.txFeeTv)
    TextView mTxFeeTv;
    @BindView(R.id.issueAddressTv)
    TextView mIssueAddressTv;
    @BindView(R.id.txHashTv)
    TextView mTxHashTv;
    @BindView(R.id.registerStatusLl)
    LinearLayout mRegisterStatusLl;
    @BindView(R.id.registerTokenInfoLl)
    LinearLayout mRegisterTokenInfoLl;

    private io.socket.client.Socket mSocket;
    private RegisterStatusInfo registerStatusInfo = new RegisterStatusInfo();
    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_register_token_status, null);
        ButterKnife.bind(this, root);
        initTopbar();
        initData();
        BPApplication application = (BPApplication)getActivity().getApplication();
        mSocket = application.getSocket();
        return root;
    }

    private void initData() {

        Bundle bundle = getArguments();
        String txStatus = bundle.getString("txStatus");
        String tokenName = bundle.getString("tokenName");
        String tokenCode = bundle.getString("tokenCode");
        String issueType = bundle.getString("issueType");
        String issueAmount = bundle.getString("issueAmount");
        String decimals = bundle.getString("decimals");
        String desc = bundle.getString("desc");

        RegisterStatusInfo.DataBean registerData = registerStatusInfo.getData();
        registerData.setName(tokenName);
        registerData.setCode(tokenCode);
        registerData.setType(issueType);
        registerData.setTotal(issueAmount);
        registerData.setDecimals(decimals);
        registerData.setVersion(getString(R.string.token_version));
        registerData.setDesc(desc);

        Drawable txStatusIconDrawable;
        String txStatusStr;
        if(txStatus.equals(TxStatusEnum.SUCCESS.getCode())){
            String txFee = bundle.getString("txFee");
            String txHash = bundle.getString("txHash");
            String issueAddress = bundle.getString("issueAddress");
            mTxFeeTv.setText(txFee);
            mTxHashTv.setText(txHash);
            mIssueAddressTv.setText(issueAddress);
            registerData.setAddress(issueAddress);
            registerData.setFee(txFee);
            registerData.setHash(txHash);
            registerStatusInfo.setErrorCode(0);
            registerStatusInfo.setErrorMsg("");
            registerStatusInfo.setData(registerData);
            mSocket.emit("token.register.success",JSON.toJSON(registerStatusInfo).toString());

            txStatusIconDrawable = ContextCompat.getDrawable(getContext(),R.mipmap.icon_send_success);
            txStatusStr = getResources().getString(R.string.register_token_success_txt);
        }else if(txStatus.equals("timeout")){
            RegisterStatusInfo registerStatusInfo = new RegisterStatusInfo();
            registerStatusInfo.setErrorCode(2);
            registerStatusInfo.setErrorMsg(getString(R.string.register_token_timeout_txt));
            mSocket.emit("token.register.timeout",JSON.toJSON(registerStatusInfo).toString());
            txStatusIconDrawable = ContextCompat.getDrawable(getContext(),R.mipmap.icon_issue_timeout);
            txStatusStr = getResources().getString(R.string.register_token_timeout_txt);
            mRegisterStatusLl.removeView(mRegisterStatusLl.findViewById(R.id.txInfoLl));
        }else {
            String txFee = bundle.getString("txFee");
            String txHash = bundle.getString("txHash");
            String issueAddress = bundle.getString("issueAddress");
            String errorMsg = bundle.getString("errorMsg");
            mTxFeeTv.setText(txFee);
            mTxHashTv.setText(txHash);
            mIssueAddressTv.setText(issueAddress);
            registerData.setAddress(issueAddress);
            registerData.setFee(txFee);
            registerData.setHash(txHash);
            registerStatusInfo.setErrorCode(1);
            registerStatusInfo.setErrorMsg(errorMsg);
            registerStatusInfo.setData(registerData);
            mSocket.emit("token.register.failure",JSON.toJSON(registerStatusInfo).toString());
            txStatusIconDrawable = ContextCompat.getDrawable(getContext(),R.mipmap.icon_send_fail);
            txStatusStr = getResources().getString(R.string.register_token_failure_txt);
        }

        if(issueType.equals(AssetTypeEnum.ATP_FIXED.getCode())){
            mIssueTypeTv.setText(getString(R.string.issue_type_disposable_txt));
        }else if(issueType.equals(AssetTypeEnum.ATP_ADD.getCode())){
            mIssueTypeTv.setText(getString(R.string.issue_type_increment_txt));
        }else if(issueType.equals(AssetTypeEnum.ATP_INFINITE.getCode())){
            mIssueTypeTv.setText(getString(R.string.issue_type_unlimited_txt));
            mRegisterTokenInfoLl.removeView(mRegisterTokenInfoLl.findViewById(R.id.tokenAmountRl));
        }
    }

    private void initTopbar() {
        mTopBar.setBackgroundDividerEnabled(false);
        mTopBar.addLeftImageButton(R.mipmap.icon_tobar_left_arrow, R.id.topbar_left_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        });
    }
}
