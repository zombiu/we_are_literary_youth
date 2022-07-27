package com.example.we_youth.net

const val successCode = 0

data class BaseJson<T>(var code: Int = 0, var data: T?, var msg: String?) {
    fun isSuccess(): Boolean {
        return code == successCode
    }
}