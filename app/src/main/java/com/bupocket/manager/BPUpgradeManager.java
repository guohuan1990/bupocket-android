package com.bupocket.manager;

import android.content.Context;
import com.vector.update_app.UpdateAppManager;

public class BPUpgradeManager {
    public static final int INVALIDATE_VERSION_CODE = -1;
    private static BPUpgradeManager bpUpgradeManager;
    private Context mContext;

    public BPUpgradeManager(Context mContext) {
        this.mContext = mContext;
    }

    public static final BPUpgradeManager getInstance(Context context){
        if(bpUpgradeManager == null){
            bpUpgradeManager = new BPUpgradeManager(context);
        }
        return bpUpgradeManager;
    }

    private void init(){

    }
    public void check(){
        int oldVersion = BPPreferenceManager.getInstance(mContext).getVersionCode();
        int currentVersion = 0;
    }
}
