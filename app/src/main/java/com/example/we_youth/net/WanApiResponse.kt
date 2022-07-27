package com.example.we_youth.net

import com.google.gson.annotations.SerializedName

/**
 *
 *
 */
data class WanApiResponse<T>(
    @field:SerializedName("data") val data: T,
    @field:SerializedName("errorCode") val errorCode: Int,
    @field:SerializedName("errorMsg") val errorMsg: String
)
