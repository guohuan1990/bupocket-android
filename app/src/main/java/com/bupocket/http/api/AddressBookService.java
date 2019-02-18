package com.bupocket.http.api;

import com.bupocket.http.api.dto.resp.ApiResult;
import com.bupocket.http.api.dto.resp.GetAddressBookRespDto;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AddressBookService {
    @POST("wallet/my/addressBook/list")
    Call<ApiResult<GetAddressBookRespDto>> getMyAddressBook(@Body Map<String, Object> map);
    @POST("wallet/my/addressBook/add")
    Call<ApiResult> addAddress(@Body Map<String, Object> map);
    @POST("wallet/my/addressBook/update")
    Call<ApiResult> updateAddress(@Body Map<String, Object> map);
    @POST("wallet/my/addressBook/delete")
    Call<ApiResult> deleteAddress(@Body Map<String, Object> map);
}
