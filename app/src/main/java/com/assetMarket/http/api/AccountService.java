package com.assetMarket.http.api;

import com.assetMarket.http.api.dto.resp.ApiResult;
import com.assetMarket.http.api.dto.resp.GetUserTokenRespDto;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

import java.util.Map;

public interface AccountService {
    @POST("wallet/device/bind")
    Call<ApiResult> deviceBind(@Body Map<String, Object> map);
    @POST("user/token")
    Call<ApiResult<GetUserTokenRespDto>> getUserToken(@Body Map<String, Object> map);
}
