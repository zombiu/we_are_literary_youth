package com.example.we_youth.data

import android.util.Log
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.LogUtils
import com.example.we_youth.utils.toShared
import com.example.we_youth.utils.toState
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*

object LocalDataSource {

    fun getFlow(): Flow<Int> {
        return listOf(1, 2, 3, 4, 5).asFlow()
            .onEach {
                delay(1000)
            }.toShared()
    }

    fun getStateFlow(): Flow<Int> {
        return listOf(1, 2, 3, 4, 5).asFlow()
            .onEach {
                delay(1000)
            }.toState(100)
    }

    // 将基于回调的 API 转换为数据流
    // 使用场景：为了将基于回调的API转换为Flow
    fun getCallbackFlow() = callbackFlow<Int> {
        for (element in listOf(1, 2, 3, 4, 5)) {
            delay(1000)
            trySend(element)
        }
        // 取消流收集cancel()或基于回调的 API 手动调用 SendChannel.close() 时调用或外部的协程被取消时，才会调用awaitClose。换句话说，需要手动关闭创建的callbackFlow，否则就会一直处于运行状态不会结束
        close()
        awaitClose {
            Log.e("-->>", "关闭flow2")
        }
    }.onCompletion {
        LogUtils.e("-->>callbackFlow完成时调用")
    }.catch {
        LogUtils.e("-->>捕捉异常$this")
    }.shareIn(
        ProcessLifecycleOwner.get().lifecycleScope,// 用于共享数据流的 CoroutineScope。此作用域函数的生命周期应长于任何使用方，以使共享数据流在足够长的时间内保持活跃状态。
        SharingStarted.WhileSubscribed(),//使Flow仅在订阅者数量从0变为1时才开始共享（实现），并在订阅者数量从1变为0时停止共享
        1
    )//新订阅者将在订阅时立即获得之前最后发出的值,这里的replay是指发送最近的多少个数据


}
