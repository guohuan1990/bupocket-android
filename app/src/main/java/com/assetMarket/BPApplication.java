package com.assetMarket;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;

import com.assetMarket.common.Constants;
import com.assetMarket.enums.BackupTipsStateEnum;
import com.assetMarket.enums.BumoNodeEnum;
import com.assetMarket.http.api.RetrofitFactory;
import com.assetMarket.utils.LocaleUtil;
import com.assetMarket.utils.SharedPreferencesHelper;
import com.assetMarket.utils.SocketUtil;
import com.assetMarket.wallet.Wallet;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.squareup.leakcanary.LeakCanary;

public class BPApplication extends Application {
    @SuppressLint("StaticFieldLeak")
    private static Context context;

    public static QMUIBottomSheet confirmPasswordDialog;

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);
        LocaleUtil.changeAppLanguage(context);
        switchNetConfig(null);
        SharedPreferencesHelper sharedPreferencesHelper = new SharedPreferencesHelper(context,"buPocket");
        sharedPreferencesHelper.put("backupTipsState", BackupTipsStateEnum.SHOW.getCode());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.e("TAG", "onConfigurationChanged");
        LocaleUtil.setLanguage(context, newConfig);
    }

    public static void switchNetConfig(String netType){
        RetrofitFactory.getInstance().setNull4Retrofit();
        Wallet.getInstance().setNull4Wallet();
        SocketUtil.getInstance().SetNull4SocketUtil();
        SharedPreferencesHelper sharedPreferencesHelper = new SharedPreferencesHelper(context,"buPocket");
        sharedPreferencesHelper.put("tokensInfoCache","");
        sharedPreferencesHelper.put("tokenBalance","");
        int netTypeCode = sharedPreferencesHelper.getInt("bumoNode", Constants.DEFAULT_BUMO_NODE);
        Boolean isMainNetConfig = true;
        if(BumoNodeEnum.TEST.getCode() == netTypeCode){
            isMainNetConfig = false;
        }else if(BumoNodeEnum.TEST.getName().equals(netType)){
            isMainNetConfig = false;
        }

        if(isMainNetConfig){
            Constants.BUMO_NODE_URL = Constants.MainNetConfig.BUMO_NODE_URL.getValue();
            Constants.PUSH_MESSAGE_SOCKET_URL = Constants.MainNetConfig.PUSH_MESSAGE_SOCKET_URL.getValue();
            Constants.WEB_SERVER_DOMAIN = Constants.MainNetConfig.WEB_SERVER_DOMAIN.getValue();
        }else {
            Constants.BUMO_NODE_URL = Constants.TestNetConfig.BUMO_NODE_URL.getValue();
            Constants.PUSH_MESSAGE_SOCKET_URL = Constants.TestNetConfig.PUSH_MESSAGE_SOCKET_URL.getValue();
            Constants.WEB_SERVER_DOMAIN = Constants.TestNetConfig.WEB_SERVER_DOMAIN.getValue();
        }
    }
}
