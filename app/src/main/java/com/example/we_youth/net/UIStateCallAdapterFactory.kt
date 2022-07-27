package com.example.we_youth.net

import com.blankj.utilcode.util.LogUtils
import com.example.we_youth.utils.UIState
import okhttp3.Request
import okio.Timeout
import retrofit2.*
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class UIStateCallAdapterFactory : CallAdapter.Factory() {
    override fun get(returnType: Type, annotations: Array<Annotation>, retrofit: Retrofit): CallAdapter<*, *>? {
        check(getRawType(returnType) == Call::class.java) { "$returnType must be retrofit2.Call." }
        check(returnType is ParameterizedType) { "$returnType must be parameterized. Raw types are not supported" }

        val apiResultType = getParameterUpperBound(0, returnType)
        check(getRawType(apiResultType) == UIState::class.java) { "$apiResultType must be UIState." }
        check(apiResultType is ParameterizedType) { "$apiResultType must be parameterized. Raw types are not supported" }

        val dataType = getParameterUpperBound(0, apiResultType)
        return UIStateCallAdapter<Any>(dataType)
    }
}

class UIStateCallAdapter<T>(private val type: Type) : CallAdapter<T, Call<UIState<T>>> {
    override fun responseType(): Type = type

    override fun adapt(call: Call<T>): Call<UIState<T>> {
        return UIStateCall(call)
    }
}

class UIStateCall<T>(private val delegate: Call<T>) : Call<UIState<T>> {

    override fun enqueue(callback: Callback<UIState<T>>) {
        delegate.enqueue(object : Callback<T> {

            override fun onResponse(call: Call<T>, response: Response<T>) {
                if (response.isSuccessful) {
                    val apiResult = if (response.body() == null) {
                        UIState.failure(ApiError.emptyData.errorCode, ApiError.emptyData.errorMsg)
                    } else {
                        // 这里将 WanApiResponse的 data取出来 ，要实现需要使用自定义的 GsonConverterFactory
                        LogUtils.e("-->> ${response.body()!!::class.java}")
                        UIState.success(response.body()!!)
                    }
                    callback.onResponse(this@UIStateCall, Response.success(apiResult))
                } else {
                    val failureApiResult =  UIState.failure(response.code(), response.message())
                    callback.onResponse(this@UIStateCall, Response.success(failureApiResult))
                }

            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                //Interceptor里会通过throw ApiException 来直接结束请求 同时ApiException里会包含错误信息
                val failureApiResult = if (t is ApiException) {
                    UIState.failure(t.errorCode, t.errorMessage)
                } else {
                    UIState.failure(ApiError.netError.errorCode, ApiError.netError.errorMsg)
                }
                callback.onResponse(this@UIStateCall, Response.success(failureApiResult))
            }
        })
    }

    override fun clone(): Call<UIState<T>> = UIStateCall(delegate.clone())

    override fun execute(): Response<UIState<T>> {
        throw UnsupportedOperationException("ApiResultCall does not support synchronous execution")
    }


    override fun isExecuted(): Boolean {
        return delegate.isExecuted
    }

    override fun cancel() {
        delegate.cancel()
    }

    override fun isCanceled(): Boolean {
        return delegate.isCanceled
    }

    override fun request(): Request {
        return delegate.request()
    }

    override fun timeout(): Timeout {
        return delegate.timeout()
    }
}




