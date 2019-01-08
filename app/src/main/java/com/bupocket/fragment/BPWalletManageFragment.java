package com.bupocket.fragment;

import android.view.LayoutInflater;
import android.view.View;

import com.bupocket.R;
import com.bupocket.base.BaseFragment;
import com.qmuiteam.qmui.widget.QMUITopBar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BPWalletManageFragment extends BaseFragment {

    @BindView(R.id.topbar)
    QMUITopBar mTopBar;

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_manage_wallet, null);
        ButterKnife.bind(this, root);
        init();
        return root;
    }

    private void init() {
        initData();
        initUI();
    }

    private void initData() {

    }

    private void initUI() {
        initTopBar();
    }

    private void initTopBar() {
        mTopBar.addLeftImageButton(R.mipmap.icon_tobar_left_arrow, R.id.topbar_left_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        });
        mTopBar.setTitle(getString(R.string.manage_txt));
    }
}
