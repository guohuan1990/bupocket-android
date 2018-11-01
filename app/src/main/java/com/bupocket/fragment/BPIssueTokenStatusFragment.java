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
import com.bupocket.model.IssueStatusInfo;
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
    @BindView(R.id.tokenNameTv)
    TextView mTokenNameTv;
    @BindView(R.id.tokenCodeTv)
    TextView mTokenCodeTv;
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
    @BindView(R.id.amountRl)
    RelativeLayout mAmountRl;
    @BindView(R.id.accumulativeIssueAmountTitleTv)
    TextView mAccumulativeIssueAmountTitleTv;
    @BindView(R.id.accumulativeIssueAmountTv)
    TextView mAccumulativeIssueAmountTv;
    @BindView(R.id.issueStatusTotalLl)
    LinearLayout mIssueStatusTotalLl;


    private io.socket.client.Socket mSocket;
    private IssueStatusInfo issueStatusInfo = new IssueStatusInfo();

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

        IssueStatusInfo.DataBean issueData = new IssueStatusInfo.DataBean();
        issueData.setName(assetName);
        issueData.setCode(assetCode);
        issueData.setTotal(totalSupply);
        issueData.setDecimals(decimals);
        issueData.setVersion(getString(R.string.token_version));
        issueData.setDesc(tokenDescription);
        issueData.setIssueTotal(issueAmount);

        mTokenNameTv.setText(assetName);
        mTokenCodeTv.setText(assetCode);
        mTokenVersionTv.setText(getString(R.string.token_version));
        mAssetDescTv.setText(tokenDescription);
        mTokenDecimalsTv.setText(decimals);
        mIssueAmountTv.setText(issueAmount);
        if(totalSupply.equals("0")){
            mAssetAmountTv.setText(getString(R.string.issue_unlimited_amount_txt));
        }else {
            mAssetAmountTv.setText(totalSupply);
        }

        Drawable txStatusIconDrawable;
        String txStatusStr;
        if (txStatus.equals(TxStatusEnum.SUCCESS.getCode().toString())){
            String actualSupply = bundle.getString("actualSupply");
            if(totalSupply.equals("0")){
                Double accumulativeIssueAmount = (Double.valueOf(actualSupply)+Double.valueOf(issueAmount));
                mAccumulativeIssueAmountTv.setText(accumulativeIssueAmount.toString());
            }else {
                mAccumulativeIssueAmountTitleTv.setText(getString(R.string.surplus_issue_amount_txt));
                Double surplusAmount = Double.valueOf(totalSupply) - (Double.valueOf(actualSupply)+Double.valueOf(issueAmount));
                mAccumulativeIssueAmountTv.setText(surplusAmount.toString());
            }
            String issueAddress = bundle.getString("issueAddress");
            String txHash = bundle.getString("txHash");
            String txFee = bundle.getString("txFee");
            mTxFeeTv.setText(txFee);
            mTxHashTv.setText(txHash);
            mIssueAddressTv.setText(issueAddress);
            issueData.setAddress(issueAddress);
            issueData.setFee(txFee);
            issueData.setHash(txHash);
            issueStatusInfo.setErrorCode(0);
            issueStatusInfo.setData(issueData);
            mSocket.emit("token.issue.success",JSON.toJSON(issueStatusInfo).toString());
            txStatusIconDrawable = ContextCompat.getDrawable(getContext(),R.mipmap.icon_send_success);
            txStatusStr = getResources().getString(R.string.issue_token_success_txt);
        }else if(txStatus.equals("timeout")) {
            issueStatusInfo.setErrorCode(2);
            issueStatusInfo.setErrorMsg(getString(R.string.issue_token_timeout_txt));
            issueStatusInfo.setData(issueData);
            mIssueStatusLl.removeView(mIssueStatusLl.findViewById(R.id.amountRl));
            mIssueStatusTotalLl.removeView(mIssueStatusTotalLl.findViewById(R.id.issueTxInfolL));
            mSocket.emit("token.issue.timeout",JSON.toJSON(issueStatusInfo).toString());
            txStatusIconDrawable = ContextCompat.getDrawable(getContext(),R.mipmap.icon_issue_timeout);
            txStatusStr = getResources().getString(R.string.issue_token_timeout_txt);
        }else {
            String actualSupply = bundle.getString("actualSupply");
            if(totalSupply.equals("0")){
                Double accumulativeIssueAmount = Double.valueOf(actualSupply);
                mAccumulativeIssueAmountTv.setText(accumulativeIssueAmount.toString());
            }else {
                mAccumulativeIssueAmountTitleTv.setText(getString(R.string.surplus_issue_amount_txt));
                Double surplusAmount = Double.valueOf(totalSupply) - Double.valueOf(actualSupply);
                mAccumulativeIssueAmountTv.setText(surplusAmount.toString());
            }
            String issueAddress = bundle.getString("issueAddress");
            String txHash = bundle.getString("txHash");
            String txFee = bundle.getString("txFee");
            String errorMsg = bundle.getString("errorMsg");
            mTxFeeTv.setText(txFee);
            mTxHashTv.setText(txHash);
            mIssueAddressTv.setText(issueAddress);
            issueData.setAddress(issueAddress);
            issueData.setFee(txFee);
            issueData.setHash(txHash);
            issueStatusInfo.setErrorCode(1);
            issueStatusInfo.setErrorMsg(errorMsg);
            issueStatusInfo.setData(issueData);
            mSocket.emit("token.issue.failure",JSON.toJSON(issueStatusInfo).toString());
            txStatusIconDrawable = ContextCompat.getDrawable(getContext(),R.mipmap.icon_send_fail);
            txStatusStr = getResources().getString(R.string.issue_token_failure_txt);
        }

        mIssueStatusIv.setImageDrawable(txStatusIconDrawable);
        mIssueStatusTv.setText(txStatusStr);
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
