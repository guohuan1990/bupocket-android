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
import com.bupocket.utils.CProgressDialogUtils;
import com.bupocket.utils.SharedPreferencesHelper;
import com.bupocket.utils.UpdateAppHttpUtil;
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
        updateApp();
        return fragment;
    }


    public void updateApp(){
        new UpdateAppManager
            .Builder()
            //当前Activity
            .setActivity(this)
            //更新地址
            .setUpdateUrl("http://www.baidu.com")
            //实现httpManager接口的对象
            .setHttpManager(new UpdateAppHttpUtil())
            .build()
            .checkNewApp(new UpdateCallback(){
                @Override
                protected UpdateAppBean parseJson(String json) {
                    GetCurrentVersionRespDto resp = JSON.parseObject(json, GetCurrentVersionRespDto.class);

                    UpdateAppBean updateAppBean = new UpdateAppBean();
                    updateAppBean
                            .setUpdate("Yes")
                            .setNewVersion(resp.getVerNumber())
                            .setApkFileUrl(resp.getDownloadLink())
                            .setTargetSize("10.83M")
                            .setUpdateLog("1，添加删除信用卡接口\n2，添加vip认证\n3，区分自定义消费，一个小时不限制。\n4，添加放弃任务接口，小时内不生成。\n5，消费任务手动生成")
                            .setConstraint(resp.getVerType() == 0 ? false : true);

                    return updateAppBean;
                }

                @Override
                public void hasNewApp(UpdateAppBean updateApp, UpdateAppManager updateAppManager) {
                    if (updateApp.isConstraint()) {

                    } else {

                    }
                    updateAppManager.showDialogFragment();
                }
                /**
                 * 网络请求之前
                 */
                @Override
                public void onBefore() {
                    CProgressDialogUtils.showProgressDialog(BPMainActivity.this);
                }

                /**
                 * 网路请求之后
                 */
                @Override
                public void onAfter() {
                    CProgressDialogUtils.cancelProgressDialog(BPMainActivity.this);
                }

            });
    }

    public static Intent createNotchHelperIntent(Context context) {
        Intent intent = new Intent(context, BPMainActivity.class);
        intent.putExtra(KEY_FRAGMENT, VALUE_FRAGMENT_NOTCH_HELPER);
        return intent;
    }
}
