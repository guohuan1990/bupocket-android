package com.bupocket.http.api;

import com.bupocket.dto.resp.ApiResult;
import com.bupocket.dto.resp.GetMyTxsRespDto;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

import java.util.Map;

public interface TxService {
    @GET("wallet/user/tx/list")
    Call<ApiResult<GetMyTxsRespDto>> getMyTxs(@QueryMap Map<String, Object> map);
}
