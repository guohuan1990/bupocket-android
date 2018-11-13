package com.bupocket.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bupocket.R;
import com.bupocket.base.BaseFragment;
import com.bupocket.utils.CommonUtil;
import com.bupocket.utils.SharedPreferencesHelper;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class BPProfileFragment extends BaseFragment{
    private SharedPreferencesHelper sharedPreferencesHelper;
    private String currentAccNick;

    @BindString(R.string.qr_copy_success_message)
    String copySuccessMessage;
    @BindView(R.id.userNick)
    TextView userNickTx;
    @BindView(R.id.changePwdRL)
    RelativeLayout mChangePwdRL;
    @BindView(R.id.helpFeedbackRL)
    RelativeLayout mHelpRL;
    @BindView(R.id.settingRL)
    RelativeLayout mSettingRL;
    @BindView(R.id.versionNameTv)
    TextView mVersionNameTv;
    @BindView(R.id.profileAvatarIv)
    QMUIRadiusImageView mProfileAvatarIv;

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_profile, null);
        ButterKnife.bind(this, root);
        initData();
        mChangePwdRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoChangePwdFragment();
            }
        });
        mHelpRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoHelpFeedbackFragment();
            }
        });
        mSettingRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoSettingFragment();
            }
        });
        mProfileAvatarIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle argz = new Bundle();
                argz.putString("accName",currentAccNick);
                BPUserInfoFragment bpUserInfoFragment = new BPUserInfoFragment();
                bpUserInfoFragment.setArguments(argz);
                startFragment(bpUserInfoFragment);
            }
        });
        return root;
    }

    private void initData(){
        sharedPreferencesHelper = new SharedPreferencesHelper(getContext(), "buPocket");
        currentAccNick = sharedPreferencesHelper.getSharedPreference("currentAccNick", "").toString();

        userNickTx.setText(currentAccNick);

        mVersionNameTv.setText(CommonUtil.packageName(getContext()));
    }


    private void gotoChangePwdFragment(){
        startFragment(new BPChangePwdFragment());
    }

    private void gotoHelpFeedbackFragment(){
        startFragment(new BPHelpFeedbackFragment());
    }

    private void gotoSettingFragment(){
        startFragment(new BPSettingFragment());
    }
}
