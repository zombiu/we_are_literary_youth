package com.example.we_youth.net

import com.example.we_youth.net.converter.OnlyData
import com.example.we_youth.net.entity.User
import com.example.we_youth.utils.UIState
import com.longjunhao.wanjetpack.data.ApiArticle
import com.longjunhao.wanjetpack.data.ApiPage
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface WanApi {


    /**
     * 登录
     */
    @POST("/user/login")
    suspend fun login(
        @Query("username") username: String,
        @Query("password") password: String
    ): BaseJson<User>

    /**
     * 获取首页文章列表
     */
//    @GET("/article/list/{page}/json")
//    suspend fun getHomeArticle(
//        @Path("page") page: Int
//    ): WanApiResponse<ApiPage<ApiArticle>>
    @OnlyData()
    @GET("/article/list/{page}/json")
    suspend fun getHomeArticle(
        @Path("page") page: Int
    ): UIState<ApiPage<ApiArticle>>

}