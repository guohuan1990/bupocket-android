package com.assetMarket.http.api;

import com.assetMarket.http.api.dto.resp.ApiResult;
import com.assetMarket.http.api.dto.resp.GetCurrentVersionRespDto;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface VersionService {
    @GET("wallet/version")
    Call<ApiResult<GetCurrentVersionRespDto>> getCurrentVersion(@Query("appType") int appTypeCode);
}
