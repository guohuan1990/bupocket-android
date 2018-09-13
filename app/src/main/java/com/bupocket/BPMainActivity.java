package com.bupocket;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import com.bupocket.base.BaseFragment;
import com.bupocket.base.BaseFragmentActivity;
import com.bupocket.fragment.BPBackupWalletFragment;
import com.bupocket.fragment.BPCreateWalletFragment;
import com.bupocket.fragment.home.HomeFragment;
import com.bupocket.fragment.home.util.QDNotchHelperFragment;
import com.bupocket.utils.SharedPreferencesHelper;
import com.bupocket.wallet.enums.CreateWalletStepEnum;

import java.util.ArrayList;

public class BPMainActivity extends BaseFragmentActivity {
    private static final String KEY_FRAGMENT = "key_fragment";
    private static final int VALUE_FRAGMENT_HOME = 0;
    private static final int VALUE_FRAGMENT_NOTCH_HELPER = 1;
    private SharedPreferencesHelper sharedPreferencesHelper;
    @Override
    protected int getContextViewId() {
        return R.id.asset_LinearLayout;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferencesHelper = new SharedPreferencesHelper(BPMainActivity.this, "buPocket");
        if(savedInstanceState == null){
            BaseFragment baseFragment = getFirstFragment();
//            sharedPreferencesHelper.put("currentAccNick", "阿木");
//            sharedPreferencesHelper.put("currentAccAddr", "buQtmZNggmK9kJ7c5NVjpJugX3ZK1wYPH9P8");
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(getContextViewId(), baseFragment, baseFragment.getClass().getSimpleName())
                    .addToBackStack(baseFragment.getClass().getSimpleName())
                    .commit();
        }
    }

    private BaseFragment getFirstFragment() {
        Intent intent = getIntent();
        int ret = intent.getIntExtra(KEY_FRAGMENT, 0);
        BaseFragment fragment;
        if (ret == VALUE_FRAGMENT_NOTCH_HELPER) {
            fragment = new QDNotchHelperFragment();
        } else {

            // 检查本地是否第一次创建钱包
            String isFirstCreateWallet = sharedPreferencesHelper.getSharedPreference("isFirstCreateWallet", "").toString();
            if(isFirstCreateWallet == ""){
                String createWalletStep = sharedPreferencesHelper.getSharedPreference("createWalletStep","").toString();
                if(CreateWalletStepEnum.CREATE_MNEONIC_CODE.getCode().equals(createWalletStep)){
                    fragment = new BPBackupWalletFragment();
                }else{
                    fragment = new BPCreateWalletFragment();
                }
            }else {
                fragment = new HomeFragment();
            }
        }

        return fragment;
    }

    public static Intent createNotchHelperIntent(Context context) {
        Intent intent = new Intent(context, BPMainActivity.class);
        intent.putExtra(KEY_FRAGMENT, VALUE_FRAGMENT_NOTCH_HELPER);
        return intent;
    }
}
