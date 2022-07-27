package com.example.we_youth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import com.blankj.utilcode.util.LogUtils
import com.example.we_youth.data.LocalDataSource
import com.example.we_youth.databinding.ActivityMainBinding
import com.example.we_youth.flow.FlowActivity
import com.example.we_youth.utils.observe
import com.example.we_youth.viewmodel.NetViewModel
import com.example.we_youth.viewmodel.WanViewModel
import com.example.we_youth.widget.CustomFlipView
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach

class MainActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    // 需要activity ktx扩展 必须是val
    val netViewModel by viewModels<NetViewModel>()
    val wanViewModel by viewModels<WanViewModel>()
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        netViewModel.test()

        /*launch(context = Dispatchers.Default) {
            LogUtils.e("-->>")

            // lifecycle-runtime-ktx 2.4.0以上才有 repeatOnLifecycle函数
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // 处于STARTED状态时会开始收集流，并且在RESUMED状态时保持收集，最终在Fragment进入STOPPED状态时结束收集过程
                netViewModel.getFlow().collect {
                    Log.e("-->>","接收到的数据3 ${it}")
                }
            }
        }*/


//        val flow2 = LocalDataSource.getFlow2()

        val flow1 = callbackFlow<Int> {
            for (element in listOf(1, 2, 3, 4, 5)) {
                delay(1000)
                trySend(element)
                Log.e("-->>", "开始发送 $element")
            }
            close()
            awaitClose {
                Log.e("-->>", "关闭flow1")
            }
        }
        val flow2 = callbackFlow<Int> {
            for (element in listOf(5, 6, 7, 8, 9)) {
                delay(1000)
                trySend(element)
                Log.e("-->>", "开始发送 $element")
            }
            close()
            // 'awaitClose { yourCallbackOrListener.cancel() }' should be used in the end of callbackFlow block.
            awaitClose {
                Log.e("-->>", "关闭flow2")
            }
        }


        /*GlobalScope.launch(Dispatchers.IO) {
            flow1.collect() {
                Log.e("-->>", "接收到的数据1 ${it}")
            }
            flow2.collect() {
                Log.e("-->>", "接收到的数据2 ${it}")
            }
        }*/

        /*launch(context = Dispatchers.Default) {
            LogUtils.e("-->>")
            flow1.collect() {
                Log.e("-->>","接收到的数据1 ${it}")
            }
            flow2.collect() {
                Log.e("-->>","接收到的数据2 ${it}")
            }
        }*/

        /*launch(context = Dispatchers.Default) {
            LogUtils.e("-->>")
            // onEach 在上游给下游发送数据之前调用
            LocalDataSource.getFlow2().onEach {
                Log.e("-->>","onEach 1 ${it}")
            }.collect() {
                Log.e("-->>","接收到的数据1 ${it}")
            }
            LocalDataSource.getFlow2().onEach {

            }.collect() {
                Log.e("-->>","接收到的数据2 ${it}")
            }
        }*/
//        testFlowEx()

//        testFlowEx1()

//        testFlowEx2()

        binding.btnFlow.setOnClickListener {
            startActivity(Intent(this, FlowActivity::class.java))

        }

        binding.btnAnimation.setOnClickListener {
            startActivity(Intent(this, AnimationActivity::class.java))
            wanViewModel.getHomeArticle()
        }
    }


    private fun testFlowEx() {
        launch(context = Dispatchers.Default) {
            LogUtils.e("-->>")
            // onEach 在上游给下游发送数据之前调用

        }

        LocalDataSource.getCallbackFlow().onEach {
            Log.e("-->>", "onEach ${it}")
        }.observe(this@MainActivity) {
            Log.e("-->>", "data=$it")
        }
    }

    private fun testFlowEx1() {
        val intFlow = flow {
            (1..3).forEach {
                emit(it)
                delay(100)
            }
        }
        launch {
            GlobalScope.launch(Dispatchers.IO) {
                intFlow.collect { println("-->>data1= $it") }
                intFlow.collect { println("-->>data2= $it") }
            }
        }
    }

    private fun testFlowEx2() {


    }
}