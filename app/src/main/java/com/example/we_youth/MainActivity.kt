package com.example.we_youth

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.collection.LruCache
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.LogUtils
import com.example.we_youth.data.LocalDataSource
import com.example.we_youth.databinding.ActivityMainBinding
import com.example.we_youth.flow.FlowActivity
import com.example.we_youth.ui.KeyboardActivity
import com.example.we_youth.ui.RetrofitActivity
import com.example.we_youth.utils.observe
import com.example.we_youth.viewmodel.NetViewModel
import com.example.we_youth.viewmodel.WanViewModel
import com.example.we_youth.widget.CustomFlipView
import com.tencent.matrix.trace.view.FrameDecorator
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach

class MainActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    // 需要activity ktx扩展 必须是val
    val netViewModel by viewModels<NetViewModel>()
    lateinit var binding: ActivityMainBinding

    inner class TimeChangeReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                Intent.ACTION_TIME_TICK -> {
                    LogUtils.e("-->>onReceive", GsonUtils.toJson(intent.data) + ",过了一分钟了")
                }
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //        监听时间变化
        var intentFilter = IntentFilter()
        intentFilter.addAction(Intent.ACTION_TIME_TICK)
        var timeChangeReceiver = TimeChangeReceiver()
        registerReceiver(timeChangeReceiver, intentFilter)

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
            App.write("打开了FlowActivity")
            App.read()
        }

        binding.btnAnimation.setOnClickListener {
            startActivity(Intent(this, AnimationActivity::class.java))
            App.write("打开了AnimationActivity")
            App.read()
        }

        binding.btnRetrofit.setOnClickListener {
            startActivity(Intent(this, RetrofitActivity::class.java))
            App.write("打开了RetrofitActivity")
            App.read()
        }

        binding.btnInput.setOnClickListener {
            startActivity(Intent(this, KeyboardActivity::class.java))
            App.write("打开了KeyboardActivity")
            App.read()
        }

        binding.btnOpen.setOnClickListener {
            App.write("打开了第三方app")
            App.read()
            //第二种方式：通过包名跳转到另一个app的启动页
            val intent: Intent = packageManager.getLaunchIntentForPackage("xx.xx.xx")!!
            intent.putExtra("type", "110")
            intent.putExtra("selector","传递过去的数据")
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }

        FrameDecorator.getInstance(this).show()

//        开发者需要重写 LruCache#sizeOf() 测量缓存单元的内存占用量，否则缓存单元的大小默认视为 1，相当于 maxSize 表示的是最大缓存数量。
        var lruCache:LruCache<String,String> = LruCache<String,String> (5000)
        lruCache.put("1","1")
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