package com.bupocket.fragment;

import android.view.LayoutInflater;
import android.view.View;
import butterknife.BindView;
import com.bupocket.R;
import butterknife.ButterKnife;
import com.bupocket.base.BaseFragment;
import com.bupocket.fragment.home.HomeFragment;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

public class BPTxRequestTimeoutFragment extends BaseFragment {
    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;

    @BindView(R.id.iKnewBtn)
    QMUIRoundButton mIKnewBtn;
    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_tx_request_timeout, null);
        ButterKnife.bind(this, root);
        initTopBar();

        mIKnewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFragmentAndDestroyCurrent(new HomeFragment());
            }
        });

        return root;
    }

    private void initTopBar() {
        mTopBar.setBackgroundDividerEnabled(false);
        mTopBar.addLeftImageButton(R.mipmap.icon_tobar_left_arrow, R.id.topbar_left_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        });
    }
}
