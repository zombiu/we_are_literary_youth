package com.example.we_youth.widget

import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.blankj.utilcode.util.Utils
import com.example.we_youth.R
import com.blankj.utilcode.util.ConvertUtils


/**
 * kotlin 自定义view写法
 */
class CustomFlipView : View {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val bitmapWidth = ConvertUtils.dp2px(100f)
    private val bitmapPadding = ConvertUtils.dp2px(100f).toFloat()
    private val bitmap = getBitmap(bitmapWidth)
    private val paint = Paint()
    private var camera = Camera()

    init {
        // 需要对canvas应用这个旋转效果
        camera.rotateX(60f)
    }

    override fun onDraw(canvas: Canvas) {


        canvas.translate(bitmapPadding + bitmapWidth / 2, bitmapPadding + bitmapWidth / 2)
        var count = canvas.save()
        camera.applyToCanvas(canvas)
        canvas.restoreToCount(count)
        canvas.translate(-(bitmapPadding + bitmapWidth / 2), -(bitmapPadding + bitmapWidth / 2))
        canvas.drawBitmap(bitmap, bitmapPadding, bitmapPadding, paint)
    }


    fun getBitmap(width: Int): Bitmap {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        // 很奇怪 这里传入 R.mipmap.ic_launcher 会解码失败 是因为webp的原因吗？
        BitmapFactory.decodeResource(Utils.getApp().resources, R.drawable.ic_dlam, options)
        options.inJustDecodeBounds = false
        options.inDensity = options.outWidth
        options.inTargetDensity = width
        return BitmapFactory.decodeResource(Resources.getSystem(), R.drawable.ic_dlam, options)
    }
}