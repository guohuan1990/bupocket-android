package com.bupocket.http.api;

import com.bupocket.http.api.dto.resp.ApiResult;
import com.bupocket.http.api.dto.resp.GetMyTxsRespDto;
import com.bupocket.http.api.dto.resp.GetTokensRespDto;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface TokenService {
    @POST("wallet/token/list")
    Call<ApiResult<GetTokensRespDto>> getTokens(@Body Map<String,Object> map);
    @POST("wallet/query/token")
    Call<ApiResult<GetTokensRespDto>> queryTokens(@Body Map<String,Object> map);
    @POST("wallet/token/tx/list")
    Call<ApiResult<GetMyTxsRespDto>> getTokenTxs(@Body Map<String,Object> map);
}
