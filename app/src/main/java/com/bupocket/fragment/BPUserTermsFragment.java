package com.bupocket.fragment;

import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bupocket.R;
import com.bupocket.base.BaseFragment;
import com.bupocket.fragment.home.HomeFragment;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;

import java.io.IOException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BPUserTermsFragment extends BaseFragment {

    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;

    @BindView(R.id.userTermsContentTv)
    TextView mUserTermsContentTv;

    @BindView(R.id.agreeUserTermsCheckbox)
    CheckBox mAgreeUserTerms;

    @BindView(R.id.userTermsNextBtn)
    TextView mUserTermsNext;

    private Boolean isAgreeTerms = false;

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_use_terms, null);
        ButterKnife.bind(this, root);
        initTopBar();
        eventListeners();

        try {
            Resources res = getResources();
            InputStream in_s = res.openRawResource(R.raw.user_terms);
            byte[] b = new byte[in_s.available()];
            in_s.read(b);
            mUserTermsContentTv.setText(new String(b));
        }catch (IOException e) {
            mUserTermsContentTv.setText(new String("loading"));
        }
        QMUIStatusBarHelper.setStatusBarLightMode(getBaseFragmentActivity());
        return root;
    }

    private void eventListeners() {
        mAgreeUserTerms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                isAgreeTerms = isChecked;
                if (isAgreeTerms) {
                    mUserTermsNext.setEnabled(true);
                    mUserTermsNext.setBackgroundColor(getResources().getColor(R.color.app_btn_color_blue));
                } else {
                    mUserTermsNext.setEnabled(false);
                    mUserTermsNext.setBackgroundColor(getResources().getColor(R.color.terms_unagree_btn_grey));
                }
            }
        });
        mUserTermsNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startFragment(new BPCreateWalletFormFragment());
            }
        });

    }

    private void initTopBar() {
        mTopBar.addLeftImageButton(R.mipmap.icon_tobar_left_arrow, R.id.topbar_left_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QMUIStatusBarHelper.setStatusBarDarkMode(getBaseFragmentActivity());
                startFragmentAndDestroyCurrent(new HomeFragment());
            }
        });
    }
}
