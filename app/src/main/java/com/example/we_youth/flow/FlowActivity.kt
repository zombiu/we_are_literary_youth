package com.example.we_youth.flow

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.example.we_youth.databinding.ActivityFlowBinding
import com.example.we_youth.utils.UIState
import com.example.we_youth.utils.observe
import com.example.we_youth.viewmodel.NetViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.onEach
import java.util.concurrent.CancellationException

// by MainScope() 需要依赖kotlinx-coroutines-android
class FlowActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    //    val mainScope = MainScope()
    // 需要activity ktx扩展 必须是val
    val netViewModel by viewModels<NetViewModel>()
    lateinit var binding: ActivityFlowBinding
    var job: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFlowBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initLogic()
    }

    private fun initLogic() {
        binding.btnCallbackFlow.setOnClickListener {
            job = launch {
                netViewModel.getCallbackFlow().collect() {
                    Log.e("-->>", "callbackFlow 接收到的数据1 ${it}")
                }
                netViewModel.getCallbackFlow().collect() {
                    Log.e("-->>", "callbackFlow 接收到的数据2 ${it}")
                }
            }
        }

        binding.btnCancelFlow.setOnClickListener {
            if (job != null) {
                job?.cancel(CancellationException("手动取消"))
                job = null
            }

        }

        binding.btnSharedFlow.setOnClickListener {
            netViewModel.requestSharedFlow()
        }


        binding.btnStateFlow.setOnClickListener {
            netViewModel.requestData()
            /*netViewModel.getStateFlow().observe(this) {
                LogUtils.e("-->>获取的数据=$it")
            }*/
        }

        // 重复订阅 查看下游状态 是否正确
        binding.btnStateFlow2.setOnClickListener {
            // 使用扩展函数 来响应界面的生命周期
            netViewModel.uiState.onEach {

            }.observe(this) { state ->
                when (state) {
                    is UIState.Success -> {
                        Log.e("-->>", "state= ${state.isSuccess}")
                        LogUtils.e("-->>请求成功2=" + state.data)
                    }
                    is UIState.Failure -> {
                        LogUtils.e("-->>请求失败2=$state")
                    }
                    is UIState.Loading -> {
                        LogUtils.e("-->>请求中2=$state")
                    }
                }
            }
        }

        // 使用官方提供的 repeatOnLifecycle API 来响应界面的生命周期
        // 修改context 可以将collect调度到任意线程上
        launch(context = Dispatchers.Default) {
            // 注意：repeatOnLifecycle API 仅在 androidx.lifecycle:lifecycle-runtime-ktx:2.4.0 库及更高版本中提供。
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // 处于STARTED状态时会开始收集流，并且在RESUMED状态时保持收集，最终在Fragment进入STOPPED状态时结束收集过程
                netViewModel.uiState.collect() { state ->
                    when (state) {
                        is UIState.Success -> {
                            Log.e("-->>", "state= ${state.isSuccess}")
                            LogUtils.e("-->>请求成功1=" + state.data)
                        }
                        is UIState.Failure -> {
                            LogUtils.e("-->>请求失败1=$state")
                        }
                        is UIState.Loading -> {
                            LogUtils.e("-->>请求中1=$state")
                        }
                    }
                }
            }
        }

        binding.btnOnceEvent.setOnClickListener {
            netViewModel.sendOnceEvent()
        }


        netViewModel.sharedFlow.observe(this) {
            LogUtils.e("-->>$it")
        }

        // 导航/吐司这样的单次事件，不需要粘性事件，这里使用sharedFlow
        launch(context = Dispatchers.Main) {
            netViewModel.toastFlow.collect() {
                Log.e("-->>", "toastState=$it")
                if (it is UIState.Success) {
                    ToastUtils.showShort(it.data)
                }
            }
        }
    }
}