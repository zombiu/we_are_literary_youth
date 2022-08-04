package com.example.we_youth.net

import com.example.we_youth.net.converter.FULL_RESULT
import com.example.we_youth.net.converter.Response
import com.example.we_youth.net.entity.ApiArticle
import com.example.we_youth.net.entity.ApiPage
import com.example.we_youth.net.entity.User
import com.example.we_youth.utils.UIState
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
    @GET("/article/list/{page}/json")
    suspend fun getHomeArticle(
        @Path("page") page: Int
    ): UIState<ApiPage<ApiArticle>>

    @Response(value = FULL_RESULT)
    @GET("/article/list/{page}/json")
    suspend fun getHomeArticle2(
        @Path("page") page: Int
    ): WanApiResponse<ApiPage<ApiArticle>>
}