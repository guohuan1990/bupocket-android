package com.assetMarket.http.api;

import com.assetMarket.http.api.dto.resp.ApiResult;
import com.assetMarket.http.api.dto.resp.TxDetailRespDto;
import com.assetMarket.http.api.dto.resp.GetMyTxsRespDto;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface TxService {
//    @POST("wallet/user/tx/list")
    @POST("wallet/v2/tx/list")
    Call<ApiResult<GetMyTxsRespDto>> getMyTxs(@Body Map<String, Object> map);
    @POST("wallet/v2/tx/detail")
    Call<ApiResult<TxDetailRespDto>> getTxDetailByOptNo(@Body Map<String, Object> map);
    @POST("wallet/tx/detail")
    Call<ApiResult<TxDetailRespDto>> getTxDetailByHash(@Body Map<String, Object> map);
}
