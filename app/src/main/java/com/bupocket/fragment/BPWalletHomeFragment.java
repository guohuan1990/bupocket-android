package com.bupocket.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.bupocket.R;
import com.bupocket.base.BaseFragment;
import com.qmuiteam.qmui.widget.QMUITopBar;

import butterknife.BindView;
import butterknife.ButterKnife;


public class BPWalletHomeFragment extends BaseFragment {
    @BindView(R.id.topbar)
    QMUITopBar mTopBar;
    private String assetCode;
    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_wallet_home, null);
        ButterKnife.bind(this, root);

        initData();
        initTopBar();
        return root;
    }

    private void initData() {
        assetCode = getArguments().get("assetCode").toString();
    }

    private void initTopBar() {
        mTopBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        });
        mTopBar.setTitle(assetCode);
    }
}
