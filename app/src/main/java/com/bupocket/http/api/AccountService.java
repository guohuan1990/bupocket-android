package com.bupocket.http.api;

import com.bupocket.http.api.dto.resp.ApiResult;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

import java.util.Map;

public interface AccountService {
    @POST("wallet/device/bind")
    Call<ApiResult> deviceBind(@Body Map<String, Object> map);
}
