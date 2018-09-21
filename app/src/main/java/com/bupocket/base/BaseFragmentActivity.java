package com.bupocket.base;


import android.app.Activity;
import android.os.Bundle;

import com.qmuiteam.qmui.arch.QMUIFragmentActivity;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseFragmentActivity extends QMUIFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManage.addActivity(this);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManage.removeActivity(this);
    }

}

