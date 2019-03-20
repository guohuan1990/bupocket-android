package com.assetMarket.http.api;

import android.app.Activity;

import com.assetMarket.common.Constants;
import com.assetMarket.http.api.interceptor.UserTokenInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitFactory {
    private static RetrofitFactory factory;
    private Activity mActivity;
    private Retrofit retrofit = null;
    private RetrofitFactory(){
        init();
    }

    public synchronized static RetrofitFactory getInstance(){
        if(factory == null){
            factory = new RetrofitFactory();
        }
        return factory;
    }

    public Retrofit getRetrofit(Activity activity){
        this.mActivity = activity;
        init();
        return retrofit;
    }

    public Retrofit getRetrofit(){
        return retrofit;
    }

    public void setNull4Retrofit() {factory = null;}

    private void init(){
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd hh:mm:ss")
                .create();

        OkHttpClient client = new OkHttpClient.Builder()
//                .readTimeout(5, TimeUnit.SECONDS)
//                .writeTimeout(5, TimeUnit.SECONDS)
//                .connectTimeout(15, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .addNetworkInterceptor(new UserTokenInterceptor(mActivity))
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.WEB_SERVER_DOMAIN)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }





}
