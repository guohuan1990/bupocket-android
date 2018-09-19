package com.bupocket;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import com.squareup.leakcanary.LeakCanary;
import com.vector.update_app.UpdateAppManager;

public class BPApplication extends Application {
    @SuppressLint("StaticFieldLeak")
    private static Context context;

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

//        BPUpgradeManager.getInstance(this).check();


    }

}
