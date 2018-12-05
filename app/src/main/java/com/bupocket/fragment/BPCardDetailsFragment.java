package com.bupocket.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bupocket.R;
import com.bupocket.adaptor.CardDetailAskAdapter;
import com.bupocket.adaptor.CardDetailMySellAdapter;
import com.bupocket.base.BaseFragment;
import com.bupocket.common.Constants;
import com.bupocket.enums.CardAdTypeEnum;
import com.bupocket.enums.ExceptionEnum;
import com.bupocket.fragment.home.HomeFragment;
import com.bupocket.http.api.AssetService;
import com.bupocket.http.api.RetrofitFactory;
import com.bupocket.http.api.dto.resp.ApiResult;
import com.bupocket.http.api.dto.resp.GetCardAdBlobRespDto;
import com.bupocket.http.api.dto.resp.GetCardAdDataRespDto;
import com.bupocket.http.api.dto.resp.GetCardDetailsDto;
import com.bupocket.utils.CommonUtil;
import com.bupocket.utils.DecimalCalculate;
import com.bupocket.utils.SharedPreferencesHelper;
import com.bupocket.wallet.Wallet;
import com.bupocket.wallet.exception.WalletException;
import com.bupocket.wallet.model.WalletSignData;
import com.qmuiteam.qmui.widget.QMUIEmptyView;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BPCardDetailsFragment extends BaseFragment {
    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.issueOrganizationNameTv)
    TextView mIssueOrganizationNameTv;
    @BindView(R.id.cardNameTv)
    TextView mCardNameTv;
    @BindView(R.id.numberRemainingTv)
    TextView mNumberRemainingTv;
    @BindView(R.id.mySellLv)
    ListView mMySellLv;
    @BindView(R.id.purchasingInfoLv)
    ListView mPurchasingInfoLv;
    @BindView(R.id.publishSellADBtn)
    QMUIRoundButton mPublishSellADBtn;
    @BindView(R.id.mySellEmptyView)
    QMUIEmptyView mMySellEmptyView;
    @BindView(R.id.purchasingInfoEmptyView)
    QMUIEmptyView mPurchasingInfoEmptyView;
    @BindView(R.id.cardLogoIv)
    QMUIRadiusImageView mCardLogoIv;

    private SharedPreferencesHelper sharedPreferencesHelper;

    private GetCardDetailsDto cardDetailsDto;
    private String cardName;
    private String issueOrganizationName;
    private String numberRemaining;
    private String issuerLogo;
    private String issuerAddress;
    private String assetCode;
    private String currentAccAddress;

    private String totalQuantityTxt = "";
    private Integer buyOrSellQuantity;
    private Integer stockQuantity;
    private double totalAmount;
    private boolean subFlag;
    private boolean addFlag;
    private String adId;
    private String adPrice;
    private String txBlob;
    private String txHash;
    private String blobId;

    private QMUIDialog pwdConfirmDialog;
    private QMUITipDialog txSendingTipDialog;
    private QMUIBottomSheet confirmOperationBtmSheet;

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_card_details, null);
        ButterKnife.bind(this, root);
        init();
        return root;
    }

    private void init() {
        initData();
        initUI();
        setListener();
    }

    private void setListener() {
        mPublishSellADBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BPCardPublishSellADFragment bpCardPublishSellADFragment = new BPCardPublishSellADFragment();
                Bundle argz = new Bundle();
                argz.putString("cardName",cardName);
                argz.putString("issuerLogo",issuerLogo);
                argz.putString("numberAvailable",numberRemaining);
                argz.putString("issuerAddress",issuerAddress);
                argz.putString("assetCode",assetCode);
                bpCardPublishSellADFragment.setArguments(argz);
                startFragment(bpCardPublishSellADFragment);
            }
        });
    }

    private void initData() {
        sharedPreferencesHelper = new SharedPreferencesHelper(getContext(), "buPocket");
        currentAccAddress = sharedPreferencesHelper.getSharedPreference("currentAccAddr", "").toString();
        Bundle argz = getArguments();
        issuerAddress = argz.getString("issuerAddress");
        assetCode = argz.getString("assetCode");
        getCardDetails();
    }

    private void getCardDetails() {
        AssetService assetService = RetrofitFactory.getInstance().getRetrofit().create(AssetService.class);
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("userToken",sharedPreferencesHelper.getSharedPreference("userToken","").toString());
        paramsMap.put("issuerAddress",issuerAddress);
        paramsMap.put("assetCode",assetCode);
        paramsMap.put("saleStartPage","1");
        paramsMap.put("salePageSize","100");
        paramsMap.put("buyStartPage","1");
        paramsMap.put("buyPageSize","100");
        Call<ApiResult<GetCardDetailsDto>> call = assetService.getCardDetails(paramsMap);
        call.enqueue(new Callback<ApiResult<GetCardDetailsDto>>() {
            @Override
            public void onResponse(Call<ApiResult<GetCardDetailsDto>> call, Response<ApiResult<GetCardDetailsDto>> response) {
                ApiResult<GetCardDetailsDto> respDto = response.body();
                System.out.println(JSON.toJSONString(respDto));
                if(respDto != null){
                    System.out.println(JSON.toJSONString(respDto));
                    if(ExceptionEnum.SUCCESS.getCode().equals(respDto.getErrCode())){
                        cardDetailsDto = respDto.getData();
                        issueOrganizationName = cardDetailsDto.getAssetDetail().getIssuer().getName();
                        cardName = cardDetailsDto.getAssetDetail().getAssetInfo().getName();
                        numberRemaining = cardDetailsDto.getAssetDetail().getAssetInfo().getMyAssetQty();
                        issuerLogo = cardDetailsDto.getAssetDetail().getIssuer().getLogo();
                        issuerAddress = cardDetailsDto.getAssetDetail().getIssuer().getAddress();
                        assetCode = cardDetailsDto.getAssetDetail().getAssetInfo().getCode();
                        initView();
                    }else{
                        new QMUIDialog.MessageDialogBuilder(getContext())
                                .setMessage(getString(R.string.network_error_msg))
                                .addAction(R.string.i_knew_btn_txt, new QMUIDialogAction.ActionListener() {
                                    @Override
                                    public void onClick(QMUIDialog dialog, int index) {
                                        dialog.dismiss();
                                        startFragmentAndDestroyCurrent(new HomeFragment());
                                    }
                                })
                                .setCanceledOnTouchOutside(false)
                                .create().show();
                    }
                }else {
                    new QMUIDialog.MessageDialogBuilder(getContext())
                            .setMessage(getString(R.string.network_error_msg))
                            .addAction(R.string.i_knew_btn_txt, new QMUIDialogAction.ActionListener() {
                                @Override
                                public void onClick(QMUIDialog dialog, int index) {
                                    dialog.dismiss();
                                    startFragmentAndDestroyCurrent(new HomeFragment());
                                }
                            })
                            .setCanceledOnTouchOutside(false)
                            .create().show();
                }
            }

            @Override
            public void onFailure(Call<ApiResult<GetCardDetailsDto>> call, Throwable t) {
                Toast.makeText(getContext(),getString(R.string.network_error_msg),Toast.LENGTH_LONG).show();
                t.printStackTrace();
            }
        });
    }

    private void initUI() {
        initTopBar();
    }

    private void initView() {
        mIssueOrganizationNameTv.setText(issueOrganizationName);
        mCardNameTv.setText(cardName);
        mNumberRemainingTv.setText(getString(R.string.number_remaining_txt) + numberRemaining);
        if(CommonUtil.isNull(issuerLogo)){
            mCardLogoIv.setImageDrawable(getResources().getDrawable(R.mipmap.avatar));
        }else {
            try {
                mCardLogoIv.setImageBitmap(CommonUtil.base64ToBitmap(issuerLogo));
            } catch (Exception e) {
                mCardLogoIv.setImageDrawable(getResources().getDrawable(R.mipmap.avatar));
            }
        }

        if(cardDetailsDto.getMySale() != null && 0 != cardDetailsDto.getMySale().size()){
            CardDetailMySellAdapter cardMySellAdapter = new CardDetailMySellAdapter(cardDetailsDto.getMySale(), getContext());
            mMySellLv.setAdapter(cardMySellAdapter);
        }else {
            mMySellEmptyView.show(getResources().getString(R.string.emptyView_mode_desc_no_data), null);
        }

        if(cardDetailsDto.getBuyRequest() != null && 0 != cardDetailsDto.getBuyRequest().size()){
            final CardDetailAskAdapter cardDetailAskAdapter = new CardDetailAskAdapter(cardDetailsDto.getBuyRequest(),getContext());
            mPurchasingInfoLv.setAdapter(cardDetailAskAdapter);
            cardDetailAskAdapter.setOnItemOptBtnListener(new CardDetailAskAdapter.OnItemOptBtnListener() {
                @Override
                public void onClick(int i) {
                    GetCardDetailsDto.BuyRequestBean getCardDetailsDto = (GetCardDetailsDto.BuyRequestBean) cardDetailAskAdapter.getItem(i);
                    showConfirmOperationBottomSheet(getCardDetailsDto);
                }
            });
        }else {
            mPurchasingInfoEmptyView.show(getResources().getString(R.string.emptyView_mode_desc_no_data), null);
        }
    }

    private void showConfirmOperationBottomSheet(final GetCardDetailsDto.BuyRequestBean itemInfo) {
        final QMUIBottomSheet confirmOperationBtmSheet = new QMUIBottomSheet(getContext());
        buyOrSellQuantity = 1;

        stockQuantity = Integer.parseInt(cardDetailsDto.getAssetDetail().getAssetInfo().getMyAssetQty());
        subFlag = true;
        addFlag = true;

        confirmOperationBtmSheet.setContentView(R.layout.card_ad_confirm_layout);
        TextView mAdTitle = confirmOperationBtmSheet.findViewById(R.id.cardAdConfirmTitleTv);
        TextView mAdPrice = confirmOperationBtmSheet.findViewById(R.id.cardAdConfirmPriceTv);
        TextView mAdAssetId = confirmOperationBtmSheet.findViewById(R.id.cardAdConfirmAssetIdTv);
        final TextView mAdTotalQuantity = confirmOperationBtmSheet.findViewById(R.id.cardAdConfirmQuantityTv);
        final TextView mAdTotalQuantitySub = confirmOperationBtmSheet.findViewById(R.id.cardAdConfirmSubQuantityTv);
        final TextView mAdTotalQuantityAdd = confirmOperationBtmSheet.findViewById(R.id.cardAdConfirmAddQuantityTv);
        final TextView mAdTotalQuantityCompute = confirmOperationBtmSheet.findViewById(R.id.cardAdConfirmComputeQuantityTv);
        TextView mAdSellFee = confirmOperationBtmSheet.findViewById(R.id.cardAdConfirmSellFeeTv);
        TextView mAdTotalAmountLabel = confirmOperationBtmSheet.findViewById(R.id.cardAdConfirmTotalAmountLabelTv);
        final TextView mAdTotalAmountValue = confirmOperationBtmSheet.findViewById(R.id.cardAdConfirmTotalAmountValueTv);

        QMUIRoundButton mConfirmBtn = confirmOperationBtmSheet.findViewById(R.id.cardAdConfirmBuyOrSellBtn);
        mAdTotalQuantitySub.setTextColor(getResources().getColor(R.color.app_txt_color_gray));

//        fill detail
        mAdTitle.setText(itemInfo.getAdTitle());
        String priceTxt = itemInfo.getPrice() + " " + "BU" + " " + getString(R.string.card_package_card_ad_confirm_unit_txt);
        mAdPrice.setText(priceTxt);
        String assetIdTxt = getString(R.string.card_package_card_ad_confirm_asset_id_txt);
        assetIdTxt = String.format(assetIdTxt, cardDetailsDto.getAssetDetail().getAssetInfo().getCode());
        mAdAssetId.setText(assetIdTxt);
        String feeTxt = getString(R.string.card_package_card_ad_sell_fee_txt);
        feeTxt = String.format(feeTxt, String.valueOf(Constants.CARD_TX_FEE)) + " " + "BU";
        mAdSellFee.setText(feeTxt);


        totalQuantityTxt = getString(R.string.card_package_card_ad_sell_amount_txt);
        mAdTotalAmountLabel.setText(R.string.card_package_card_ad_confirm_total_amount_sell_txt);
        totalAmount = DecimalCalculate.sub(DecimalCalculate.mul(Double.parseDouble(String.valueOf(buyOrSellQuantity)), Double.parseDouble(itemInfo.getPrice())), Constants.CARD_TX_FEE);

        mConfirmBtn.setText(R.string.card_package_card_ad_confirm_sell_txt);

        mAdTotalQuantity.setText(totalQuantityTxt);
        mAdTotalQuantityCompute.setText(buyOrSellQuantity.toString());
        mAdTotalAmountValue.setText(CommonUtil.rvZeroAndDot(String.valueOf(totalAmount)) + "BU");

        confirmOperationBtmSheet.show();
        confirmOperationBtmSheet.findViewById(R.id.cardAdConfirmCloseBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmOperationBtmSheet.dismiss();
            }
        });
        mConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPasswordConfirmDialog();
            }
        });
        mAdTotalQuantitySub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!subFlag) {
                    return;
                }
                buyOrSellQuantity--;
                addFlag = true;
                mAdTotalQuantityAdd.setTextColor(getResources().getColor(R.color.app_color_green));
                if (buyOrSellQuantity == 1) {
                    subFlag = false;
                    mAdTotalQuantitySub.setTextColor(getResources().getColor(R.color.app_txt_color_gray));
                }
                totalAmount = DecimalCalculate.sub(DecimalCalculate.mul(Double.parseDouble(String.valueOf(buyOrSellQuantity)), Double.parseDouble(itemInfo.getPrice())), Constants.CARD_TX_FEE);

                mAdTotalQuantity.setText(totalQuantityTxt);
                mAdTotalQuantityCompute.setText(buyOrSellQuantity.toString());
                mAdTotalAmountValue.setText(CommonUtil.rvZeroAndDot(String.valueOf(totalAmount)) + "BU");
            }
        });
        mAdTotalQuantityAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!addFlag) {
                    return;
                }
                buyOrSellQuantity++;
                subFlag = true;
                mAdTotalQuantitySub.setTextColor(getResources().getColor(R.color.app_color_green));
                if (buyOrSellQuantity.equals(stockQuantity)) {
                    addFlag = false;
                    mAdTotalQuantityAdd.setTextColor(getResources().getColor(R.color.app_txt_color_gray));
                }
                totalAmount = DecimalCalculate.sub(DecimalCalculate.mul(Double.parseDouble(String.valueOf(buyOrSellQuantity)), Double.parseDouble(itemInfo.getPrice())), Constants.CARD_TX_FEE);

                mAdTotalQuantity.setText(totalQuantityTxt);
                mAdTotalQuantityCompute.setText(buyOrSellQuantity.toString());
                mAdTotalAmountValue.setText(CommonUtil.rvZeroAndDot(String.valueOf(totalAmount)) + "BU");
            }
        });
    }

    private void showPasswordConfirmDialog() {
        pwdConfirmDialog = new QMUIDialog(getContext());
        pwdConfirmDialog.setCanceledOnTouchOutside(true);
        pwdConfirmDialog.setContentView(R.layout.password_comfirm_layout);
        pwdConfirmDialog.show();

        QMUIRoundButton mPasswordConfirmBtn = pwdConfirmDialog.findViewById(R.id.passwordConfirmBtn);
        ImageView mPasswordConfirmCloseBtn = pwdConfirmDialog.findViewById(R.id.passwordConfirmCloseBtn);

        mPasswordConfirmCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pwdConfirmDialog.dismiss();
            }
        });
        mPasswordConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 检查合法性
                EditText mPasswordConfirmEt = pwdConfirmDialog.findViewById(R.id.passwordConfirmEt);
                final String password = mPasswordConfirmEt.getText().toString().trim();
                txSendingTipDialog = new QMUITipDialog.Builder(getContext())
                        .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                        .setTipWord(getResources().getString(R.string.send_tx_handleing_txt))
                        .create();
                txSendingTipDialog.show();
                txSendingTipDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

                        if(event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
                            return true;
                        }
                        return false;
                    }
                });
                getSellAdBlob(password);
            }
        });
    }

    private void getSellAdBlob(final String password) {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("advertId",adId);
        paramsMap.put("price",adPrice);
        paramsMap.put("totalQuantity",buyOrSellQuantity.toString());
        paramsMap.put("userToken", sharedPreferencesHelper.getSharedPreference("userToken","").toString());
        AssetService assetService = RetrofitFactory.getInstance().getRetrofit().create(AssetService.class);
        Call<ApiResult<GetCardAdBlobRespDto>> call = assetService.getCardSellAdBlob(paramsMap);
        call.enqueue(new Callback<ApiResult<GetCardAdBlobRespDto>>() {
            @Override
            public void onResponse(Call<ApiResult<GetCardAdBlobRespDto>> call, Response<ApiResult<GetCardAdBlobRespDto>> response) {
                ApiResult<GetCardAdBlobRespDto> respDto = response.body();
                if(ExceptionEnum.SUCCESS.getCode().equals(respDto.getErrCode())){
                    txBlob = respDto.getData().getTxBlob();
                    txHash = respDto.getData().getTxHash();
                    blobId = respDto.getData().getBlobId();
                    signBlob(password);
                } else {
                    txSendingTipDialog.dismiss();
                    Toast.makeText(getContext(),getString(R.string.err_code_txt) +
                            respDto.getErrCode(),Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResult<GetCardAdBlobRespDto>> call, Throwable t) {
                txSendingTipDialog.dismiss();
                if (getActivity() != null) {
                    Toast.makeText(getContext(),getString(R.string.network_error_msg),Toast.LENGTH_LONG).show();
                }
                t.printStackTrace();
            }
        });
    }

    private void signBlob(final String password) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String bpData = getAccountBPData();
                    final WalletSignData walletSignData = Wallet.getInstance().signTxBlob(password, txBlob, bpData);

                    if (walletSignData != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                submitSell(walletSignData.getSignData(), walletSignData.getPublicKey());
                            }
                        });
                    }
                }catch (WalletException e){
                    e.printStackTrace();
                    Looper.prepare();
                    Toast.makeText(getActivity(), R.string.network_error_msg, Toast.LENGTH_SHORT).show();
                    txSendingTipDialog.dismiss();
                    Looper.loop();
                } catch (Exception e) {
                    e.printStackTrace();
                    Looper.prepare();
                    Toast.makeText(getActivity(), R.string.checking_password_error, Toast.LENGTH_SHORT).show();
                    txSendingTipDialog.dismiss();
                    Looper.loop();
                }
            }
        }).start();
    }

    private void submitSell(String txBlobSign, String publicKey) {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("txBlob", txBlob);
        paramsMap.put("txHash", txHash);
        paramsMap.put("blobId", blobId);
        paramsMap.put("txBlobSign", txBlobSign);
        paramsMap.put("publicKey", publicKey);
        paramsMap.put("userToken", sharedPreferencesHelper.getSharedPreference("userToken", "").toString());
        AssetService assetService = RetrofitFactory.getInstance().getRetrofit().create(AssetService.class);
        Call<ApiResult> call = assetService.submitSellAd(paramsMap);
        call.enqueue(new Callback<ApiResult>() {
            @Override
            public void onResponse(Call<ApiResult> call, Response<ApiResult> response) {
                ApiResult respDto = response.body();
                if(ExceptionEnum.SUCCESS.getCode().equals(respDto.getErrCode())){
                    pwdConfirmDialog.dismiss();
                    txSendingTipDialog.dismiss();
                    confirmOperationBtmSheet.dismiss();
                } else {
                    txSendingTipDialog.dismiss();
                    Toast.makeText(getContext(),getString(R.string.err_code_txt) +
                            respDto.getErrCode(),Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResult> call, Throwable t) {
                txSendingTipDialog.dismiss();
                if (getActivity() != null) {
                    Toast.makeText(getContext(),getString(R.string.network_error_msg),Toast.LENGTH_LONG).show();
                }
                t.printStackTrace();
            }
        });
    }

    private String getAccountBPData(){
        return sharedPreferencesHelper.getSharedPreference("BPData", "").toString();
    }

    private void initTopBar() {
        mTopBar.setBackgroundDividerEnabled(false);
        mTopBar.setTitle(cardName);
        mTopBar.setBackgroundColor(getResources().getColor(R.color.app_bg_color_purple_2));
        mTopBar.addLeftImageButton(R.mipmap.icon_tobar_left_arrow, R.id.topbar_left_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        });
    }
}
