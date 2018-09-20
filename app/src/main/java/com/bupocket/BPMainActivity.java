package com.bupocket;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.widget.Toast;
import com.alibaba.fastjson.JSON;
import com.bupocket.base.BaseFragment;
import com.bupocket.base.BaseFragmentActivity;
import com.bupocket.fragment.BPAssetsFragment;
import com.bupocket.fragment.BPBackupWalletFragment;
import com.bupocket.fragment.BPCreateWalletFragment;
import com.bupocket.fragment.home.HomeFragment;
import com.bupocket.http.api.dto.resp.GetCurrentVersionRespDto;
import com.bupocket.manager.BPUpgradeManager;
import com.bupocket.utils.CProgressDialogUtils;
import com.bupocket.utils.SharedPreferencesHelper;
import com.bupocket.utils.UpdateAppHttpUtil;
import com.bupocket.wallet.Constants;
import com.bupocket.wallet.enums.CreateWalletStepEnum;
import com.vector.update_app.UpdateAppBean;
import com.vector.update_app.UpdateAppManager;
import com.vector.update_app.UpdateCallback;

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

        setStrictMode();
        sharedPreferencesHelper = new SharedPreferencesHelper(BPMainActivity.this, "buPocket");
        if(savedInstanceState == null){
            BaseFragment baseFragment = getFirstFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(getContextViewId(), baseFragment, baseFragment.getClass().getSimpleName())
                    .addToBackStack(baseFragment.getClass().getSimpleName())
                    .commit();
        }
        BPUpgradeManager.getInstance(this).init();
    }

    private void setStrictMode() {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads().detectDiskWrites().detectNetwork()
                .penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
                .penaltyLog().penaltyDeath().build());
    }

    private BaseFragment getFirstFragment() {
        BaseFragment fragment;
        // 检查本地是否第一次创建钱包
        String isFirstCreateWallet = sharedPreferencesHelper.getSharedPreference("isFirstCreateWallet", "").toString();
        if(isFirstCreateWallet == ""){
            String createWalletStep = sharedPreferencesHelper.getSharedPreference("createWalletStep","").toString();
            if(CreateWalletStepEnum.CREATE_MNEONIC_CODE.getCode().equals(createWalletStep)){
                fragment = new BPBackupWalletFragment();
            } else if(CreateWalletStepEnum.BACKUPED_MNEONIC_CODE.getCode().equals(createWalletStep)){
                fragment = new HomeFragment();
            }else{
                fragment = new BPCreateWalletFragment();
            }
        }else {
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
