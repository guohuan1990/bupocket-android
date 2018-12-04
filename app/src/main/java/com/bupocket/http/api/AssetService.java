package com.bupocket.http.api;

import com.bupocket.http.api.dto.resp.ApiResult;
import com.bupocket.http.api.dto.resp.GetCardAdDataRespDto;
import com.bupocket.http.api.dto.resp.GetCardMyAssetsRespDto;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AssetService {
    @POST("asset/v1/my")
    Call<ApiResult<GetCardMyAssetsRespDto>> getMyCardMine(@Body Map<String, Object> map);
    @POST("asset/ad/list")
    Call<ApiResult<GetCardAdDataRespDto>> getCardAdData(@Body Map<String, Object> map);

}
