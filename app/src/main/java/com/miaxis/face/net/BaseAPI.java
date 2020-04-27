package com.miaxis.face.net;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.miaxis.face.manager.ConfigManager;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class BaseAPI {

    protected final static Retrofit.Builder RETROFIT_BUILDER = new Retrofit.Builder()
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create());

    protected static Retrofit retrofit;

    protected static Retrofit getRetrofit() {
        return retrofit;
    }

    public static void rebuildRetrofit() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .build();
        retrofit = RETROFIT_BUILDER
                .client(okHttpClient)
                .baseUrl("http://192.168.5.110:8080/")
                .build();
    }


}
