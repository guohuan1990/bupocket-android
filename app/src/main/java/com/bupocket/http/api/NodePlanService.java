package com.bupocket.http.api;

import com.bupocket.http.api.dto.resp.ApiResult;
import com.bupocket.http.api.dto.resp.GetQRContentDto;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface NodePlanService {
    @POST("nodeServer/qr/v1/content")
    Call<ApiResult<GetQRContentDto>> getQRContent(@Body Map<String,Object> map);
    @POST("nodeServer/tx/v1/confirm")
    Call<ApiResult> confirmTransaction(@Body Map<String,Object> map);
}
