package com.example.we_youth.flow

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import com.blankj.utilcode.util.LogUtils
import com.example.we_youth.R
import com.example.we_youth.databinding.ActivityFlowBinding
import com.example.we_youth.utils.UIState
import com.example.we_youth.viewmodel.NetViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.collect
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

            netViewModel.requestData()
        }

        launch {
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
    }
}