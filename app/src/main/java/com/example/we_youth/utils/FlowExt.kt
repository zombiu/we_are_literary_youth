package com.example.we_youth.utils

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


@PublishedApi
internal class ObserverImpl<T> (
    lifecycleOwner: LifecycleOwner,
    private val flow: Flow<T>,
    private val collector: suspend (T) -> Unit
) : DefaultLifecycleObserver {

    private var job: Job? = null

    override fun onStart(owner: LifecycleOwner) {
        //onStart时，开启job
        job = owner.lifecycleScope.launch {
            //onStart时，进行collect
            flow.collect {
                collector(it)
            }
        }
    }

    override fun onStop(owner: LifecycleOwner) {
        //onStop时，取消该job
        job?.cancel()
        job = null
    }

    init {
        lifecycleOwner.lifecycle.addObserver(this)
    }
}

inline fun <reified T> Flow<T>.observe(
    lifecycleOwner: LifecycleOwner,
    noinline collector: suspend (T) -> Unit
) {
    ObserverImpl(lifecycleOwner, this, collector)
}

inline fun <reified T> Flow<T>.observeIn(
    lifecycleOwner: LifecycleOwner
) {
    ObserverImpl(lifecycleOwner, this, {})
}

// 利用 shareIn 使冷数据流变为热数据流
fun <T> Flow<T>.toShared() = shareIn(
    ProcessLifecycleOwner.get().lifecycleScope,// 用于共享数据流的 CoroutineScope。此作用域函数的生命周期应长于任何使用方，以使共享数据流在足够长的时间内保持活跃状态。
    SharingStarted.WhileSubscribed(),//使Flow仅在订阅者数量从0变为1时才开始共享（实现），并在订阅者数量从1变为0时停止共享
    1, //新订阅者将在订阅时立即获得之前最后发出的值,这里的replay是指发送最近的多少个数据
)


// 上面那种写法报错 为啥？
fun <T> Flow<T>.toState(initialValue: T) = stateIn(
    ProcessLifecycleOwner.get().lifecycleScope,//
    started = SharingStarted.WhileSubscribed(),//使Flow仅在订阅者数量从0变为1时才开始共享（实现），并在订阅者数量从1变为0时停止共享
    initialValue, // stateFlow需要一个初始值
)
