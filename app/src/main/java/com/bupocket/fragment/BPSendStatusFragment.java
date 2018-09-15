package com.bupocket.fragment;

import android.view.LayoutInflater;
import android.view.View;

import android.widget.TextView;
import com.bupocket.R;
import com.bupocket.base.BaseFragment;
import com.bupocket.fragment.home.HomeFragment;
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

    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_send_status, null);
        ButterKnife.bind(this, root);
        initTopBar();
        initData();
        return root;
    }


    private void initData(){

        String destAccAddr = getArguments().getString("destAccAddr");
        String sendAmount = getArguments().getString("sendAmount") + " BU";
        String txFee = getArguments().getString("txFee") + " BU";
        String note = getArguments().getString("note");
        targetAddrTv.setText(destAccAddr);
        sendAmountTv.setText(sendAmount);
        sendFeeTv.setText(txFee);
        sendNoteTv.setText(note);

    }

    private void initTopBar() {
        mTopBar.addLeftImageButton(R.mipmap.icon_tobar_left_arrow, R.id.topbar_left_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFragmentAndDestroyCurrent(new HomeFragment());
            }
        });
    }
}
