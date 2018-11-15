package com.bupocket.fragment;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bupocket.R;
import com.bupocket.base.BaseFragment;
import com.bupocket.common.Constants;
import com.bupocket.enums.BumoNodeEnum;
import com.bupocket.enums.HiddenFunctionStatusEnum;
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
    @BindView(R.id.currentTestNetTipsTv)
    TextView mCurrentTestNetTipsTv;
    @BindView(R.id.meLinearLayout)
    LinearLayout mMeLinearLayout;

    final static int CLICKCOUNTS = 5;
    final static long DURATION = 3 * 1000;

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_profile, null);
        ButterKnife.bind(this, root);
        init();
        return root;
    }

    private void init() {
        initData();
        initUI();
        setListener();
    }

    private void initUI() {
        if(SharedPreferencesHelper.getInstance().getInt("bumoNode",Constants.DEFAULT_BUMO_NODE)== BumoNodeEnum.TEST.getCode()){
            mCurrentTestNetTipsTv.setText(getString(R.string.current_test_message_txt));
            mMeLinearLayout.setBackgroundColor(getResources().getColor(R.color.test_net_background_color));
        }
    }

    private void setListener() {
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

        mVersionNameTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click();
            }
        });

    }

    long[] mHits = new long[CLICKCOUNTS];
    public void click(){
        System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
        mHits[mHits.length - 1] = SystemClock.uptimeMillis();
        if(mHits[0] > SystemClock.uptimeMillis() - DURATION){
            SharedPreferencesHelper.getInstance().save("hiddenFunctionStatus",HiddenFunctionStatusEnum.ENABLE.getCode());
            SharedPreferencesHelper.getInstance().save("bumoNode", BumoNodeEnum.TEST.getCode());
            startFragment(new BPSettingFragment());
        }
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
