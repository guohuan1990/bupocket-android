package com.bupocket.utils;

import android.support.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.bupocket.http.api.RetrofitFactory;
import com.bupocket.http.api.VersionService;
import com.bupocket.http.api.dto.resp.ApiResult;
import com.bupocket.http.api.dto.resp.GetCurrentVersionRespDto;
import com.vector.update_app.HttpManager;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;
import java.util.Map;

import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Response;

public class UpdateAppHttpUtil implements HttpManager {
    @Override
    public void asyncGet(@NonNull String url, @NonNull Map<String, String> params, @NonNull final Callback callBack) {
        VersionService versionService = RetrofitFactory.getInstance().getRetrofit().create(VersionService.class);
        Call<ApiResult<GetCurrentVersionRespDto>> call = versionService.getCurrentVersion();
        call.enqueue(new retrofit2.Callback<ApiResult<GetCurrentVersionRespDto>>() {
            @Override
            public void onResponse(Call<ApiResult<GetCurrentVersionRespDto>> call, Response<ApiResult<GetCurrentVersionRespDto>> response) {
                ApiResult<GetCurrentVersionRespDto> respDto = response.body();
                try{
                    callBack.onResponse(JSON.toJSONString(respDto.getData()));
                    System.out.println(respDto);
                }catch (Exception e){
                    e.printStackTrace();
                    callBack.onError("false");
                }
            }

            @Override
            public void onFailure(Call<ApiResult<GetCurrentVersionRespDto>> call, Throwable t) {
                System.out.println("call = [" + call + "], t = [" + t + "]");
                callBack.onError("false");
            }
        });
    }

    @Override
    public void asyncPost(@NonNull String url, @NonNull Map<String, String> params, @NonNull Callback callBack) {

    }

    @Override
    public void download(@NonNull String url, @NonNull String path, @NonNull String fileName, @NonNull final FileCallback callback) {

        OkHttpUtils.get()
                .url(url)
                .build()
                .execute(new FileCallBack(path, fileName) {
                    @Override
                    public void inProgress(float progress, long total, int id) {
                        super.inProgress(progress, total, id);
                        callback.onProgress(progress, total);
                    }

                    @Override
                    public void onError(okhttp3.Call call, okhttp3.Response response, Exception e, int id) {
                        callback.onError(validateError(e, response));
                    }


                    @Override
                    public void onResponse(File response, int id) {
                        callback.onResponse(response);

                    }

                    @Override
                    public void onBefore(Request request, int id) {
                        super.onBefore(request, id);
                        callback.onBefore();
                    }
                });
    }
}
