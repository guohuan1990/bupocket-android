package com.bupocket.fragment;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.support.v7.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bupocket.R;
import com.bupocket.base.BaseFragment;
import com.bupocket.fragment.home.HomeFragment;
import com.bupocket.utils.SharedPreferencesHelper;
import com.bupocket.utils.TO;
import com.bupocket.wallet.enums.CreateWalletStepEnum;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import java.util.Collections;
import java.util.List;

public class BPCreateWalletConfirmMneonicCodeFragment extends BaseFragment {
    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.confirmMneonicCodeGL)
    GridLayout mConfirmMneonicCodeGL;

    @BindView(R.id.columns_linear)
    LinearLayout mL;

    @BindView(R.id.completeMnemonicCodeBtn)
    QMUIRoundButton mCompleteMnemonicCodeBtn;
    private SharedPreferencesHelper sharedPreferencesHelper;
    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_create_wallet_confirm_mneonic_code, null);
        ButterKnife.bind(this, root);
        initData();
        initTopBar();
        printMnemonicCode();


        for (int i = 0; i < 12; i++) {
            Context mContext = getContext();
            TextView textView = new TextView(mContext);
            textView.setPadding(TO.dip2px(mContext, 15f), 0, 0, 0);
            textView.setGravity(Gravity.CENTER_VERTICAL);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            textView.setText(getMneonicCode().get(i));
            textView.setTextColor(0xFF36B3FF);
            textView.setBackgroundColor(0xFFF5F5F5);
            textView.setOnClickListener(clickListener);

            mL.addView(textView);
        }

        mCompleteMnemonicCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPreferencesHelper.put("createWalletStep", CreateWalletStepEnum.BACKUPED_MNEONIC_CODE.getCode());
                sharedPreferencesHelper.put("isFirstCreateWallet", "0");
                startFragment(new HomeFragment());
            }
        });

        return root;
    }

    private List<String> getMneonicCode(){
        return getArguments().getStringArrayList("mneonicCodeList");
    }

    private void initData(){
        sharedPreferencesHelper = new SharedPreferencesHelper(getContext(), "buPocket");
    }
    private void printMnemonicCode() {
        List<String> mnemonicCodeList = getMneonicCode();
        Collections.shuffle(mnemonicCodeList);

        mConfirmMneonicCodeGL.setColumnCount(4);
        mConfirmMneonicCodeGL.setRowCount(3);
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
                textView.setOnClickListener(clickListener);
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
                mConfirmMneonicCodeGL.addView(textView, params);

            }
        }


    }

    private void addMneonicCodeBtn(String text){
        TextView textView = new TextView(getContext());
        textView.setText(text);
        mConfirmMneonicCodeGL.addView(textView);
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            System.out.println(((TextView)v).getText());
        }
    };





    private void initTopBar() {
        mTopBar.addLeftImageButton(R.mipmap.icon_tobar_left_arrow, R.id.topbar_left_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        });
    }


}
