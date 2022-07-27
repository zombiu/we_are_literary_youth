package com.example.we_youth.net

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    fun <T> get(baseUrl: String, apiClass: Class<T>): T {

        val logger = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }

        val client = OkHttpClient.Builder()
            .addInterceptor(logger)
//                .cookieJar(LocalCookieJar())
            .build()

        //注意：如果只是用Paging、Flow不需要LiveDataCallAdapterFactory或CoroutineCallAdapterFactory。
        //其实用了协程后，是不需要添加addCallAdapterFactory的
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(UIStateCallAdapterFactory())
            .build()
            .create(apiClass)
    }


}