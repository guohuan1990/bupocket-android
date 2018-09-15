package com.bupocket.http.api;

import com.bupocket.dto.resp.ApiResult;
import com.bupocket.dto.resp.GetMyTxsRespDto;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.Map;

public interface TxService {
    @POST("wallet/user/tx/list")
    Call<ApiResult<GetMyTxsRespDto>> getMyTxs(@Body Map<String, Object> map);
}
