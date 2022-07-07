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
 * 遇到的bug：写demo时，遇到很多次flow发送事件无效的情况，可能是编译器有问题还是咋的，多运行几次就好了
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

    // 注意 replay 或者 extraBufferCapacity 必须有一个值大于0，否则不能发送数据
    private val _toastFlow: MutableSharedFlow<UIState<String>> =
        MutableSharedFlow(replay = 0, extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.SUSPEND);
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

    fun sendOnceEvent() {
        viewModelScope.launch(context = Dispatchers.IO) {
            Log.e("-->>", "准备toast");
            delay(3000)
            var success = UIState.success("这是一个toast，加载数据成功！！")
            var tryEmit = _toastFlow.tryEmit(success)
            Log.e("-->>", "这是一个toast，加载数据成功！！ $tryEmit")
        }
    }
}