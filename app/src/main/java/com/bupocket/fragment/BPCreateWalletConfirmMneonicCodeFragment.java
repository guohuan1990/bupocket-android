package com.bupocket.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.support.v7.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.alibaba.fastjson.JSON;
import com.bupocket.R;
import com.bupocket.base.BaseFragment;
import com.bupocket.fragment.home.HomeFragment;
import com.bupocket.utils.SharedPreferencesHelper;
import com.bupocket.utils.TO;
import com.bupocket.wallet.enums.CreateWalletStepEnum;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BPCreateWalletConfirmMneonicCodeFragment extends BaseFragment {
    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.confirmMneonicCodeGL)
    GridLayout mConfirmMneonicCodeGL;
    @BindView(R.id.mnemonicCodeListSelected)
    TextView mMnemonicCodeListSelected;
    @BindView(R.id.completeMnemonicCodeBtn)
    QMUIRoundButton mCompleteMnemonicCodeBtn;

    private SharedPreferencesHelper sharedPreferencesHelper;
    private List<String> srcMnemonicCodeList = new ArrayList<>();

    private List<String> mnemonicCodeList;
    private List<String> mnemonicCodeListSelected = new ArrayList<>();
    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_create_wallet_confirm_mneonic_code, null);
        ButterKnife.bind(this, root);
        initData();
        initTopBar();
        printMnemonicCode();
        mCompleteMnemonicCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 校验助记词是否合法
                if(mnemonicCodeListSelected.size() < 12){
                    Toast.makeText(getActivity(), R.string.check_mneonic_code_err1,Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!mnemonicCodeListSelected.equals(srcMnemonicCodeList)){
                    Toast.makeText(getActivity(), R.string.check_mneonic_code_err1,Toast.LENGTH_SHORT).show();
                    return;
                }
                sharedPreferencesHelper.put("createWalletStep", CreateWalletStepEnum.BACKUPED_MNEONIC_CODE.getCode());
                sharedPreferencesHelper.put("isFirstCreateWallet", "0");
                startFragment(new HomeFragment());
            }
        });

        return root;
    }

    private void getMneonicCode(){
        srcMnemonicCodeList = getArguments().getStringArrayList("mneonicCodeList");
        mnemonicCodeList = new ArrayList<>(srcMnemonicCodeList);
        Collections.shuffle(mnemonicCodeList);
        System.out.println("srcMnemonicCodeList:" + JSON.toJSONString(srcMnemonicCodeList));
        System.out.println("mnemonicCodeList:" + JSON.toJSONString(mnemonicCodeList));
    }

    private void initData(){
        sharedPreferencesHelper = new SharedPreferencesHelper(getContext(), "buPocket");
        getMneonicCode();
    }
    private void printMnemonicCode() {


        mConfirmMneonicCodeGL.setColumnCount(4);
        mConfirmMneonicCodeGL.setRowCount(3);
        for (int i = 0; i < 12; i++) {
            Context mContext = getContext();
            TextView textView = new TextView(mContext);
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            textView.setText(mnemonicCodeList.get(i));
            textView.setTextColor(0xFF36B3FF);
            textView.setBackgroundColor(0xFFF5F5F5);
            textView.setOnClickListener(clickListener);
            GridLayout.Spec rowSpec = GridLayout.spec(i / 4, 1.0f);
            GridLayout.Spec columnSpec = GridLayout.spec(i % 4, 1.0f);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams(rowSpec, columnSpec);

            params.height = TO.dip2px(mContext, 40f);
            params.width = TO.dip2px(mContext, 60f);
            params.bottomMargin = TO.dip2px(mContext, 10f);
            params.leftMargin = TO.dip2px(mContext, 5f);
            params.rightMargin = TO.dip2px(mContext, 5f);
            mConfirmMneonicCodeGL.addView(textView, params);
        }
    }



    private void printMneonicCodeSelected(){
        StringBuffer sb = new StringBuffer();
        for (String code: mnemonicCodeListSelected
             ) {
            sb.append(code + "\t\t");
        }
        mMnemonicCodeListSelected.setText(sb.toString());
    }

    private void addMneonicCodeBtn(String text){
        TextView textView = new TextView(getContext());
        textView.setText(text);
        mConfirmMneonicCodeGL.addView(textView);
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onClick(View v) {
            TextView textView = (TextView)v;
            String code = textView.getText().toString();
            if(!mnemonicCodeListSelected.contains(code)){
                textView.setTextColor(0xFFFFFFFF);
                textView.setBackgroundColor(0xFF36B3FF);
                mnemonicCodeListSelected.add(code);
            } else {
                textView.setTextColor(0xFF36B3FF);
                textView.setBackgroundColor(0xFFF5F5F5);
                mnemonicCodeListSelected.remove(code);
            }
            printMneonicCodeSelected();

            if(mnemonicCodeListSelected.size() == 12 && mnemonicCodeListSelected.equals(srcMnemonicCodeList)){
                mCompleteMnemonicCodeBtn.setEnabled(true);
                mCompleteMnemonicCodeBtn.setBackground(getResources().getDrawable(R.drawable.radius_button_able_bg));
            }else{
                mCompleteMnemonicCodeBtn.setEnabled(false);
                mCompleteMnemonicCodeBtn.setBackground(getResources().getDrawable(R.drawable.radius_button_disable_bg));
            }
            System.out.println("ssss:" + textView.getText());
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
