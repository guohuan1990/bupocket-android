package com.bupocket.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.bupocket.R;
import com.bupocket.base.BaseFragment;
import com.bupocket.fragment.home.HomeFragment;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BPUserTermsFragment extends BaseFragment {

    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;

    @BindView(R.id.agreeUserTermsCheckbox)
    CheckBox mAgreeUserTerms;

    @BindView(R.id.userTermsNextBtn)
    TextView mUserTermsNext;

    private Boolean isAgreeTerms = false;

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_send_status, null);
        ButterKnife.bind(this, root);
        initTopBar();
        eventListeners();
        return root;
    }

    private void eventListeners() {
        mAgreeUserTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//
            }
        });
        mUserTermsNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isAgreeTerms) {
//                    继续
                } else {
                    return;
                }
            }
        });

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
