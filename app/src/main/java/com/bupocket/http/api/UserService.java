package com.bupocket.http.api;


import com.bupocket.http.api.dto.resp.ApiResult;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserService {
    @POST("/user/feedback")
    Call<ApiResult> submitFeedback(@Body Map<String, Object> map);
}
