package com.assetMarket.base;


import android.content.Context;

import com.assetMarket.utils.LocaleUtil;
import com.qmuiteam.qmui.arch.QMUIFragmentActivity;

public abstract class BaseFragmentActivity extends QMUIFragmentActivity {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleUtil.attachBaseContext(base));
    }
}

