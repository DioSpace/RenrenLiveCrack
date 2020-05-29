package com.my.okhttpdemo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MyOkHttp {

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static OkHttpClient client;

    private MyOkHttp() {
        client = new OkHttpClient
                .Builder()
                .connectTimeout(60 * 1000, TimeUnit.MILLISECONDS)
                .readTimeout(5 * 60 * 1000, TimeUnit.MILLISECONDS)
                .writeTimeout(5 * 60 * 1000, TimeUnit.MILLISECONDS)
                .build();
    }

    private static class HttpOkHttpInstance {
        private final static MyOkHttp INSTANCE = new MyOkHttp();
    }

    public static MyOkHttp getInstance() {
        return HttpOkHttpInstance.INSTANCE;
    }

    /**
     * GET请求
     *
     * @param url            请求地址
     * @param okHttpCallBack 请求回调
     * @param clazz          返回结果的Class
     * @param <T>            返回结果类型
     */
    public <T> void requestGet(@NotNull String url, @NotNull final OkHttpCallBack<T> okHttpCallBack,
                               @NotNull final Class<T> clazz) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                okHttpCallBack.requestFailure(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    requestResult(response.body().string(), okHttpCallBack, clazz);
                } else {
                    okHttpCallBack.requestFailure(response.message());
                }
            }
        });
    }

    /**
     * POST请求
     *
     * @param url            请求地址
     * @param json           请求参数 json 格式
     * @param okHttpCallBack 请求回调
     * @param clazz          返回结果的class
     * @param <T>            请求返回的类型
     */
    public <T> void requestPost(@NotNull String url, @NotNull String json, @NotNull final OkHttpCallBack<T> okHttpCallBack,
                                @NotNull final Class<T> clazz) {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                okHttpCallBack.requestFailure(e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    requestResult(response.body().string(), okHttpCallBack, clazz);
                } else {
                    okHttpCallBack.requestFailure(response.message());
                }
            }
        });
    }

    public <T> void submitFormdata(@NotNull String url, @NotNull RequestBody body, @NotNull final OkHttpCallBack<T> okHttpCallBack, @NotNull final Class<T> clazz) {
        final Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                okHttpCallBack.requestFailure(e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    requestResult(response.body().string(), okHttpCallBack, clazz);
                } else {
                    okHttpCallBack.requestFailure(response.message());
                }
            }
        });
    }

    private <T> void requestResult(String result, OkHttpCallBack<T> callBack, @NotNull Class<T> clazz) {

        if ("java.lang.String".equals(clazz.getName())) {
            callBack.requestSuccess((T) result);

        } else {
            Gson gson = new GsonBuilder().create();
            callBack.requestSuccess(gson.fromJson(result, clazz));
        }
    }


    public interface OkHttpCallBack<T> {
        /**
         * 请求成功回调
         *
         * @param t 回调返回成功结果输出
         */
        void requestSuccess(T t);

        /**
         * 请求失败回调
         *
         * @param message 回调返回失败消息
         */
        void requestFailure(String message);
    }

}
