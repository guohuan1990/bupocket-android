package com.bupocket.http.api;

import com.bupocket.http.api.dto.resp.ApiResult;
import com.bupocket.http.api.dto.resp.GetCardAdBlobRespDto;
import com.bupocket.http.api.dto.resp.GetCardAdDataRespDto;
import com.bupocket.http.api.dto.resp.BlobInfoDto;
import com.bupocket.http.api.dto.resp.GetCardDetailsDto;
import com.bupocket.http.api.dto.resp.GetCardMyAssetsRespDto;
import com.bupocket.http.api.dto.resp.PublishAdRespDto;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AssetService {
    @POST("asset/v1/my")
    Call<ApiResult<GetCardMyAssetsRespDto>> getMyCardMine(@Body Map<String, Object> map);
    @POST("advert/list")
    Call<ApiResult<GetCardAdDataRespDto>> getCardAdData(@Body Map<String, Object> map);
    @POST("asset/sell/getBlob")
    Call<ApiResult<BlobInfoDto>> getBlob(@Body Map<String, Object> map);
    @POST("advert/sell/publish")
    Call<ApiResult<PublishAdRespDto>> publishSellAd(@Body Map<String, Object> map);
    @POST("order/sell/blob")
    Call<ApiResult<GetCardAdBlobRespDto>> getCardSellAdBlob(@Body Map<String, Object> map);
    @POST("order/buy/blob")
    Call<ApiResult<GetCardAdBlobRespDto>> getCardBuyAdBlob(@Body Map<String, Object> map);
    @POST("order/buy/submit")
    Call<ApiResult> submitBuyAd(@Body Map<String, Object> map);
    @POST("order/sell/submit")
    Call<ApiResult> submitSellAd(@Body Map<String, Object> map);
    @POST("asset/v1/detail")
    Call<ApiResult<GetCardDetailsDto>> getCardDetails(@Body Map<String,Object> map);
}
