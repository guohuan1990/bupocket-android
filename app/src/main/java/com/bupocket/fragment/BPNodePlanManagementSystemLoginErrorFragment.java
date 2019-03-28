package com.bupocket.fragment;

import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.bupocket.R;
import com.bupocket.base.BaseFragment;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BPNodePlanManagementSystemLoginErrorFragment extends BaseFragment {
    @BindView(R.id.confirmBtn)
    QMUIRoundButton mConfirmBtn;
    @BindView(R.id.closeIv)
    ImageView mCloseTv;

    private String errorCode;
    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_node_plan_management_system_login_error, null);
        ButterKnife.bind(this, root);
        init();
        return root;
    }

    private void init() {
        initData();
        initUI();
        setListener();
    }

    private void initData() {
        Bundle bundle = getArguments();
        if(null != bundle){
            errorCode = bundle.getString("errorCode");
        }
    }

    private void initUI() {
        
    }

    private void setListener() {
        mConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        });
        mCloseTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        });
    }
}
