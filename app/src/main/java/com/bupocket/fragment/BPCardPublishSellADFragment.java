package com.bupocket.fragment;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bupocket.R;
import com.bupocket.base.BaseFragment;
import com.bupocket.utils.CommonUtil;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BPCardPublishSellADFragment extends BaseFragment {
    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.publishBuyADEt)
    EditText mPublishBuyADEt;
    @BindView(R.id.unitPriceEt)
    EditText mUnitPriceEt;
    @BindView(R.id.sellAmountEt)
    EditText mSellAmountEt;
    @BindView(R.id.publishSellADBtn)
    QMUIRoundButton mPublishSellADBtn;
    @BindView(R.id.numberAvailableTv)
    TextView mNumberAvailableTv;
    @BindView(R.id.cardLogoIv)
    QMUIRadiusImageView mCardLogoIv;
    @BindView(R.id.cardNameTv)
    TextView mCardNameTv;

    private String numberAvailable;
    private String cardName;
    private String issuerLogo;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_card_publish_sell_ad, null);
        ButterKnife.bind(this, root);
        init();
        return root;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void init() {
        initData();
        initUI();
        buildTextWatcher();
        setListener();
    }

    private void setListener() {
        mPublishSellADBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateData()){
                    
                }
            }
        });
    }

    private boolean validateData() {
        String sellAmount = mSellAmountEt.getText().toString();
        if(mPublishBuyADEt.getText().length() < 2 || mPublishBuyADEt.getText().length() > 15){
            Toast.makeText(getActivity(), R.string.card_package_title_error_message_txt, Toast.LENGTH_SHORT).show();
            return false;
        }
        if(Integer.valueOf(sellAmount) > Integer.valueOf(numberAvailable)){
            Toast.makeText(getActivity(), R.string.card_package_sell_amount_not_enough_message_txt, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void buildTextWatcher() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mPublishSellADBtn.setEnabled(false);
                mPublishSellADBtn.setBackground(getResources().getDrawable(R.drawable.radius_button_disable_bg));
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPublishSellADBtn.setEnabled(false);
                mPublishSellADBtn.setBackground(getResources().getDrawable(R.drawable.radius_button_disable_bg));
            }

            @Override
            public void afterTextChanged(Editable s) {
                boolean title = mPublishBuyADEt.getText().length() > 0;
                boolean unitPrice = mUnitPriceEt.getText().length() > 0;
                boolean sellAmount = mSellAmountEt.getText().length() > 0;
                if(title && unitPrice && sellAmount){
                    mPublishSellADBtn.setEnabled(true);
                    mPublishSellADBtn.setBackground(getResources().getDrawable(R.drawable.radius_button_able_bg));
                }else {
                    mPublishSellADBtn.setEnabled(false);
                    mPublishSellADBtn.setBackground(getResources().getDrawable(R.drawable.radius_button_disable_bg));
                }
            }
        };
        mPublishBuyADEt.addTextChangedListener(textWatcher);
        mUnitPriceEt.addTextChangedListener(textWatcher);
        mSellAmountEt.addTextChangedListener(textWatcher);
    }

    private void initData() {
        Bundle argz = getArguments();
        cardName = argz.getString("cardName");
        issuerLogo = argz.getString("issuerLogo");
        numberAvailable = argz.getString("numberAvailable");

    }

    private void initUI() {
        initTopBar();
        initView();
    }

    private void initView() {
        mNumberAvailableTv.setText(numberAvailable+"张");
        mCardNameTv.setText(cardName);
        try{
            mCardLogoIv.setImageBitmap(CommonUtil.base64ToBitmap(issuerLogo));
        }catch (Exception e){
            mCardLogoIv.setImageDrawable(getResources().getDrawable(R.mipmap.avatar));
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
