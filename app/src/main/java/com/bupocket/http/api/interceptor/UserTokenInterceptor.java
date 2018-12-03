package com.bupocket.http.api.interceptor;

import android.app.Activity;
import android.os.*;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.alibaba.fastjson.JSON;
import com.bupocket.BPApplication;
import com.bupocket.R;
import com.bupocket.common.Constants;
import com.bupocket.enums.ExceptionEnum;
import com.bupocket.http.api.AccountService;
import com.bupocket.http.api.RetrofitFactory;
import com.bupocket.http.api.dto.resp.ApiResult;
import com.bupocket.http.api.dto.resp.GetUserTokenRespDto;
import com.bupocket.utils.SharedPreferencesHelper;
import com.bupocket.wallet.Wallet;
import com.bupocket.wallet.exception.WalletException;
import com.bupocket.wallet.model.WalletSignData;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UserTokenInterceptor implements Interceptor {
    private Activity mActivity;
    private QMUIBottomSheet mConfirmPasswordDialog;
    private QMUITipDialog confirmPasswordTipDialog;
    protected SharedPreferencesHelper sharedPreferencesHelper = new SharedPreferencesHelper(BPApplication.getContext(), "buBox");

    public UserTokenInterceptor(Activity activity) {
        this.mActivity = activity;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());
        ResponseBody body = response.body();
        String bodyString = body.string();
        MediaType contentType = body.contentType();
        Log.d("Response", bodyString);

        ApiResult<Object> apiResult = JSON.parseObject(bodyString, ApiResult.class);
        if (ExceptionEnum.USER_TOKEN_ERR.getCode().equals(apiResult.getErrCode())) {
            if (BPApplication.confirmPasswordDialog == null) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        startConfirmPasswordDialog();
                    }
                });
                return response.newBuilder().build();
            }
        }
        return response.newBuilder().body(ResponseBody.create(contentType, bodyString)).build();
    }


    private QMUIBottomSheet startConfirmPasswordDialog() {
        mConfirmPasswordDialog = new QMUIBottomSheet(mActivity);
        mConfirmPasswordDialog.setContentView(R.layout.user_comfirm_password_bottom_sheet);
        QMUIRoundButton mPasswordConfirmBtn = mConfirmPasswordDialog.findViewById(R.id.passwordConfirmBtn);
        mConfirmPasswordDialog.show();
        mConfirmPasswordDialog.setCancelable(true);
        mPasswordConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 检查合法性
                EditText mPasswordConfirmEt = mConfirmPasswordDialog.findViewById(R.id.passwordConfirmEt);
                final String password = mPasswordConfirmEt.getText().toString().trim();
                confirmPasswordTipDialog = new QMUITipDialog.Builder(mActivity)
                        .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                        .setTipWord(mActivity.getString(R.string.send_tx_handleing_txt))
                        .create();
                confirmPasswordTipDialog.show();


//                mPasswordConfirmEt.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//
//                    }
//                },500);


                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String accountBPData = getAccountBPData();
                            String currentAccAddress = sharedPreferencesHelper.getSharedPreference("currentAccAddr", "").toString();
                            final WalletSignData walletSignData = Wallet.getInstance().signData(password, currentAccAddress, accountBPData);
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    getUserToken(walletSignData.getPublicKey(), walletSignData.getSignData());
                                }
                            });

                        } catch (WalletException e) {
                            e.printStackTrace();
                            confirmPasswordTipDialog.dismiss();
                        } catch (NetworkOnMainThreadException e) {
                            confirmPasswordTipDialog.dismiss();
                            mConfirmPasswordDialog.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Looper.prepare();
                            Toast.makeText(mActivity, R.string.checking_password_error, Toast.LENGTH_SHORT).show();
                            confirmPasswordTipDialog.dismiss();
                            Looper.loop();
                        }
                    }
                }).start();


            }
        });
        return mConfirmPasswordDialog;
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String userToken = data.getString("userToken");
            if (userToken != null) {
                sharedPreferencesHelper.put("userToken", userToken);
                BPApplication.confirmPasswordDialog = mConfirmPasswordDialog;
                confirmPasswordTipDialog.dismiss();
                mConfirmPasswordDialog.dismiss();
            }
        }
    };

    private String getAccountBPData() {
        String data = sharedPreferencesHelper.getSharedPreference("BPData", "").toString();
        return data;
    }

    private void getUserToken(String publicKey, String signData) {
        AccountService accountService = RetrofitFactory.getInstance().getRetrofit().create(AccountService.class);
        Map<String, Object> parmasMap = new HashMap<>();
        parmasMap.put("publicKey", publicKey);
        parmasMap.put("signData", signData);
        retrofit2.Call<ApiResult<GetUserTokenRespDto>> call = accountService.getUserToken(parmasMap);
        call.enqueue(new retrofit2.Callback<ApiResult<GetUserTokenRespDto>>() {

            @Override
            public void onResponse(Call<ApiResult<GetUserTokenRespDto>> call, retrofit2.Response<ApiResult<GetUserTokenRespDto>> response) {
                ApiResult<GetUserTokenRespDto> respDto = response.body();
                if (ExceptionEnum.SUCCESS.equals(respDto.getErrCode())) {
                    Message msg = new Message();
                    Bundle data = new Bundle();
                    data.putString("userToken", respDto.getData().getUserToken());
                    msg.setData(data);
                    handler.sendMessage(msg);
                }
            }

            @Override
            public void onFailure(Call<ApiResult<GetUserTokenRespDto>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
