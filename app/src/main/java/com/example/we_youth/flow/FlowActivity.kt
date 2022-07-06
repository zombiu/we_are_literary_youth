package com.example.we_youth.flow

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.blankj.utilcode.util.LogUtils
import com.example.we_youth.R
import com.example.we_youth.databinding.ActivityFlowBinding
import com.example.we_youth.utils.UIState
import com.example.we_youth.utils.observe
import com.example.we_youth.viewmodel.NetViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
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

        }

        binding.btnStateFlow.setOnClickListener {
//            netViewModel.requestData()
            netViewModel.getStateFlow().observe(this) {
                LogUtils.e("-->>获取的数据=$it")
            }
        }

        // 使用官方提供的 repeatOnLifecycle API 来响应界面的生命周期
        /*launch {
            // 注意：repeatOnLifecycle API 仅在 androidx.lifecycle:lifecycle-runtime-ktx:2.4.0 库及更高版本中提供。
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // 处于STARTED状态时会开始收集流，并且在RESUMED状态时保持收集，最终在Fragment进入STOPPED状态时结束收集过程
                netViewModel.uiState.collect() { state ->
                    when (state) {
                        is UIState.Success -> {
                            Log.e("-->>", "state= ${state.isSuccess}")
                            LogUtils.e("-->>请求成功=" + state.data)
                        }
                        is UIState.Failure -> {
                            LogUtils.e("-->>请求失败=$state")
                        }
                        is UIState.Loading -> {
                            LogUtils.e("-->>请求中=$state")
                        }
                    }
                }
            }
        }*/

        // 使用扩展函数 来响应界面的生命周期
        netViewModel.uiState.onEach {

        }.observe(this) { state ->
            when (state) {
                is UIState.Success -> {
                    Log.e("-->>", "state= ${state.isSuccess}")
                    LogUtils.e("-->>请求成功=" + state.data)
                }
                is UIState.Failure -> {
                    LogUtils.e("-->>请求失败=$state")
                }
                is UIState.Loading -> {
                    LogUtils.e("-->>请求中=$state")
                }
            }
        }

    }
}