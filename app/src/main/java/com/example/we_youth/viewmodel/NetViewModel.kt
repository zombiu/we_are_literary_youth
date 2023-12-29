package com.example.we_youth.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.LogUtils
import com.example.we_youth.data.LocalDataSource
import com.example.we_youth.utils.UIState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 *
 */
class NetViewModel : ViewModel() {
    // 1.replay表示当新的订阅者Collect时，发送几个已经发送过的数据给它，默认为0，即默认新订阅者不会获取以前的数据
    //2.extraBufferCapacity表示减去replay，MutableSharedFlow还缓存多少数据，默认为0
    //3.onBufferOverflow表示缓存策略，即缓冲区满了之后Flow如何处理，默认为挂起
    //
    //作者：程序员江同学
    //链接：https://juejin.cn/post/6986265488275800072
    //来源：稀土掘金
    //著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。
    private val _sharedFlow = MutableSharedFlow<UIState<String>>(replay = 1, extraBufferCapacity = 0, onBufferOverflow = BufferOverflow.SUSPEND)
    val sharedFlow: SharedFlow<UIState<String>> = _sharedFlow

    // UIState是loading时 值不重要
    private val _uiState: MutableStateFlow<UIState<Int>> = MutableStateFlow<UIState<Int>>(UIState.loading())

    // The UI collects from this StateFlow to get its state updates
    val uiState: StateFlow<UIState<Int>> = _uiState.asStateFlow()

    // 注意 使用tryEmit 发送数据时，replay 或者 extraBufferCapacity 必须有一个值大于0，否则不能发送数据
    // 注意：SharedFlow规范禁止你在缓冲区总值为零时使用onBufferOverflow = BufferOverflow.SUSPEND以外的任何东西。因为tryEmit(value: T)不会暂停，如果你用默认的replay和extraBufferCapacity值来使用它，它就不会工作。换句话说，用tryEmit(value: T)发射事件的唯一方法是，至少要有一个总缓冲区。
    private val _toastFlow: MutableSharedFlow<UIState<String>> =
        MutableSharedFlow(replay = 0, extraBufferCapacity = 0, onBufferOverflow = BufferOverflow.SUSPEND);
    val toastFlow = _toastFlow.asSharedFlow()

    fun requestData() {
        Result
        viewModelScope.launch(context = Dispatchers.IO) {
            // 模拟请求数据操作
            delay(3000)
            val randoms = (0..2).random()
            when (randoms) {
                0 -> {
                    var success = UIState.success(0)
                    _uiState.value = success

                }
                1 -> {
                    _uiState.value = UIState.failure(-1, "错误状态")
                }
                2 -> {
                    _uiState.value = UIState.loading()
                }
            }
            LogUtils.e("-->>requestData ${_uiState.value}")
        }
    }

    fun sendDataToSharedFlow() {

    }

    fun getCallbackFlow(): Flow<Int> {
        return LocalDataSource.getCallbackFlow()
    }

    fun getStateFlow(): Flow<Int> {
        return LocalDataSource.getStateFlow()
    }

    fun requestSharedFlow() {
        viewModelScope.launch(context = Dispatchers.Default) {
            // 模拟请求数据操作
            delay(1000)
            val randoms = (0..2).random()
            when (randoms) {
                0 -> {
                    var success = UIState.success("成功状态")
                    _sharedFlow.tryEmit(success)

                }
                1 -> {
                    _sharedFlow.tryEmit(UIState.failure(-1, "错误状态"))
                }
                2 -> {
                    _sharedFlow.tryEmit(UIState.loading())
                }
            }
            LogUtils.e("-->>requestSharedFlow $randoms")
        }
    }

    fun sendOnceEventByEmit() {
        viewModelScope.launch(context = Dispatchers.Default) {
            Log.e("-->>", "emit 准备toast");
            delay(3000)
            var success = UIState.success("emit 这是一个toast，加载数据成功！！")
            var tryEmit = _toastFlow.emit(success)
            Log.e("-->>", "这是一个toast，加载数据成功！！ $tryEmit")
        }
    }

    fun sendOnceEventByTryEmit() {
        viewModelScope.launch(context = Dispatchers.Default) {
            Log.e("-->>", "tryEmit 准备toast");
            delay(3000)
            var success = UIState.success("tryEmit 这是一个toast，加载数据成功！！")
            // 尝试向此共享流发出一个值，但不挂起。如果成功发出该值，则返回true(见下文)。当此函数返回false时，这意味着对普通emit函数的调用将暂停，直到有可用的缓冲区空间。
            //只有当BufferOverflow策略为SUSPEND且有订阅者收集此共享流时，此调用才能返回false。
            //如果没有订阅者，则不使用缓冲区。相反，如果配置了重放缓存，则将最近发出的值简单地存储到重放缓存中，替换那里的旧元素，如果没有配置重放缓存，则将其删除。在任何情况下，tryEmit返回true。
            //此方法是线程安全的，可以安全地从并发协程调用，而无需外部同步。
            var tryEmit = _toastFlow.tryEmit(success)
            Log.e("-->>", "这是一个toast，加载数据成功！！ $tryEmit")
        }
    }

}