package com.assetMarket.fragment;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.assetMarket.R;
import com.assetMarket.base.BaseFragment;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BPCardPublishBuyADFragment extends BaseFragment {
    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.publishBuyADBtn)
    QMUIRoundButton mPublishBuyADBtn;
    @BindView(R.id.publishBuyADEt)
    EditText mPublishBuyADEt;
    @BindView(R.id.unitPriceEt)
    EditText mUnitPriceEt;
    @BindView(R.id.buyAmountET)
    EditText mBuyAmountET;
    @BindView(R.id.cardNameTv)
    TextView mCardNameTv;
    @BindView(R.id.cardLogoIv)
    QMUIRadiusImageView mCardLogoIv;


    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_card_publish_buy_ad, null);
        ButterKnife.bind(this, root);
        init();
        return root;
    }

    private void init() {
        initData();
        initUI();
        buildTextWatcher();
        setListener();
    }

    private void setListener() {
        mPublishBuyADBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateData()){
                    showPasswordConfirmDialog();
                }
            }
        });
    }

    private void buildTextWatcher() {
        TextWatcher textWatcher = new TextWatcher() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mPublishBuyADBtn.setEnabled(false);
                mPublishBuyADBtn.setBackground(getResources().getDrawable(R.drawable.radius_button_disable_bg));
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
    }

    private void initData() {

    }

    private void initUI() {
        initTopBar();
        initView();
    }

    private void initView() {

    }

    private boolean validateData() {

        return true;
    }

    private void showPasswordConfirmDialog() {

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
