package com.bupocket.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayout;
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
import com.bupocket.model.MnemonicCodeInfo;
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
//    @BindView(R.id.showMneonicCodeGL)
//    GridLayout mShowMneonicCodeGL;
    @BindView(R.id.showMneonicCodeQMUIFL)
    QMUIFloatLayout mShowMneonicCodeQMUIFl;
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
        List<String> mnemonicCodeList = getMneonicCode();
        Collections.shuffle(mnemonicCodeList);

        for (int i = 0; i < 12; i++) {
            addItemToFloatLayout(mShowMneonicCodeQMUIFl,mnemonicCodeList.get(i));
        }

    }

    private void addItemToFloatLayout(QMUIFloatLayout floatLayout, String text) {

        TextView textView = new TextView(getActivity());
        int textViewPadding = QMUIDisplayHelper.dp2px(getContext(), 4);
        textView.setPadding(textViewPadding, textViewPadding, textViewPadding, textViewPadding);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        textView.setTextColor(ContextCompat.getColor(getContext(), R.color.qmui_config_color_white));
        textView.setText(text);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(0xFF36B3FF);
        textView.setBackgroundColor(0xFFF5F5F5);
        int textViewW = QMUIDisplayHelper.dpToPx(80);
        int textViewH = QMUIDisplayHelper.dpToPx(50);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(textViewW, textViewH);
        floatLayout.addView(textView, lp);
    }
    private void initTopBar() {
        mTopBar.addLeftImageButton(R.mipmap.icon_tobar_left_arrow, R.id.topbar_left_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        });
    }

}
