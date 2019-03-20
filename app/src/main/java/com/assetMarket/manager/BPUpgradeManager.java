package com.assetMarket.manager;

import android.app.Activity;
import com.alibaba.fastjson.JSON;
import com.assetMarket.common.Constants;
import com.assetMarket.R;
import com.assetMarket.http.api.dto.resp.GetCurrentVersionRespDto;
import com.assetMarket.utils.CommonUtil;
import com.assetMarket.utils.UpdateAppHttpUtil;
import com.vector.update_app.UpdateAppBean;
import com.vector.update_app.UpdateAppManager;
import com.vector.update_app.UpdateCallback;

public class BPUpgradeManager {
    public static final int INVALIDATE_VERSION_CODE = -1;
    private static BPUpgradeManager bpUpgradeManager;
    private Activity mActivity;

    public BPUpgradeManager(Activity mActivity) {
        this.mActivity = mActivity;
    }

    public static final BPUpgradeManager getInstance(Activity mActivity){
        if(bpUpgradeManager == null){
            bpUpgradeManager = new BPUpgradeManager(mActivity);
        }
        return bpUpgradeManager;
    }

    public void init(){
        new UpdateAppManager
                .Builder()
                .setActivity(mActivity)
                .setThemeColor(0xFF02CA71)
                .setUpdateUrl(Constants.WEB_SERVER_DOMAIN)
                .setHttpManager(new UpdateAppHttpUtil())
                .setTopPic(R.mipmap.upgrade_dialog_bg)
                .build()
                .checkNewApp(new UpdateCallback(){
                    @Override
                    protected UpdateAppBean parseJson(String json) {
                        GetCurrentVersionRespDto resp = JSON.parseObject(json, GetCurrentVersionRespDto.class);

                        UpdateAppBean updateAppBean = new UpdateAppBean();
                        updateAppBean
                                .setUpdate(check(resp))
                                .setNewVersion(resp.getVerNumber())
                                .setApkFileUrl(resp.getDownloadLink())
                                .setTargetSize(CommonUtil.isNull(resp.getAppSize()) ? "" : resp.getAppSize())
                                .setUpdateLog(resp.getVerContents())
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
//                        CProgressDialogUtils.showProgressDialog(mActivity);
                    }

                    /**
                     * 网路请求之后
                     */
                    @Override
                    public void onAfter() {
//                        CProgressDialogUtils.cancelProgressDialog(mActivity);
                    }

                });
    }
    private String check(GetCurrentVersionRespDto resp){
        int oldVersion = CommonUtil.packageCode(mActivity.getBaseContext());
        if(resp.getVerNumberCode() != null){
            int currentVersion = Integer.parseInt(resp.getVerNumberCode());
            if(currentVersion > oldVersion){
                return "Yes";
            }
        }
        return "No";
    }
}
