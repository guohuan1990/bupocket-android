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
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.subgraph.orchid.RelayCell;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BPIssueTokenStatusFragment extends BaseFragment {
    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.txFeeTv)
    TextView mTxFeeTv;
    @BindView(R.id.issueAddressTv)
    TextView mIssueAddressTv;
    @BindView(R.id.txHashTv)
    TextView mTxHashTv;
    @BindView(R.id.issueStatusIv)
    ImageView mIssueStatusIv;
    @BindView(R.id.issueStatusTv)
    TextView mIssueStatusTv;
    @BindView(R.id.assetNameTv)
    TextView mAssetNameTv;
    @BindView(R.id.assetCodeTv)
    TextView mAssetCodeTv;
    @BindView(R.id.tokenDecimalsTv)
    TextView mTokenDecimalsTv;
    @BindView(R.id.assetAmountTv)
    TextView mAssetAmountTv;
    @BindView(R.id.issueAmountTv)
    TextView mIssueAmountTv;
    @BindView(R.id.tokenVersionTv)
    TextView mTokenVersionTv;
    @BindView(R.id.assetDescTv)
    TextView mAssetDescTv;
    @BindView(R.id.issueTxInfolL)
    LinearLayout mIssueTxInfoLl;
    @BindView(R.id.issueStatusLl)
    LinearLayout mIssueStatusLl;
    @BindView(R.id.assetAmountRl)
    RelativeLayout mAssetAmountRl;


    private io.socket.client.Socket mSocket;
    private RegisterStatusInfo registerStatusInfo = new RegisterStatusInfo();

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_issue_token_status, null);
        ButterKnife.bind(this, root);
        BPApplication application = (BPApplication)getActivity().getApplication();
        mSocket = application.getSocket();
        initData();
        initTopbar();
        return root;
    }

    private void initData() {

        Bundle bundle = getArguments();
        String txStatus = bundle.getString("txStatus");
        String assetName = bundle.getString("assetName");
        String assetCode = bundle.getString("assetCode");
        String issueAmount = bundle.getString("issueAmount");
        String totalSupply = bundle.getString("totalSupply");
        String decimals = bundle.getString("decimals");
        String tokenDescription = bundle.getString("tokenDescription");

        RegisterStatusInfo.DataBean registerData = new RegisterStatusInfo.DataBean();
        registerData.setName(assetName);
        registerData.setCode(assetCode);
        registerData.setTotal(issueAmount);
        registerData.setDecimals(decimals);
        registerData.setVersion(getString(R.string.token_version));
        registerData.setDesc(tokenDescription);

        Drawable txStatusIconDrawable;
        String txStatusStr;
        if (txStatus.equals(TxStatusEnum.SUCCESS.getCode().toString())){
            String issueAddress = bundle.getString("issueAddress");
            String txHash = bundle.getString("hash");
            String txFee = bundle.getString("txFee");
            mTxFeeTv.setText(txFee);
            mTxHashTv.setText(txHash);
            mIssueAddressTv.setText(issueAddress);
            registerData.setAddress(issueAddress);
            registerData.setFee(txFee);
            registerData.setHash(txHash);
            registerStatusInfo.setErrorCode(0);
            registerStatusInfo.setErrorMsg("");
            registerStatusInfo.setData(registerData);
            mSocket.emit("token.issue.success",JSON.toJSON(registerStatusInfo).toString());
            txStatusIconDrawable = ContextCompat.getDrawable(getContext(),R.mipmap.icon_send_success);
            txStatusStr = getResources().getString(R.string.issue_token_success_txt);
        }else if(txStatus.equals("timeout")) {
            registerStatusInfo.setErrorCode(2);
            registerStatusInfo.setErrorMsg(getString(R.string.register_token_timeout_txt));
            registerStatusInfo.setData(registerData);
            mSocket.emit("token.issue.timeout",JSON.toJSON(registerStatusInfo).toString());
            txStatusIconDrawable = ContextCompat.getDrawable(getContext(),R.mipmap.icon_issue_timeout);
            txStatusStr = getResources().getString(R.string.issue_token_timeout_txt);
        }else {
            String issueAddress = bundle.getString("issueAddress");
            String txHash = bundle.getString("hash");
            String txFee = bundle.getString("txFee");
            String errorMsg = bundle.getString("errorMsg");
            mTxFeeTv.setText(txFee);
            mTxHashTv.setText(txHash);
            mIssueAddressTv.setText(issueAddress);
            mSocket.emit("token.issue.failure",JSON.toJSON(registerStatusInfo).toString());
            txStatusIconDrawable = ContextCompat.getDrawable(getContext(),R.mipmap.icon_send_fail);
            txStatusStr = getResources().getString(R.string.issue_token_failure_txt);
        }

        mIssueStatusIv.setImageDrawable(txStatusIconDrawable);
        mIssueStatusTv.setText(txStatusStr);

        mAssetNameTv.setText(assetName);
        mAssetCodeTv.setText(assetCode);
        mTokenVersionTv.setText(getString(R.string.token_version));
        mAssetDescTv.setText(tokenDescription);
        mTokenDecimalsTv.setText(decimals);
        mIssueAmountTv.setText(issueAmount);
        mAssetAmountTv.setText(totalSupply);
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
