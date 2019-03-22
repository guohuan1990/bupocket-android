package com.bupocket.fragment.discover;

import android.view.LayoutInflater;
import android.view.View;

import com.bupocket.R;
import com.bupocket.base.BaseFragment;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;

import butterknife.ButterKnife;

public class BPDiscoverHomeFragment extends BaseFragment {

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_discover, null);
        ButterKnife.bind(this, root);
        init();
        return root;
    }

    @Override
    protected boolean canDragBack() {
        return false;
    }

    private void init() {
        initUI();
    }

    private void initUI() {
        QMUIStatusBarHelper.setStatusBarLightMode(getBaseFragmentActivity());
    }
}
