package com.example.we_youth.utils

import android.app.Activity
import android.content.Context
import android.os.Build

object UIKit {

    /**
     * 平行窗口模式（华为、小米）
     */
    fun inMagicWindow(context: Context): Boolean {
        val config: String = context.resources.configuration.toString()
        return config.contains("hwMultiwindow-magic") || config.contains("hw-magic-windows") // || config.contains("miui-magic-windows")
    }


    fun isMuiltWindowMode(activity: Activity): Boolean {
        var isInMultiWindowMode = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            isInMultiWindowMode = activity.isInMultiWindowMode
        }
        return isInMultiWindowMode
    }

    /**
     * 判断是否平板设备，此值不会改变
     */
//    val isTabletDevice: Boolean by lazy {
//        SystemPropertiesProxy.get(context, "ro.build.characteristics")?.contains("tablet") == true
//    }

}