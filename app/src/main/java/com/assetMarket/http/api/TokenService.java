package com.assetMarket.http.api;

import com.assetMarket.http.api.dto.resp.ApiResult;
import com.assetMarket.http.api.dto.resp.GetMyTxsRespDto;
import com.assetMarket.http.api.dto.resp.GetTokenDetailRespDto;
import com.assetMarket.http.api.dto.resp.SearchTokenRespDto;
import com.assetMarket.http.api.dto.resp.GetTokensRespDto;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

import java.util.Map;

public interface TokenService {
    @POST("wallet/token/list")
    Call<ApiResult<GetTokensRespDto>> getTokens(@Body Map<String,Object> map);
    @POST("wallet/query/token")
    Call<ApiResult<SearchTokenRespDto>> queryTokens(@Body Map<String,Object> map);
    @POST("wallet/token/tx/list")
    Call<ApiResult<GetMyTxsRespDto>> getTokenTxs(@Body Map<String,Object> map);
    @POST("wallet/token/detail")
    Call<ApiResult<GetTokenDetailRespDto>> getTokenDetail(@Body Map<String,Object> map);
}
