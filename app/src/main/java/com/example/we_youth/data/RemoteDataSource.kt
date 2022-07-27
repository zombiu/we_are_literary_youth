package com.example.we_youth.data

import com.example.we_youth.net.RetrofitClient
import com.example.we_youth.net.WanApi

object RemoteDataSource {
    private const val BASE_URL = "https://www.wanandroid.com/"

    val wanApi by lazy {
        RetrofitClient.get(BASE_URL, WanApi::class.java)
    }
}