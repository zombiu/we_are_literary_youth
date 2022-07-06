package com.example.we_youth.utils

import java.io.Serializable

/**
 * 密封类不能被实例化
 */
sealed class UIState<out T> constructor(val value: Any?) {
    data class Success<out T>(val data: T) : UIState<T>(data)

    // Nothing 是一个 空类型（uninhabited type），也就是说，程序运行时不会出现任何一个 Nothing 类型对象。Nothing 还是其他所有类型的子类型。
    // Kotlin中一切方法都是表达式，也就是都有返回值，那么正常方法返回 Unit ，无法正常返回的方法就返回 Nothing
    // 如果一个方法的返回类型定义为 Nothing ，那么这个方法就是无法正常返回的
    data class Failure(val error: ApiError) : UIState<Nothing>(error)
    data class Loading(val data: Any?) : UIState<Nothing>(data)

//    val isSuccess: Boolean get() = value !is ApiError && this is Success
//
//    val isFailure: Boolean get() = value is ApiError

    val isSuccess: Boolean get() = this is Success

    val isFailure: Boolean get() = this is Failure

    companion object {
        inline fun <T> success(value: T): UIState<T> =
            Success(value)

        inline fun failure(errorCode: Int, errorMsg: String): UIState<Nothing> =
            Failure(ApiError(errorCode, errorMsg))

        inline fun loading(): UIState<Nothing> =
            Loading(null)

    }

    class ApiError(val errorCode: Int, val errorMsg: String?) : Serializable {
        override fun equals(other: Any?): Boolean = other is ApiError && errorCode == other.errorCode
        override fun hashCode(): Int {
            var result = errorCode
            result = 31 * result + (errorMsg?.hashCode() ?: 0)
            return result
        }

        override fun toString(): String {
            return "ApiError(errorCode=$errorCode, errorMsg=$errorMsg)"
        }
    }
}

// 用来处理导航 或者toast 这种一次性事件
fun <T> UIState<T>.once() = Event(this)

/**
 * Used as a wrapper for data that is exposed via a LiveData that represents an event.
 */
open class Event<out T>(private val content: T) {

    var hasBeenHandled = false
        private set // Allow external read but not write

    /**
     * Returns the content and prevents its use again.
     */
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    /**
     * Returns the content, even if it's already been handled.
     */
    fun peekContent(): T = content
}



