package com.example.we_youth.widget

import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.*
import android.widget.PopupWindow
import com.blankj.utilcode.util.ScreenUtils

/**
 * @author  FreddyChen
 * @name
 * @date    2020/06/08 16:37
 * @email   chenshichao@outlook.com
 * @github  https://github.com/FreddyChen
 * @desc
 */
class KeyboardStatePopupWindow(var context: Context, anchorView: View) : PopupWindow(),
    ViewTreeObserver.OnGlobalLayoutListener {

    var keyboardHeight = 0

    init {
        val contentView = View(context)
        setContentView(contentView)
        width = 0
//        width = ConvertUtils.dp2px( 50f)
        height = ViewGroup.LayoutParams.MATCH_PARENT
        setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setBackgroundDrawable(ColorDrawable(Color.BLUE))
        softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        inputMethodMode = INPUT_METHOD_NEEDED
        contentView.viewTreeObserver.addOnGlobalLayoutListener(this)

        anchorView.post {
            showAtLocation(
                anchorView,
                Gravity.NO_GRAVITY,
                0,
                0
            )
        }
    }

    private var maxHeight = 0
    private var isSoftKeyboardOpened = false

    override fun onGlobalLayout() {
        // 第一次绘制时，是占满全部的，此时记录下整个屏幕的高度
        val rect = Rect()
        contentView.getWindowVisibleDisplayFrame(rect)
        // 当弹出软键盘时，rect.bottom就是弹出后，布局被压缩之后显示区域的高度
        if (rect.bottom > maxHeight) {
            maxHeight = rect.bottom
        }
        val screenHeight: Int = ScreenUtils.getScreenHeight()
        //键盘的高度
        val keyboardHeight = maxHeight - rect.bottom
        val visible = keyboardHeight > screenHeight / 4
        if (!isSoftKeyboardOpened && visible) {
            isSoftKeyboardOpened = true
            onKeyboardStateListener?.onOpened(keyboardHeight)
            this.keyboardHeight = keyboardHeight
        } else if (isSoftKeyboardOpened && !visible) {
            isSoftKeyboardOpened = false
            onKeyboardStateListener?.onClosed()
        }
        Log.e("-->>", "onGlobalLayout")
    }

    fun release() {
        contentView.viewTreeObserver.removeOnGlobalLayoutListener(this)
    }

    private var onKeyboardStateListener: OnKeyboardStateListener? = null

    fun setOnKeyboardStateListener(listener: OnKeyboardStateListener?) {
        this.onKeyboardStateListener = listener
    }

    interface OnKeyboardStateListener {
        fun onOpened(keyboardHeight: Int)
        fun onClosed()
    }
}