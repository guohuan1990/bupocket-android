package com.bupocket.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.bupocket.R;
import com.bupocket.base.BaseFragment;
import com.bupocket.http.api.NodePlanManagementSystemService;
import com.bupocket.http.api.RetrofitFactory;
import com.bupocket.http.api.TokenService;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BPNodePlanManagementSystemLoginFragment extends BaseFragment {
    @BindView(R.id.closeIv)
    ImageView mCloseIv;
    @BindView(R.id.loginCancelBtn)
    QMUIRoundButton mLoginCancelBtn;

    private String appId;

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_node_plan, null);
        ButterKnife.bind(this, root);
        init();
        return root;
    }

    private void init() {
        initData();
        setListener();
    }

    private void initData() {
        Bundle bundle = getArguments();
        String uuid = bundle.getString("uuid");
        NodePlanManagementSystemService nodePlanManagementSystemService = RetrofitFactory.getInstance().getRetrofit().create(NodePlanManagementSystemService.class);
        Map<String, Object> parmasMap = new HashMap<>();
        parmasMap.put("uuid",uuid);
    }

    private void setListener() {
        mCloseIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        });

        mLoginCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        });
    }
}
