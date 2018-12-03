package com.bupocket.http.api;

import com.bupocket.http.api.dto.resp.ApiResult;
import com.bupocket.http.api.dto.resp.GetCardAdDatasRespDto;
import com.bupocket.http.api.dto.resp.GetCardMineDto;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AssetService {
    @POST("asset/v1/my")
    Call<ApiResult<GetCardMineDto>> getMyCardMine(@Body Map<String, Object> map);
    @POST("asset/ad/list")
    Call<ApiResult<GetCardAdDatasRespDto>> getCardAdDatas(@Body Map<String, Object> map);

}
