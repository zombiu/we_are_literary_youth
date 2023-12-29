package im.yixin.b.qiye.module.session.module.keyboard

import android.content.Context
import android.content.res.Resources
import android.graphics.Rect
import android.view.*
import android.widget.PopupWindow

/**
 *
 */
class KeyboardStatePopupWindow(var context: Context, anchorView: View) : PopupWindow(),
    ViewTreeObserver.OnGlobalLayoutListener {

    init {
        val contentView = View(context)
        setContentView(contentView)
        width = 0
        height = ViewGroup.LayoutParams.MATCH_PARENT
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
    var isSoftKeyboardOpened = false

    override fun onGlobalLayout() {
        // 第一次绘制时，是占满全部的，此时记录下整个屏幕的高度
        val rect = Rect()
        contentView.getWindowVisibleDisplayFrame(rect)
        // 当弹出软键盘时，rect.bottom就是弹出后，布局被压缩之后显示区域的高度
        if (rect.bottom > maxHeight) {
            maxHeight = rect.bottom
        }
        val screenHeight: Int = getScreenHeight()
        //键盘的高度
        val keyboardHeight = maxHeight - rect.bottom
        val visible = keyboardHeight > screenHeight / 3
        if (!isSoftKeyboardOpened && visible) {
            isSoftKeyboardOpened = true
            onKeyboardStateListener?.onOpened(keyboardHeight)
            KeyboardWatcher.keyboardHeight = keyboardHeight
        } else if (isSoftKeyboardOpened && !visible) {
            isSoftKeyboardOpened = false
            onKeyboardStateListener?.onClosed()
        }
    }

    fun release() {
        contentView.viewTreeObserver.removeOnGlobalLayoutListener(this)
    }

    private var onKeyboardStateListener: OnKeyboardStateListener? = null

    fun setOnKeyboardStateListener(listener: OnKeyboardStateListener?) {
        this.onKeyboardStateListener = listener
    }

    fun getScreenHeight(): Int {
        return Resources.getSystem().displayMetrics.heightPixels
    }

    interface OnKeyboardStateListener {
        fun onOpened(keyboardHeight: Int)
        fun onClosed()
    }
}