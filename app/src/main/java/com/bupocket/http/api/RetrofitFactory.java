package com.bupocket.http.api;

import com.bupocket.common.Constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitFactory {
    private static RetrofitFactory factory;
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

    public Retrofit getRetrofit(){
        return retrofit;
    }

    private void init(){
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd hh:mm:ss")
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.WEB_SERVER_DOMAIN)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }





}
