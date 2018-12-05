package com.bupocket.fragment;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.*;
import android.support.annotation.RequiresApi;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.alibaba.fastjson.JSON;
import com.bupocket.R;
import com.bupocket.activity.CaptureActivity;
import com.bupocket.base.BaseFragment;
import com.bupocket.common.Constants;
import com.bupocket.enums.TokenTypeEnum;
import com.bupocket.enums.TxStatusEnum;
import com.bupocket.http.api.RetrofitFactory;
import com.bupocket.http.api.TxService;
import com.bupocket.http.api.dto.resp.ApiResult;
import com.bupocket.http.api.dto.resp.TxDetailRespDto;
import com.bupocket.utils.AmountUtil;
import com.bupocket.utils.CommonUtil;
import com.bupocket.utils.DecimalCalculate;
import com.bupocket.utils.SharedPreferencesHelper;
import com.bupocket.wallet.Wallet;
import com.bupocket.wallet.enums.ExceptionEnum;
import com.bupocket.wallet.exception.WalletException;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import retrofit2.Call;
import retrofit2.Response;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class BPSendTokenFragment extends BaseFragment {
    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.accountAvailableBalanceTv)
    TextView mAccountAvailableBalanceTv;

    private String currentAccAddress;
    protected SharedPreferencesHelper sharedPreferencesHelper;
    @BindView(R.id.destAccountAddressEt)
    EditText destAccountAddressEt;
    @BindView(R.id.sendAmountEt)
    EditText sendAmountET;
    @BindView(R.id.sendFormNoteEt)
    EditText sendFormNoteEt;
    @BindView(R.id.sendFormTxFeeEt)
    EditText sendFormTxFeeEt;
    @BindView(R.id.completeMnemonicCodeBtn)
    QMUIRoundButton mCompleteMnemonicCodeBtn;
    @BindView(R.id.sendFormScanIv)
    ImageView mSendFormScanIv;
    @BindView(R.id.tokenCodeTv)
    TextView mTokenCodeTv;

    @BindView(R.id.sendTokenAmountLable)
    TextView mSendTokenAmountLable;


    private String hash;
    String tokenCode;
    String tokenType;
    String tokenIssuer;
    String tokenDecimals;
    private String availableTokenBalance;
    QMUITipDialog txSendingTipDialog;

    private TxDetailRespDto.TxDeatilRespBoBean txDeatilRespBoBean;
    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_send, null);
        ButterKnife.bind(this, root);
        QMUIStatusBarHelper.setStatusBarLightMode(getBaseFragmentActivity());

        initData();
        confirmSendInfo();
        initTopBar();
        setDestAddress();

        mSendFormScanIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startScan();
            }
        });
        buildWatcher();
        return root;

    }

    private void buildWatcher() {
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mCompleteMnemonicCodeBtn.setEnabled(false);
                mCompleteMnemonicCodeBtn.setBackgroundColor(getResources().getColor(R.color.disabled_btn_color));
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCompleteMnemonicCodeBtn.setEnabled(false);
                mCompleteMnemonicCodeBtn.setBackgroundColor(getResources().getColor(R.color.disabled_btn_color));
            }

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void afterTextChanged(Editable s) {
                boolean signAccountAddress = destAccountAddressEt.getText().length() > 0;
                boolean signAmount = sendAmountET.getText().length() > 0;
                boolean signTxFee = sendFormTxFeeEt.getText().length() > 0;
                if(signAccountAddress && signAmount && signTxFee){
                    mCompleteMnemonicCodeBtn.setEnabled(true);
                    mCompleteMnemonicCodeBtn.setBackground(getResources().getDrawable(R.drawable.radius_button_able_bg));
                }else {
                    mCompleteMnemonicCodeBtn.setEnabled(false);
                    mCompleteMnemonicCodeBtn.setBackground(getResources().getDrawable(R.drawable.radius_button_disable_bg));
                }
            }
        };

        destAccountAddressEt.addTextChangedListener(watcher);
        sendAmountET.addTextChangedListener(watcher);
        sendFormTxFeeEt.addTextChangedListener(watcher);

        TextWatcher addressEtWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(final Editable s) {

                if(!TokenTypeEnum.BU.getCode().equals(tokenType)){
                    @SuppressLint("HandlerLeak")
                    final Handler handler = new Handler(){
                        @Override
                        public void handleMessage(Message msg) {
                            super.handleMessage(msg);
                            Bundle data = msg.getData();
                            String fee = data.getString("fee");
                            sendFormTxFeeEt.setText(fee);
                        }
                    };
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            String fee;
                            if(Wallet.getInstance().checkAccAddress(s.toString())){
                                if(!Wallet.getInstance().checkAccountActivated(s.toString())){
                                    fee = Constants.ACCOUNT_NOT_ACTIVATED_SEND_FEE;
                                }else{
                                    fee = Constants.ACCOUNT_ACTIVATED_SEND_FEE;
                                }
                            }else {
                                fee = Constants.ACCOUNT_ACTIVATED_SEND_FEE;
                            }
                            Message message = new Message();
                            Bundle data = new Bundle();
                            data.putString("fee",fee);
                            message.setData(data);
                            handler.sendMessage(message);
                        }
                    };
                    new Thread(runnable).start();
                }

            }
        };
        destAccountAddressEt.addTextChangedListener(addressEtWatcher);
    }

    private void initData(){
        sharedPreferencesHelper = new SharedPreferencesHelper(getContext(), "buPocket");
        currentAccAddress = sharedPreferencesHelper.getSharedPreference("currentAccAddr", "").toString();

        tokenCode = getArguments().getString("tokenCode");
        tokenType = getArguments().getString("tokenType");
        tokenIssuer = getArguments().getString("tokenIssuer");
        tokenDecimals = getArguments().getString("tokenDecimals");
        String tokenBalance = getArguments().getString("tokenBalance");
        mTokenCodeTv.setText(tokenCode);
        mSendTokenAmountLable.setText(getResources().getText(R.string.send_amount_title) + "（"+tokenCode+"）");
        getAccountAvailableTokenBalance(tokenType, tokenBalance);

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

    private void getAccountAvailableTokenBalance(String tokenType,String tokenBalance){

        if(TokenTypeEnum.BU.getCode().equals(tokenType)){
            if(tokenBalance == null || Double.parseDouble(tokenBalance) < 0 || Double.parseDouble(tokenBalance) == 0){
                availableTokenBalance = "0";
            } else {
                Double doubleAvailableTokenBalance = DecimalCalculate.sub(Double.parseDouble(tokenBalance),com.bupocket.common.Constants.RESERVE_AMOUNT);
                if(doubleAvailableTokenBalance < 0){
                    availableTokenBalance = "0";
                }else {
                    availableTokenBalance = CommonUtil.rvZeroAndDot(new BigDecimal(DecimalCalculate.sub(Double.parseDouble(tokenBalance),com.bupocket.common.Constants.RESERVE_AMOUNT)).setScale(Integer.valueOf(tokenDecimals),BigDecimal.ROUND_HALF_UP).toPlainString());
                }
//                availableTokenBalance = CommonUtil.rvZeroAndDot(new BigDecimal(AmountUtil.availableSubtractionFee(tokenBalance,com.bupocket.common.Constants.RESERVE_AMOUNT)).setScale(Integer.valueOf(tokenDecimals),BigDecimal.ROUND_HALF_UP).toPlainString());
            }
        }else{
            availableTokenBalance = tokenBalance;
        }
        mAccountAvailableBalanceTv.setText(availableTokenBalance);
    }
    private String getAccountBPData(){
        String data = sharedPreferencesHelper.getSharedPreference("BPData", "").toString();
        return data;
    }


    private String getDestAccAddr(){
        return destAccountAddressEt.getText().toString().trim();
    }

    private void confirmSendInfo(){

        mCompleteMnemonicCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final QMUITipDialog tipDialog;

                String address = destAccountAddressEt.getText().toString().trim();
                Boolean flag = Wallet.getInstance().checkAccAddress(address);
                if(!flag || CommonUtil.isNull(address)){
                    tipDialog = new QMUITipDialog.Builder(getContext())
                            .setTipWord(getResources().getString(R.string.invalid_address))
                            .create();
                    tipDialog.show();
                    destAccountAddressEt.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            tipDialog.dismiss();
                        }
                    }, 1500);

                    return;
                }

                if(address.equals(currentAccAddress)){
                    tipDialog = new QMUITipDialog.Builder(getContext())
                            .setTipWord(getResources().getString(R.string.send_err1))
                            .create();
                    tipDialog.show();
                    destAccountAddressEt.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            tipDialog.dismiss();
                        }
                    }, 1500);
                    return;
                }



                final String sendAmount = sendAmountET.getText().toString().trim();
                if(!CommonUtil.isBU(sendAmount) || CommonUtil.isNull(sendAmount) || CommonUtil.checkSendAmountDecimals(sendAmount,tokenDecimals)){
                    tipDialog = new QMUITipDialog.Builder(getContext())
                            .setTipWord(getResources().getString(R.string.invalid_amount))
                            .create();
                    tipDialog.show();
                    sendAmountET.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            tipDialog.dismiss();
                        }
                    }, 1500);
                    return;
                }
                if (Double.parseDouble(sendAmount) < com.bupocket.common.Constants.MIN_SEND_AMOUNT) {
                    tipDialog = new QMUITipDialog.Builder(getContext())
                            .setTipWord(CommonUtil.addSuffix(CommonUtil.addSuffix(getResources().getString(R.string.amount_too_small),CommonUtil.calculateMinSendAmount(tokenDecimals)),tokenCode))
                            .create();
                    tipDialog.show();
                    sendAmountET.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            tipDialog.dismiss();
                        }
                    }, 1500);
                    return;
                }
                if(Double.parseDouble(mAccountAvailableBalanceTv.getText().toString()) < Double.parseDouble(sendAmount)){
                    tipDialog = new QMUITipDialog.Builder(getContext())
                            .setTipWord(getResources().getString(R.string.balance_not_enough))
                            .create();
                    tipDialog.show();
                    mAccountAvailableBalanceTv.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            tipDialog.dismiss();
                        }
                    }, 1500);
                    return;
                }
                if (Double.parseDouble(sendAmount) > com.bupocket.common.Constants.MAX_SEND_AMOUNT) {
                    tipDialog = new QMUITipDialog.Builder(getContext())
                            .setTipWord(CommonUtil.addSuffix(getResources().getString(R.string.amount_too_big),tokenCode))
                            .create();
                    tipDialog.show();
                    sendAmountET.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            tipDialog.dismiss();
                        }
                    }, 1500);
                    return;
                }

                final String note = sendFormNoteEt.getText().toString();

                if(!CommonUtil.isNull(note) && note.length() > com.bupocket.common.Constants.SEND_TOKEN_NOTE_MAX_LENGTH){
                    tipDialog = new QMUITipDialog.Builder(getContext())
                            .setTipWord(getResources().getString(R.string.send_token_note_too_long))
                            .create();
                    tipDialog.show();
                    sendFormNoteEt.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            tipDialog.dismiss();
                        }
                    }, 1500);
                    return;
                }

                final String txFee = sendFormTxFeeEt.getText().toString();

                if(!CommonUtil.isBU(txFee) || CommonUtil.isNull(txFee)){
                    tipDialog = new QMUITipDialog.Builder(getContext())
                            .setTipWord(getResources().getString(R.string.invalid_tx_fee))
                            .create();
                    tipDialog.show();
                    sendFormTxFeeEt.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            tipDialog.dismiss();
                        }
                    }, 1500);
                    return;
                }
                if (Double.parseDouble(txFee) < com.bupocket.common.Constants.MIN_FEE) {
                    tipDialog = new QMUITipDialog.Builder(getContext())
                            .setTipWord(getResources().getString(R.string.tx_fee_too_small))
                            .create();
                    tipDialog.show();
                    sendFormTxFeeEt.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            tipDialog.dismiss();
                        }
                    }, 1500);
                    return;
                }
                if(Double.parseDouble(txFee) > Constants.MAX_FEE){
                    tipDialog = new QMUITipDialog.Builder(getContext())
                            .setTipWord(getResources().getString(R.string.tx_fee_too_big))
                            .create();
                    tipDialog.show();
                    sendFormTxFeeEt.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            tipDialog.dismiss();
                        }
                    }, 1500);
                    return;
                }




                final QMUIBottomSheet sheet = new QMUIBottomSheet(getContext());

                sheet.setContentView(R.layout.send_confirm_layout);

                final TextView addressTxt = sheet.findViewById(R.id.sendTargetAddress);
                addressTxt.setText(address);

                TextView amountTxt = sheet.findViewById(R.id.sendAmount);
                amountTxt.setText(sendAmount + " " + tokenCode);

                TextView estimateCostTxt = sheet.findViewById(R.id.sendEstimateCost);
                estimateCostTxt.setText(txFee + " BU");

                TextView remarkTxt = sheet.findViewById(R.id.sendRemark);
                remarkTxt.setText(note);
                
                sheet.findViewById(R.id.sendConfirmCloseBtn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sheet.dismiss();
                    }
                });

                sheet.show();

                sheet.findViewById(R.id.sendConfirmBtn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sheet.dismiss();
                        final QMUIDialog qmuiDialog = new QMUIDialog(getContext());
                        qmuiDialog.setCanceledOnTouchOutside(false);
                        qmuiDialog.setContentView(R.layout.password_comfirm_layout);
                        qmuiDialog.show();

                        QMUIRoundButton mPasswordConfirmBtn = qmuiDialog.findViewById(R.id.passwordConfirmBtn);

                        ImageView mPasswordConfirmCloseBtn = qmuiDialog.findViewById(R.id.passwordConfirmCloseBtn);

                        mPasswordConfirmCloseBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                qmuiDialog.dismiss();
                            }
                        });

                        mPasswordConfirmBtn.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                // 检查合法性
                                EditText mPasswordConfirmEt = qmuiDialog.findViewById(R.id.passwordConfirmEt);
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


                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {

                                        String accountBPData = getAccountBPData();
                                        String destAddess = getDestAccAddr();
                                        try {

                                            if(TokenTypeEnum.BU.getCode().equals(tokenType)){
                                                hash = Wallet.getInstance().sendBu(password,accountBPData, currentAccAddress, destAddess, sendAmount, note,txFee);
                                            }else if(TokenTypeEnum.ATP10.getCode().equals(tokenType)){
                                                hash = Wallet.getInstance().sendToken(password,accountBPData,currentAccAddress,destAddess,tokenCode,tokenIssuer, sendAmount,tokenDecimals,note, txFee);
                                            }


                                        }catch (WalletException e){
                                            e.printStackTrace();
                                            Looper.prepare();
                                            if(ExceptionEnum.FEE_NOT_ENOUGH.getCode().equals(e.getErrCode())){
                                                Toast.makeText(getActivity(), R.string.send_tx_fee_not_enough, Toast.LENGTH_SHORT).show();
                                            }else if(ExceptionEnum.BU_NOT_ENOUGH.getCode().equals(e.getErrCode())){
                                                Toast.makeText(getActivity(), R.string.send_tx_bu_not_enough, Toast.LENGTH_SHORT).show();
                                            }else {
                                                Toast.makeText(getActivity(), R.string.network_error_msg, Toast.LENGTH_SHORT).show();
                                            }
                                            txSendingTipDialog.dismiss();
                                            Looper.loop();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            Looper.prepare();
                                            Toast.makeText(getActivity(), R.string.checking_password_error, Toast.LENGTH_SHORT).show();
                                            txSendingTipDialog.dismiss();
                                            Looper.loop();
                                        }finally {
                                            timer.schedule(timerTask,
                                                    1 * 1000,//延迟1秒执行
                                                    1000);
                                        }
                                    }
                                }).start();
                                qmuiDialog.dismiss();

                            }
                        });

                    }
                });
            }
        });
    }

    private void setDestAddress(){
        Bundle bundle = getArguments();
        if(bundle != null){
            final String destAddress = getArguments().getString("destAddress");
            destAccountAddressEt.setText(destAddress);

            if(!TokenTypeEnum.BU.getCode().equals(tokenType)){
                @SuppressLint("HandlerLeak")
                final Handler handler = new Handler(){
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        Bundle data = msg.getData();
                        String fee = data.getString("fee");
                        sendFormTxFeeEt.setText(fee);
                    }
                };
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        String fee;
                        if(Wallet.getInstance().checkAccAddress(destAddress)){
                            if(!Wallet.getInstance().checkAccountActivated(destAddress)){
                                fee = Constants.ACCOUNT_NOT_ACTIVATED_SEND_FEE;
                            }else{
                                fee = Constants.ACCOUNT_ACTIVATED_SEND_FEE;
                            }
                        }else {
                            fee = Constants.ACCOUNT_ACTIVATED_SEND_FEE;
                        }
                        Message message = new Message();
                        Bundle data = new Bundle();
                        data.putString("fee",fee);
                        message.setData(data);
                        handler.sendMessage(message);
                    }
                };
                new Thread(runnable).start();
            }

        }
    }

    private void startScan(){
        IntentIntegrator intentIntegrator = IntentIntegrator.forSupportFragment(this);
        intentIntegrator.setBeepEnabled(true);
        intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        intentIntegrator.setPrompt(getResources().getString(R.string.wallet_scan_notice));
        intentIntegrator.setCaptureActivity(CaptureActivity.class);
        // 开始扫描
        intentIntegrator.initiateScan();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(getActivity(), R.string.wallet_scan_cancel, Toast.LENGTH_LONG).show();
            } else {
                String destAddress = result.getContents();
                destAccountAddressEt.setText(destAddress);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);

        }
    }


    private int timerTimes = 0;
    private final Timer timer = new Timer();
    @SuppressLint("HandlerLeak")
    private Handler mHanlder = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if(timerTimes > Constants.TX_REQUEST_TIMEOUT_TIMES){
                        timerTask.cancel();
                        txSendingTipDialog.dismiss();
                        startFragmentAndDestroyCurrent(new BPTxRequestTimeoutFragment());
                        return;
                    }
                    timerTimes++;
                    System.out.println("timerTimes:" + timerTimes);
                    TxService txService = RetrofitFactory.getInstance().getRetrofit().create(TxService.class);
                    Map<String, Object> paramsMap = new HashMap<>();
                    paramsMap.put("hash",hash);
                    Call<ApiResult<TxDetailRespDto>> call = txService.getTxDetailByHash(paramsMap);
                    call.enqueue(new retrofit2.Callback<ApiResult<TxDetailRespDto>>(){

                        @Override
                        public void onResponse(Call<ApiResult<TxDetailRespDto>> call, Response<ApiResult<TxDetailRespDto>> response) {
                            ApiResult<TxDetailRespDto> resp = response.body();
                            if(!TxStatusEnum.SUCCESS.getCode().toString().equals(resp.getErrCode())){
                                return;
                            }else{
                                txDeatilRespBoBean = resp.getData().getTxDeatilRespBo();
                                timerTask.cancel();
                                txSendingTipDialog.dismiss();
                                Bundle argz = new Bundle();
                                argz.putString("destAccAddr",txDeatilRespBoBean.getDestAddress());
                                argz.putString("sendAmount",txDeatilRespBoBean.getAmount());
                                argz.putString("txFee",txDeatilRespBoBean.getFee());
                                argz.putString("tokenCode",tokenCode);
                                argz.putString("note",txDeatilRespBoBean.getOriginalMetadata());
                                argz.putString("state",txDeatilRespBoBean.getStatus().toString());
                                argz.putString("sendTime",txDeatilRespBoBean.getApplyTimeDate());
                                BPSendStatusFragment bpSendStatusFragment = new BPSendStatusFragment();
                                bpSendStatusFragment.setArguments(argz);
                                startFragmentAndDestroyCurrent(bpSendStatusFragment);
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResult<TxDetailRespDto>> call, Throwable t) {
                            Toast.makeText(getActivity(), R.string.tx_timeout_err, Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            if(hash != null && !hash.equals("")){
                mHanlder.sendEmptyMessage(1);
            }
        }
    };
}
