package com.example.we_youth

import android.app.Application
import com.example.we_youth.matrix.config.DynamicConfigImplDemo
import com.example.we_youth.matrix.listener.TestPluginListener
import com.tencent.matrix.Matrix
import com.tencent.matrix.trace.TracePlugin
import com.tencent.matrix.trace.config.TraceConfig
import com.tencent.matrix.util.MatrixLog
import java.io.File

class App : Application() {
    val TAG = "-->>${this.javaClass.name}"

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
}