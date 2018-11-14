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
import com.bupocket.enums.TxStatusEnum;
import com.bupocket.model.RegisterStatusInfo;
import com.bupocket.utils.CommonUtil;
import com.bupocket.utils.SocketUtil;
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
        mSocket = SocketUtil.getInstance().getSocket();
        initTopbar();
        initData();
        return root;
    }

    private void initData() {

        Bundle bundle = getArguments();
        String txStatus = bundle.getString("txStatus");
        String tokenName = bundle.getString("tokenName");
        String tokenCode = bundle.getString("tokenCode");
        String issueAmount = bundle.getString("issueAmount");
        String tokenDecimals = bundle.getString("tokenDecimals");
        String tokenDesc = bundle.getString("tokenDesc");
        String issueAddress = bundle.getString("issueAddress");

        RegisterStatusInfo.DataBean registerData = new RegisterStatusInfo.DataBean();
        registerData.setName(tokenName);
        registerData.setCode(tokenCode);
        registerData.setTotal(issueAmount);
        registerData.setDecimals(tokenDecimals);
        registerData.setVersion(getString(R.string.token_version));
        registerData.setDesc(tokenDesc);
        registerData.setAddress(issueAddress);

        Drawable txStatusIconDrawable;
        String txStatusStr;
        if(txStatus.equals(TxStatusEnum.SUCCESS.getCode().toString())){
            String txFee = bundle.getString("txFee");
            String txHash = bundle.getString("txHash");
            mTxFeeTv.setText(CommonUtil.addSuffix(txFee,"BU"));
            mTxHashTv.setText(txHash);
            mIssueAddressTv.setText(issueAddress);
            registerData.setFee(txFee);
            registerData.setHash(txHash);
            registerStatusInfo.setErrorCode(0);
            registerStatusInfo.setErrorMsg("");
            registerStatusInfo.setData(registerData);
            mSocket.emit("token.register.success",JSON.toJSON(registerStatusInfo).toString());
            txStatusIconDrawable = ContextCompat.getDrawable(getContext(),R.mipmap.icon_send_success);
            txStatusStr = getResources().getString(R.string.register_token_success_txt);
        }else if(txStatus.equals("timeout")){
            registerStatusInfo.setErrorCode(2);
            registerStatusInfo.setErrorMsg(getString(R.string.register_token_timeout_txt));
            registerStatusInfo.setData(registerData);
            mSocket.emit("token.register.timeout",JSON.toJSON(registerStatusInfo).toString());
            txStatusIconDrawable = ContextCompat.getDrawable(getContext(),R.mipmap.icon_issue_timeout);
            txStatusStr = getResources().getString(R.string.register_token_timeout_txt);
            mRegisterStatusLl.removeView(mRegisterStatusLl.findViewById(R.id.txInfoLl));
        }else {
            String txFee = bundle.getString("txFee");
            String txHash = bundle.getString("txHash");
            String errorMsg = bundle.getString("errorMsg");
            mTxFeeTv.setText(CommonUtil.addSuffix(txFee,"BU"));
            mTxHashTv.setText(txHash);
            mIssueAddressTv.setText(issueAddress);
            registerData.setFee(txFee);
            registerData.setHash(txHash);
            registerStatusInfo.setErrorCode(1);
            registerStatusInfo.setErrorMsg(errorMsg);
            registerStatusInfo.setData(registerData);
            mSocket.emit("token.register.failure",JSON.toJSON(registerStatusInfo).toString());
            txStatusIconDrawable = ContextCompat.getDrawable(getContext(),R.mipmap.icon_send_fail);
            txStatusStr = getResources().getString(R.string.register_token_failure_txt);
        }

        mRegisterStatusIv.setImageDrawable(txStatusIconDrawable);
        mRegisterStatusTv.setText(txStatusStr);
        mTokenNameTv.setText(tokenName);
        mTokenCodeTv.setText(tokenCode);
        mTokenVersionTv.setText(getString(R.string.token_version));
        mTokenDescTv.setText(tokenDesc);
        mTokenDecimalsTv.setText(tokenDecimals);
        if(issueAmount.equals("0")){
            mTokenAmountTv.setText(getString(R.string.issue_unlimited_amount_txt));
        }else {
            mTokenAmountTv.setText(CommonUtil.thousandSeparator(issueAmount));
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
