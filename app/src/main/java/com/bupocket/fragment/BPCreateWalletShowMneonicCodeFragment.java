package com.bupocket.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayout;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bupocket.R;
import com.bupocket.base.BaseFragment;
import com.bupocket.utils.TO;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.QMUIFloatLayout;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BPCreateWalletShowMneonicCodeFragment extends BaseFragment {
    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.showMneonicCodeGL)
    GridLayout mShowMneonicCodeGL;
//    @BindView(R.id.showMneonicCodeQMUIFL)
//    QMUIFloatLayout mShowMneonicCodeQMUIFl;
    @BindView(R.id.go2ConfirmMneonicCodeBtn)
    QMUIRoundButton mGo2ConfirmMneonicCodeBtn;
    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_create_wallet_show_mneonic_code, null);
        ButterKnife.bind(this, root);
        initTopBar();
        printMnemonicCode();
        submit();
        return root;
    }



    private void submit(){
        mGo2ConfirmMneonicCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BPCreateWalletConfirmMneonicCodeFragment createWalletConfirmMneonicCodeFragment = new BPCreateWalletConfirmMneonicCodeFragment();
                Bundle argz = new Bundle();
                List<String> mneonicCodeList = getArguments().getStringArrayList("mneonicCodeList");
                argz.putStringArrayList("mneonicCodeList", (ArrayList<String>) mneonicCodeList);
                createWalletConfirmMneonicCodeFragment.setArguments(argz);
                startFragment(createWalletConfirmMneonicCodeFragment);
            }
        });
    }


    private List<String> getMneonicCode(){
        return getArguments().getStringArrayList("mneonicCodeList");
    }

    private void printMnemonicCode() {
        mShowMneonicCodeGL.setColumnCount(4);
        mShowMneonicCodeGL.setRowCount(3);
        for (int i = 0; i < 12; i++) {
            Context mContext = getContext();
            TextView textView = new TextView(mContext);
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            textView.setText(getMneonicCode().get(i));
            textView.setTextColor(0xFF36B3FF);
            textView.setBackgroundColor(0xFFF5F5F5);
//            textView.setOnClickListener(clickListener);
            GridLayout.Spec rowSpec = GridLayout.spec(i / 4, 1.0f);
            GridLayout.Spec columnSpec = GridLayout.spec(i % 4, 1.0f);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams(rowSpec, columnSpec);
            params.height = TO.dip2px(mContext, 40f);
            params.width = TO.dip2px(mContext, 60f);
            params.bottomMargin = TO.dip2px(mContext, 10f);
            params.leftMargin = TO.dip2px(mContext, 5f);
            params.rightMargin = TO.dip2px(mContext, 5f);

            mShowMneonicCodeGL.addView(textView, params);

        }

    }
    private void initTopBar() {
        mTopBar.setBackgroundDividerEnabled(false);
        mTopBar.addLeftImageButton(R.mipmap.icon_tobar_left_arrow, R.id.topbar_left_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        });
    }

}
