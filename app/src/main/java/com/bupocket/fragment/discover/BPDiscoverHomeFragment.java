package com.bupocket.fragment.discover;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.bupocket.R;
import com.bupocket.activity.BumoNewsActivity;
import com.bupocket.base.BaseFragment;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BPDiscoverHomeFragment extends BaseFragment {
    @BindView(R.id.cardPackageRl)
    RelativeLayout mCardPackageRl;
    @BindView(R.id.nodePlanRl)
    RelativeLayout mNodePlanRl;
    @BindView(R.id.nodeBuildRl)
    RelativeLayout mNodeBuildRl;
    @BindView(R.id.informationRl)
    RelativeLayout mInformationRl;
    @BindView(R.id.xiaobuYoupinRl)
    RelativeLayout mXiaobuYoupinRl;

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
        setListener();
    }

    private void initUI() {
        QMUIStatusBarHelper.setStatusBarLightMode(getBaseFragmentActivity());
    }

    private void setListener() {

        // open https://m-news.bumo.io/
        mInformationRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getContext(), BumoNewsActivity.class);
                startActivity(intent);
            }
        });

        // open weixin mini program
        mXiaobuYoupinRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String appId = "wxaecf7ac4085fd34a";
                IWXAPI api = WXAPIFactory.createWXAPI(getContext(), appId);

                WXLaunchMiniProgram.Req req = new WXLaunchMiniProgram.Req();
                req.userName = "gh_463781563a74";
                req.miniprogramType = WXLaunchMiniProgram.Req.MINIPTOGRAM_TYPE_RELEASE;
                api.sendReq(req);
            }
        });
    }
}
