package im.yixin.b.qiye.module.session.module.keyboard

import android.app.Activity
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.blankj.utilcode.util.LogUtils

import java.util.*

object KeyboardWatcher : LifecycleObserver {

    var screenHeight = 0
    var keyboardHeight: Int = 0
    private var keyboardStatePopupWindow: KeyboardStatePopupWindow? = null

    private val listeners: MutableList<SoftKeyboardStateListener> = LinkedList()

    fun init(act: FragmentActivity) {
        act.lifecycle.addObserver(this)
        // 创建 软键盘弹出监听 窗口
        createFullWindow(act, act.window.decorView)
        LogUtils.e("-->>", "初始化KeyboardWatcher")
    }


    private fun createFullWindow(activity: Activity, anchorView: View) {
        keyboardStatePopupWindow = KeyboardStatePopupWindow(activity, anchorView)
        keyboardStatePopupWindow?.setOnKeyboardStateListener(object :
            KeyboardStatePopupWindow.OnKeyboardStateListener {
            override fun onOpened(keyboardHeight: Int) {
                this@KeyboardWatcher.keyboardHeight = keyboardHeight
                LogUtils.e("-->>", "软键盘高度=$keyboardHeight  onOpened")
                notifyOnSoftKeyboardOpened(keyboardHeight)
            }

            override fun onClosed() {
                LogUtils.e("-->>", "软键盘  onClosed")
                notifyOnSoftKeyboardClosed()
            }
        })
        screenHeight = keyboardStatePopupWindow!!.getScreenHeight()
    }

    fun addSoftKeyboardStateListener(listener: SoftKeyboardStateListener) {
        listeners.add(listener)
    }

    fun removeSoftKeyboardStateListener(listener: SoftKeyboardStateListener) {
        listeners.remove(listener)
    }

    private fun notifyOnSoftKeyboardOpened(keyboardHeightInPx: Int) {
        for (listener in listeners) {
            listener.onSoftKeyboardOpened(keyboardHeightInPx)
        }
    }

    private fun notifyOnSoftKeyboardClosed() {
        for (listener in listeners) {
            listener.onSoftKeyboardClosed()
        }
    }

    fun clean() {
        listeners.clear()
    }

    fun keyboardIsShow(): Boolean {
        return keyboardStatePopupWindow?.isSoftKeyboardOpened == true
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onLifeDestroy() {
        keyboardStatePopupWindow?.release()
        keyboardStatePopupWindow?.dismiss()
    }

    interface SoftKeyboardStateListener {
        fun onSoftKeyboardOpened(keyboardHeight: Int)
        fun onSoftKeyboardClosed()
    }
}