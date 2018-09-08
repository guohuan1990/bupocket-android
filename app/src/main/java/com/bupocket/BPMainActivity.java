package com.bupocket;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.bupocket.base.BaseFragment;
import com.bupocket.base.BaseFragmentActivity;
import com.bupocket.fragment.BPCreateWalletStepOne;
import com.bupocket.fragment.home.HomeFragment;
import com.bupocket.fragment.home.util.QDNotchHelperFragment;

public class BPMainActivity extends BaseFragmentActivity {
    private static final String KEY_FRAGMENT = "key_fragment";
    private static final int VALUE_FRAGMENT_HOME = 0;
    private static final int VALUE_FRAGMENT_NOTCH_HELPER = 1;
    @Override
    protected int getContextViewId() {
        return R.id.asset_LinearLayout;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState == null){
            BaseFragment baseFragment = getFirstFragment();
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
            fragment = new HomeFragment();
        }

        return fragment;
    }

    public static Intent createNotchHelperIntent(Context context) {
        Intent intent = new Intent(context, BPMainActivity.class);
        intent.putExtra(KEY_FRAGMENT, VALUE_FRAGMENT_NOTCH_HELPER);
        return intent;
    }
}
