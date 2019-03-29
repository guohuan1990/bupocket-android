package com.bupocket.http.api;

import com.bupocket.http.api.dto.resp.ApiResult;
import com.bupocket.http.api.dto.resp.GetQRContentDto;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface NodePlanService {
    @FormUrlEncoded
    @POST("nodeServer/qr/v1/content")
    @Headers("Content-Type:application/x-www-form-urlencoded; charset=utf-8")
    Call<ApiResult<GetQRContentDto>> getQRContent(@Field("qrcodeSessionId") String qrcodeSessionId);
    @POST("nodeServer/tx/v1/confirm")
    Call<ApiResult> confirmTransaction(@Body Map<String,Object> map);
    @POST("nodeServer/node/v1/vote/revoke/user")
    Call<ApiResult> revokeVote(@Body Map<String,Object> map);
    @POST("nodeServer/node/v1/get/reward/user")
    Call<ApiResult> getReward(@Body Map<String,Object> map);
    @POST("nodeServer/node/v1/list")
    Call<ApiResult> getSuperNodeList(Map<String,Object> map);
}
