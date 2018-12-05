package com.bupocket.fragment;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bupocket.R;
import com.bupocket.base.BaseFragment;
import com.bupocket.enums.ExceptionEnum;
import com.bupocket.http.api.AssetService;
import com.bupocket.http.api.RetrofitFactory;
import com.bupocket.http.api.dto.resp.ApiResult;
import com.bupocket.http.api.dto.resp.BlobInfoDto;
import com.bupocket.http.api.dto.resp.PublishAdRespDto;
import com.bupocket.model.AssetInfo;
import com.bupocket.utils.CommonUtil;
import com.bupocket.utils.SharedPreferencesHelper;
import com.bupocket.wallet.Wallet;
import com.bupocket.wallet.model.WalletSignData;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BPCardPublishSellADFragment extends BaseFragment {
    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.publishSellADEt)
    EditText mPublishSellADEt;
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

    private SharedPreferencesHelper sharedPreferencesHelper;

    private QMUITipDialog publishTipDialog;
    private QMUIDialog showPwdQmuiDialog;

    private String numberAvailable;
    private String cardName;
    private String issuerLogo;
    private String userToken;
    private String issuerAddress;
    private String assetCode;
    private String publishADTxBlob;
    private String publishADTxHash;
    private String publishADBlobId;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_card_publish_sell_ad, null);
        QMUIStatusBarHelper.setStatusBarLightMode(getBaseFragmentActivity());
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
                    showPasswordConfirmDialog();
                }
            }
        });
    }

    private void showPasswordConfirmDialog() {
        final String unitPrice = mUnitPriceEt.getText().toString();
        final String sellAmount = mSellAmountEt.getText().toString();
        final String adTitle = mPublishSellADEt.getText().toString();
        showPwdQmuiDialog = new QMUIDialog(getContext());
        showPwdQmuiDialog.setCanceledOnTouchOutside(false);
        showPwdQmuiDialog.setContentView(R.layout.password_comfirm_layout);
        showPwdQmuiDialog.show();


        QMUIRoundButton mPasswordConfirmBtn = showPwdQmuiDialog.findViewById(R.id.passwordConfirmBtn);

        ImageView mPasswordConfirmCloseBtn = showPwdQmuiDialog.findViewById(R.id.passwordConfirmCloseBtn);

        mPasswordConfirmCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPwdQmuiDialog.dismiss();
            }
        });

        mPasswordConfirmBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // 检查合法性
                EditText mPasswordConfirmEt = showPwdQmuiDialog.findViewById(R.id.passwordConfirmEt);
                final String password = mPasswordConfirmEt.getText().toString().trim();
                publishTipDialog = new QMUITipDialog.Builder(getContext())
                        .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                        .setTipWord(getResources().getString(R.string.send_tx_handleing_txt))
                        .create();
                publishTipDialog.show();
                publishTipDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

                        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
                            return true;
                        }
                        return false;
                    }
                });
                buildPublishSellAdTxBlob(userToken,unitPrice,sellAmount,adTitle,issuerAddress,assetCode,password);
                showPwdQmuiDialog.dismiss();
            }
        });
    }

    private void buildPublishSellAdTxBlob(String userToken, String unitPrice, String sellAmount, String adTitle, String issuerAddress, String assetCode, final String password) {
        AssetService assetService = RetrofitFactory.getInstance().getRetrofit().create(AssetService.class);
        Map<String,Object> paramsMap = new HashMap<>();
        paramsMap.put("userToken",userToken);
        paramsMap.put("price",unitPrice);
        paramsMap.put("sellAmount",sellAmount);
        paramsMap.put("advertTitle",adTitle);
        AssetInfo assetInfo = new AssetInfo();
        assetInfo.setCode(assetCode);
        assetInfo.setIssuerAddress(issuerAddress);
        paramsMap.put("asset",assetInfo);

        Call<ApiResult<BlobInfoDto>> call = assetService.getBlob(paramsMap);
        call.enqueue(new Callback<ApiResult<BlobInfoDto>>() {
            @Override
            public void onResponse(Call<ApiResult<BlobInfoDto>> call, Response<ApiResult<BlobInfoDto>> response) {
                ApiResult<BlobInfoDto> respDto = response.body();
                if(respDto != null){
                    if(ExceptionEnum.SUCCESS.getCode().equals(respDto.getErrCode())){
                        BlobInfoDto blobInfoDto = respDto.getData();
                        publishADTxBlob = blobInfoDto.getTxBlob();
                        publishADTxHash = blobInfoDto.getTxHash();
                        publishADBlobId = blobInfoDto.getBlobId();
                        publishSellAd(password);
                    }else if(ExceptionEnum.USER_TOKEN_ERR.getCode().equals(respDto.getErrCode())){
                        publishTipDialog.dismiss();
                        showPwdQmuiDialog.dismiss();
                        Toast.makeText(getContext(),getString(R.string.card_package_user_token_error_message_txt),Toast.LENGTH_LONG).show();
                    }else{
                        publishTipDialog.dismiss();
                        showPwdQmuiDialog.dismiss();
                        Toast.makeText(getContext(),getString(R.string.network_error_msg),Toast.LENGTH_LONG).show();
                    }
                } else {
                    publishTipDialog.dismiss();
                    showPwdQmuiDialog.dismiss();
                    Toast.makeText(getContext(),"Null",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResult<BlobInfoDto>> call, Throwable t) {
                publishTipDialog.dismiss();
                showPwdQmuiDialog.dismiss();
                Toast.makeText(getContext(),getString(R.string.network_error_msg),Toast.LENGTH_LONG).show();
                t.printStackTrace();
            }
        });
    }

    private void publishSellAd(final String password) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String bpData = getAccountBPData();
                    final WalletSignData walletSignData = Wallet.getInstance().signTxBlob(password, publishADTxBlob, bpData);
                    if(walletSignData != null){
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                AssetService assetService = RetrofitFactory.getInstance().getRetrofit().create(AssetService.class);
                                Map<String, Object> paramsMap = new HashMap<>();
                                paramsMap.put("txBlob", publishADTxBlob);
                                paramsMap.put("txHash", publishADTxHash);
                                paramsMap.put("blobId", publishADBlobId);
                                paramsMap.put("txBlobSign", walletSignData.getSignData());
                                paramsMap.put("publicKey", walletSignData.getPublicKey());
                                paramsMap.put("userToken", userToken);

                                Call<ApiResult<PublishAdRespDto>> call = assetService.publishSellAd(paramsMap);

                                call.enqueue(new Callback<ApiResult<PublishAdRespDto>>() {
                                    @Override
                                    public void onResponse(Call<ApiResult<PublishAdRespDto>> call, Response<ApiResult<PublishAdRespDto>> response) {
                                        showPwdQmuiDialog.dismiss();
                                        ApiResult<PublishAdRespDto> respDto = response.body();
                                        if(ExceptionEnum.SUCCESS.getCode().equals(respDto.getErrCode())){
                                            publishTipDialog.dismiss();
                                            Toast.makeText(getActivity(), R.string.card_package_publish_success_message_txt, Toast.LENGTH_SHORT).show();
                                            popBackStack();
                                            /*BPCardDetailsFragment bpCardDetailsFragment = new BPCardDetailsFragment();
                                            Bundle argz = new Bundle();
                                            argz.putString("issuerAddress", issuerAddress);
                                            argz.putString("assetCode", assetCode);
                                            Toast.makeText(getContext(),getString(R.string.card_package_publish_success_message_txt),Toast.LENGTH_LONG).show();
                                            bpCardDetailsFragment.setArguments(argz);
                                            startFragmentAndDestroyCurrent(bpCardDetailsFragment);*/
                                        }else if(ExceptionEnum.USER_TOKEN_ERR.getCode().equals(respDto.getErrCode())){
                                            publishTipDialog.dismiss();
                                            Toast.makeText(getContext(),getString(R.string.card_package_user_token_error_message_txt),Toast.LENGTH_LONG).show();
                                        }else {
                                            publishTipDialog.dismiss();
                                            Toast.makeText(getContext(),getString(R.string.network_error_msg),Toast.LENGTH_LONG).show();
                                        }

                                    }

                                    @Override
                                    public void onFailure(Call<ApiResult<PublishAdRespDto>> call, Throwable t) {
                                        showPwdQmuiDialog.dismiss();
                                        publishTipDialog.dismiss();
                                        Toast.makeText(getContext(),getString(R.string.network_error_msg),Toast.LENGTH_LONG).show();
                                        t.printStackTrace();
                                    }
                                });
                            }
                        });
                    }
                } catch (Exception e) {
                    showPwdQmuiDialog.dismiss();
                    e.printStackTrace();
                    Looper.prepare();
                    Toast.makeText(getActivity(), R.string.checking_password_error, Toast.LENGTH_SHORT).show();
                    publishTipDialog.dismiss();
                    Looper.loop();
                }
            }
        }).start();
        showPwdQmuiDialog.dismiss();
    }

    private String getAccountBPData() {
        return sharedPreferencesHelper.getSharedPreference("BPData", "").toString();
    }

    private boolean validateData() {
        String sellAmount = mSellAmountEt.getText().toString();
        if(mPublishSellADEt.getText().length() < 2 || mPublishSellADEt.getText().length() > 15){
            Toast.makeText(getActivity(), R.string.card_package_title_error_message_txt, Toast.LENGTH_SHORT).show();
            return false;
        }
        if(Integer.valueOf(sellAmount) > Integer.valueOf(numberAvailable)){
            Toast.makeText(getActivity(), R.string.card_package_sell_amount_not_enough_message_txt, Toast.LENGTH_SHORT).show();
            return false;
        }
        if(Integer.valueOf(sellAmount) <= 0){
            Toast.makeText(getActivity(), R.string.err_canout_zero, Toast.LENGTH_SHORT).show();
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
                boolean title = mPublishSellADEt.getText().length() > 0;
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
        mPublishSellADEt.addTextChangedListener(textWatcher);
        mUnitPriceEt.addTextChangedListener(textWatcher);
        mSellAmountEt.addTextChangedListener(textWatcher);
    }

    private void initData() {
        Bundle argz = getArguments();
        cardName = argz.getString("cardName");
        issuerLogo = argz.getString("issuerLogo");
        numberAvailable = argz.getString("numberAvailable");
        issuerAddress = argz.getString("issuerAddress");
        assetCode = argz.getString("assetCode");
        sharedPreferencesHelper = new SharedPreferencesHelper(getContext(), "buPocket");
        userToken = sharedPreferencesHelper.getSharedPreference("userToken","").toString();
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
