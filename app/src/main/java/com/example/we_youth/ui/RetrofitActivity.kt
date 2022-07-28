package com.example.we_youth.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.blankj.utilcode.util.LogUtils
import com.example.we_youth.R
import com.example.we_youth.databinding.ActivityMainBinding
import com.example.we_youth.databinding.ActivityRetrofitBinding
import com.example.we_youth.utils.UIState
import com.example.we_youth.viewmodel.WanViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class RetrofitActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    val wanViewModel by viewModels<WanViewModel>()
    lateinit var binding: ActivityRetrofitBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRetrofitBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.tvResp.setMovementMethod(ScrollingMovementMethod.getInstance())

        binding.btnHttp.setOnClickListener {
            wanViewModel.getHomeArticle()
        }


        launch(context = Dispatchers.Main) {
            // 注意：repeatOnLifecycle API 仅在 androidx.lifecycle:lifecycle-runtime-ktx:2.4.0 库及更高版本中提供。
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // 处于STARTED状态时会开始收集流，并且在RESUMED状态时保持收集，最终在Fragment进入STOPPED状态时结束收集过程
                wanViewModel.uiState.collect() { state ->
                    when (state) {
                        is UIState.Success -> {
                            Log.e("-->>", "state= ${state.isSuccess}")
                            LogUtils.e("-->>请求成功1=" + state.data)
                            binding.tvResp.text = state.data.toString()
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
    }
}