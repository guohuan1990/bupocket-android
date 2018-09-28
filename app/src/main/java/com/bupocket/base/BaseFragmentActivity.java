package com.bupocket.base;


import android.content.Context;

import com.bupocket.utils.LocaleUtil;
import com.qmuiteam.qmui.arch.QMUIFragmentActivity;

public abstract class BaseFragmentActivity extends QMUIFragmentActivity {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleUtil.attachBaseContext(base));
    }
}

