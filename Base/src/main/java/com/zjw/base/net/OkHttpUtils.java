package com.zjw.base.net;

import com.zjw.base.utils.LogCatUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

/**
 * Created by Frank on 2017/6/20.
 */

public class OkHttpUtils {

    public static void postAsynHttp() {
        OkHttpClient mOkHttpClient=new OkHttpClient
                .Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request()
                                .newBuilder()
                                .addHeader("Content-Type", "application/json; charset=UTF-8")
                                .addHeader("Accept-Encoding", "gzip, deflate")
                                .addHeader("Connection", "keep-alive")
                                .addHeader("Accept", "*/*")
                                .addHeader("Cookie", "add cookies here")
                                .build();
                        return chain.proceed(request);
                    }
                }).build();
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("Noise", "12345678901234567890123456789012");
        builder.add("ClientDevice", "IOS10.1TomsPhone");
        builder.add("Token", "");
        builder.add("TimeStamp", "2017-04-19T03:00:03");
        builder.add("Signature", "caf15d57d87535422c5ad768fa105b57248cd85d");
        builder.add("Hello", "Hello~Server");
        RequestBody formBody = builder.build();
        Request request = new Request.Builder()
                .url("http://www.dotasell.com/API/Server/Hello")
                .post(formBody)
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogCatUtil.e("frank","error"+call.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String str = response.body().string();
                LogCatUtil.e("frank","onResponse"+response.isSuccessful()+" : "+response.request().toString());
                LogCatUtil.e("frank","str" +str);
            }

        });
    }

}
