package com.bupocket.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class BPPreferenceManager {
    private static SharedPreferences sPreferences;
    private static BPPreferenceManager bpPreferenceManager = null;

    private static final String APP_VERSION_CODE = "app_version_code";

    private BPPreferenceManager(Context context) {
        sPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
    }

    public static final BPPreferenceManager getInstance(Context context) {
        if (bpPreferenceManager == null) {
            bpPreferenceManager = new BPPreferenceManager(context);
        }
        return bpPreferenceManager;
    }

    public void setAppVersionCode(int code) {
        final SharedPreferences.Editor editor = sPreferences.edit();
        editor.putInt(APP_VERSION_CODE, code);
        editor.apply();
    }

    public int getVersionCode() {
        return sPreferences.getInt(APP_VERSION_CODE, BPUpgradeManager.INVALIDATE_VERSION_CODE);
    }
}
