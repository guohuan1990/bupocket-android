package com.assetMarket.fragment;

import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.assetMarket.R;
import com.assetMarket.base.BaseFragment;
import com.assetMarket.enums.TxStatusEnum;
import com.assetMarket.fragment.home.HomeFragment;
import com.assetMarket.utils.TimeUtil;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BPSendStatusFragment extends BaseFragment {

    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;

    @BindView(R.id.targetAddr)
    TextView targetAddrTv;

    @BindView(R.id.sendAmount)
    TextView sendAmountTv;

    @BindView(R.id.sendFee)
    TextView sendFeeTv;

    @BindView(R.id.sendNote)
    TextView sendNoteTv;

    @BindView(R.id.sendTimeTv)
    TextView mSendTimeTv;

    @BindView(R.id.sendTokenStatusIcon)
    QMUIRadiusImageView mSendTokenStatusIcon;

    @BindView(R.id.sendTokenStatusTv)
    TextView mSendTokenStatusTv;

    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_send_status, null);
        ButterKnife.bind(this, root);
        initTopBar();
        initData();
        return root;
    }


    private void initData(){

        Drawable txStatusIconDrawable;
        String destAccAddr = getArguments().getString("destAccAddr");
        String tokenCode = getArguments().getString("tokenCode");
        String sendAmount = getArguments().getString("sendAmount") + " " + tokenCode;
        String txFee = getArguments().getString("txFee") + " BU";
        String note = getArguments().getString("note");
        String sendTime = getArguments().getString("sendTime");
        Integer txStatus = Integer.parseInt(getArguments().getString("state"));

        String txStatusStr;
        if(txStatus.equals(TxStatusEnum.SUCCESS.getCode())){
            txStatusIconDrawable = ContextCompat.getDrawable(getContext(),R.mipmap.icon_send_success);
            txStatusStr = getResources().getString(R.string.tx_status_success_txt1);
        }else{
            txStatusIconDrawable = ContextCompat.getDrawable(getContext(),R.mipmap.icon_send_fail);
            txStatusStr = getResources().getString(R.string.tx_status_fail_txt1);
        }
        targetAddrTv.setText(destAccAddr);
        mSendTokenStatusIcon.setImageDrawable(txStatusIconDrawable);
        mSendTokenStatusTv.setText(txStatusStr);
        sendAmountTv.setText(sendAmount);
        sendFeeTv.setText(txFee);
        sendNoteTv.setText(note);
        mSendTimeTv.setText(TimeUtil.timeStamp2Date(sendTime.substring(0,10),"yyyy.MM.dd HH:mm:ss"));

    }

    private void initTopBar() {
        mTopBar.setBackgroundDividerEnabled(false);
        mTopBar.addLeftImageButton(R.mipmap.icon_tobar_left_arrow, R.id.topbar_left_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFragmentAndDestroyCurrent(new HomeFragment());
            }
        });
    }
}
