package com.bupocket.fragment;

import android.view.LayoutInflater;
import android.view.View;

import com.bupocket.R;
import com.bupocket.base.BaseFragment;

import butterknife.ButterKnife;

public class BPNodePlanManagementSystemLoginErrorFragment extends BaseFragment {
    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_node_plan_management_system_login_error, null);
        ButterKnife.bind(this, root);
        init();
        return root;
    }

    private void init() {

    }
}
