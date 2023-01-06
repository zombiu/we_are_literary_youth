package com.example.we_youth

import android.app.Application
import android.util.Log
import com.blankj.utilcode.util.LogUtils
import com.example.we_youth.matrix.config.DynamicConfigImplDemo
import com.example.we_youth.matrix.listener.TestPluginListener
import com.tencent.matrix.Matrix
import com.tencent.matrix.trace.TracePlugin
import com.tencent.matrix.trace.config.TraceConfig
import com.tencent.matrix.util.MatrixLog
import glog.android.Glog
import java.io.File
import java.io.IOException


class App : Application() {
    val TAG = "-->>${this.javaClass.name}"


    companion object {
        var glog: Glog? = null

        fun log(): Glog {
            return glog!!
        }

        // 写入日志
        public fun write(content: String) {
            val data: ByteArray = serialize(content) // 序列化数据
            var b = glog!!.write(data) // 写入二进制数组
            Log.e("-->>", "写入是否成功 $b")
        }

        private fun serialize(content: String): ByteArray {
            return content.toByteArray()
        }

        // 读取日志
        public fun read() {
            val logFiles: ArrayList<String> = ArrayList()
            glog!!.getArchiveSnapshot(logFiles, 10, 100 * 1024) // 获取日志文件快照，当 cache 中日志条数 >=10 或体积 >= 100 KB 将自动 flush
            val inBuffer = ByteArray(Glog.getSingleLogMaxLength())
            for (logFile in logFiles) {
                try {
                    glog!!.openReader(logFile).use { reader ->
                        while (true) {
                            val n = reader.read(inBuffer)
                            if (n < 0) {
                                break
                            } else if (n == 0) { // 触发容错
                                continue
                            }
                            val outBuffer = ByteArray(n)
                            System.arraycopy(inBuffer, 0, outBuffer, 0, n)
                            deserialize(outBuffer) // 反序列化数据
                        }
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }

        private fun deserialize(outBuffer: ByteArray) {
            LogUtils.e("-->>GLOG", String(outBuffer))
        }
    }

    override fun onCreate() {
        super.onCreate()


        // Switch.
        val dynamicConfig = DynamicConfigImplDemo()

        MatrixLog.i(TAG, "Start Matrix configurations.")

        // Builder. Not necessary while some plugins can be configured separately.

        val builder = Matrix.Builder(this)


        // Reporter. Matrix will callback this listener when found issue then emitting it.
        builder.pluginListener(TestPluginListener(this))


        // Configure trace canary.
        val tracePlugin: TracePlugin = configureTracePlugin(dynamicConfig)
        builder.plugin(tracePlugin)

        Matrix.init(builder.build())
        // Trace Plugin need call start() at the beginning.
//        tracePlugin.start();
//        memoryCanaryPlugin.start();


        // Trace Plugin need call start() at the beginning.
        tracePlugin.start();
//        memoryCanaryPlugin.start();
//        Matrix.with().startAllPlugins()

        // 全局初始化 设置内部调试日志等级
        try {
            Glog.initialize(if (BuildConfig.DEBUG) Glog.InternalLogLevel.InternalLogLevelDebug else Glog.InternalLogLevel.InternalLogLevelInfo)
            Log.d("glog", "glog initialize success")
        } catch (t: Throwable) {
            Log.e("glog", "glog initialize failed", t)
        }
        setup()
    }

    private fun configureTracePlugin(dynamicConfig: DynamicConfigImplDemo): TracePlugin {
        val fpsEnable = dynamicConfig.isFPSEnable
        val traceEnable = dynamicConfig.isTraceEnable
        val signalAnrTraceEnable = dynamicConfig.isSignalAnrTraceEnable
        val traceFileDir = File(applicationContext.filesDir, "matrix_trace")
        if (!traceFileDir.exists()) {
            if (traceFileDir.mkdirs()) {
                MatrixLog.e(TAG, "failed to create traceFileDir")
            }
        }
        val anrTraceFile = File(traceFileDir, "anr_trace") // path : /data/user/0/sample.tencent.matrix/files/matrix_trace/anr_trace
        val printTraceFile = File(traceFileDir, "print_trace") // path : /data/user/0/sample.tencent.matrix/files/matrix_trace/print_trace
        val traceConfig = TraceConfig.Builder()
            .dynamicConfig(dynamicConfig)
            .enableFPS(fpsEnable)
            .enableEvilMethodTrace(traceEnable)
            .enableAnrTrace(traceEnable)
            .enableStartup(traceEnable)
            .enableIdleHandlerTrace(traceEnable) // Introduced in Matrix 2.0
            .enableMainThreadPriorityTrace(true) // Introduced in Matrix 2.0
            .enableSignalAnrTrace(signalAnrTraceEnable) // Introduced in Matrix 2.0
            .anrTracePath(anrTraceFile.absolutePath)
            .printTracePath(printTraceFile.absolutePath)
//            .splashActivities("sample.tencent.matrix.SplashActivity;")
            .isDebug(true)
            .isDevEnv(false)
            .build()

        //Another way to use SignalAnrTracer separately
        //useSignalAnrTraceAlone(anrTraceFile.getAbsolutePath(), printTraceFile.getAbsolutePath());
        return TracePlugin(traceConfig)
    }

    // 初始化实例
    private fun setup() {
        glog = Glog.Builder(applicationContext)
            .rootDirectory(externalCacheDir.toString())
            .protoName("glog_identify") // 实例标识，相同标识的实例只创建一次
//            .encryptMode(Glog.EncryptMode.AES) // 加密方式
//            .key("123") // ECDH Server public key
            .incrementalArchive(true) // 增量归档，当天日志写入同一文件
            .build()
    }
}