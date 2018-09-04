package com.bupocket.fragment;

import android.view.LayoutInflater;
import android.view.View;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bupocket.R;
import com.bupocket.base.BaseFragment;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;

public class BPDiscoveryFragment extends BaseFragment {

    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_discovery, null);
        ButterKnife.bind(this, root);
        initTopBar();
        return root;
    }

    private void initTopBar() {
        mTopBar.setTitle(getResources().getString(R.string.tabbar_discovery_txt));
    }

}
