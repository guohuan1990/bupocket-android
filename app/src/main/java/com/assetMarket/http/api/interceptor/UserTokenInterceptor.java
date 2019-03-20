package com.assetMarket.http.api.interceptor;

import android.app.Activity;
import android.os.*;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.alibaba.fastjson.JSON;
import com.assetMarket.common.Constants;
import com.assetMarket.enums.ExceptionEnum;
import com.assetMarket.http.api.dto.resp.GetUserTokenRespDto;
import com.assetMarket.utils.SharedPreferencesHelper;
import com.assetMarket.wallet.exception.WalletException;
import com.assetMarket.wallet.model.WalletSignData;
import com.assetMarket.BPApplication;
import com.assetMarket.R;
import com.assetMarket.http.api.AccountService;
import com.assetMarket.http.api.RetrofitFactory;
import com.assetMarket.http.api.dto.resp.ApiResult;
import com.assetMarket.wallet.Wallet;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import retrofit2.Call;

import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.HashMap;
import java.util.Map;

import static com.alibaba.fastjson.util.IOUtils.UTF8;

public class UserTokenInterceptor implements Interceptor {
    private Activity mActivity;
    private QMUIBottomSheet mConfirmPasswordDialog;
    private QMUITipDialog confirmPasswordTipDialog;
    protected SharedPreferencesHelper sharedPreferencesHelper = new SharedPreferencesHelper(BPApplication.getContext(), Constants.LOCAL_SHARED_FILE_NAME);

    public UserTokenInterceptor(Activity activity) {
        this.mActivity = activity;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());
        ResponseBody responseBody = response.body();
        long contentLength = responseBody.contentLength();
        if (!bodyEncoded(response.headers())) {
            BufferedSource source = responseBody.source();
            source.request(Long.MAX_VALUE); // Buffer the entire body.
            Buffer buffer = source.buffer();

            Charset charset = UTF8;
            MediaType contentType = responseBody.contentType();
            if (contentType != null) {
                try {
                    charset = contentType.charset(UTF8);
                } catch (UnsupportedCharsetException e) {
                    return response;
                }
            }
            if (!isPlaintext(buffer)) {
                return response;
            }
            String bodyString = "";
            if (contentLength != 0) {
                bodyString = buffer.clone().readString(charset);
                Log.d("Response", " response.url():"+ response.request().url());
                Log.d("Response", " response.body():" + bodyString);
                //得到所需的string，开始判断是否异常
                //***********************do something*****************************
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

            return response.newBuilder().body(ResponseBody.create(contentType, bodyString)).build();
        }
        return response.newBuilder().build();
//        String bodyString = responseBody.string();
//        MediaType contentType = responseBody.contentType();
//        Log.d("Response", bodyString);

//        ApiResult<Object> apiResult = JSON.parseObject(bodyString, ApiResult.class);
//        if (ExceptionEnum.USER_TOKEN_ERR.getCode().equals(apiResult.getErrCode())) {
//            if (BPApplication.confirmPasswordDialog == null) {
//                mActivity.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        startConfirmPasswordDialog();
//                    }
//                });
//                return response.newBuilder().build();
//            }
//        }
//        return response.newBuilder().body(ResponseBody.create(contentType, bodyString)).build();
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
                if (ExceptionEnum.SUCCESS.getCode().equals(respDto.getErrCode())) {
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

    private boolean bodyEncoded(Headers headers) {
        String contentEncoding = headers.get("Content-Encoding");
        return contentEncoding != null && !contentEncoding.equalsIgnoreCase("identity");
    }

    static boolean isPlaintext(Buffer buffer) throws EOFException {
        try {
            Buffer prefix = new Buffer();
            long byteCount = buffer.size() < 64 ? buffer.size() : 64;
            buffer.copyTo(prefix, 0, byteCount);
            for (int i = 0; i < 16; i++) {
                if (prefix.exhausted()) {
                    break;
                }
                int codePoint = prefix.readUtf8CodePoint();
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false;
                }
            }
            return true;
        } catch (EOFException e) {
            return false; // Truncated UTF-8 sequence.
        }
    }
}
