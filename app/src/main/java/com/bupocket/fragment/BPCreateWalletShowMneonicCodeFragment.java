package com.bupocket.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.GridLayout;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bupocket.R;
import com.bupocket.base.BaseFragment;
import com.bupocket.model.MnemonicCodeInfo;
import com.bupocket.utils.TO;
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

        mShowMneonicCodeGL.setColumnCount(4);
        mShowMneonicCodeGL.setRowCount(3);
        int index = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                Context mContext = getContext();
                TextView textView = new TextView(mContext);
                textView.setGravity(Gravity.CENTER);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                textView.setText(getMneonicCode().get(index++));
                textView.setTextColor(0xFF36B3FF);
                textView.setBackgroundColor(0xFFF5F5F5);
                GridLayout.Spec rowSpec = GridLayout.spec(i, 1.0f);
                GridLayout.Spec columnSpec = GridLayout.spec(j, 1.0f);
                GridLayout.LayoutParams params = new GridLayout.LayoutParams(rowSpec, columnSpec);
                //左边的靠左，右边的靠右，中间的居中，默认居中
                switch (j) {
                    case 0:
                        params.setGravity(Gravity.LEFT);
                        break;
                    case 1:
                        params.setGravity(Gravity.CENTER);
                        break;
                    case 2:
                        params.setGravity(Gravity.RIGHT);
                        break;
                    default:
                        params.setGravity(Gravity.CENTER);
                        break;
                }
                params.bottomMargin = TO.dip2px(mContext, 11.5f);
                //控制TextView的高度和宽度
                params.height = TO.dip2px(mContext, 25f);;
                params.width = TO.dip2px(mContext, 90f);;
                mShowMneonicCodeGL.addView(textView, params);

            }
        }
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
