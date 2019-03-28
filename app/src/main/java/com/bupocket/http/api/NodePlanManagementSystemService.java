package com.bupocket.http.api;

import com.bupocket.http.api.dto.resp.ApiResult;
import com.bupocket.http.api.dto.resp.UserScanQrLoginDto;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface NodePlanManagementSystemService {
    @POST("bu_pocket_api_war/login/v1/qr")
    Call<ApiResult<UserScanQrLoginDto>> userScanQrLogin(@Body Map<String,Object> map);
    @POST("bu_pocket_api_war/login/v1/qr/confirm")
    Call<ApiResult> userScanQrConfirmLogin(@Body Map<String,Object> map);
}
