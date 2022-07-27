package com.example.we_youth.widget

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.View
import androidx.core.graphics.withSave
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

    private val bitmapWidth = ConvertUtils.dp2px(200f)
    private val bitmapPadding = ConvertUtils.dp2px(80f).toFloat()
    private val bitmap = getBitmap(bitmapWidth)
    private val paint = Paint()
    private var camera = Camera()
    private var bottomRectF = RectF()
    private var topRectF = RectF()

    private var rotateDegress = 0f
        set(value) {
            field = value
            invalidate()
        }

    private val rotateAnimation by lazy {
        ObjectAnimator.ofFloat(this, "rotateDegress", 0f, 360f)
    }

    init {
        camera.setLocation(0f, 0f, -5 * resources.displayMetrics.density)
        // 需要对canvas应用这个旋转效果
        camera.rotateX(30f)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        bottomRectF.set(-bitmapWidth.toFloat(), 0f, bitmapWidth.toFloat(), bitmapWidth.toFloat())
        topRectF.set(-bitmapWidth.toFloat(), -bitmapWidth.toFloat(), bitmapWidth.toFloat(), 0f)

        rotateAnimation.duration = 2000
        rotateAnimation.startDelay = 1500
        rotateAnimation.start()
    }

    override fun onDraw(canvas: Canvas) {
        // 上半部分
        canvas.withSave {
            canvas.translate(bitmapPadding + bitmapWidth / 2, bitmapPadding + bitmapWidth / 2)
            canvas.rotate(-rotateDegress)
            // 进行剪裁 保留下半部分
            canvas.clipRect(topRectF)
            // 进行旋转 重点：canvas中的rotate方法是绕画布左上角（0,0）进行旋转的，而且会受到translate的影响
            canvas.rotate(rotateDegress)
            canvas.translate(-(bitmapPadding + bitmapWidth / 2), -(bitmapPadding + bitmapWidth / 2))
            canvas.drawBitmap(bitmap, bitmapPadding, bitmapPadding, paint)
        }

        // 下半部分
        var count = canvas.save()
        canvas.translate(bitmapPadding + bitmapWidth / 2, bitmapPadding + bitmapWidth / 2)
        canvas.rotate(-rotateDegress)
        camera.save()
        camera.applyToCanvas(canvas)
        camera.restore()
        // 进行剪裁 保留下半部分
        canvas.clipRect(bottomRectF)
        // 进行旋转 重点：canvas中的rotate方法是绕画布左上角（0,0）进行旋转的，而且会受到translate的影响
        // 参数里的 degrees 是旋转角度，单位是度
        canvas.rotate(rotateDegress)
        canvas.translate(-(bitmapPadding + bitmapWidth / 2), -(bitmapPadding + bitmapWidth / 2))
        canvas.drawBitmap(bitmap, bitmapPadding, bitmapPadding, paint)
        canvas.restoreToCount(count)
    }


    fun getBitmap(width: Int): Bitmap {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeResource(Utils.getApp().resources, R.drawable.ic_dlam, options)
        options.inJustDecodeBounds = false
        options.inDensity = options.outWidth
        options.inTargetDensity = width
        // 主要问题点是BitmapFactory.decodeResource()加载 vector资源文件会失败，需要针对性做兼容处理
        // 不能使用 Resources.getSystem()
        return BitmapFactory.decodeResource(resources, R.drawable.ic_dlam, options)
        // 这个可以加载成功
//        return ContextCompat.getDrawable(context, R.drawable.ic_dlam)!!.toBitmap()
    }

    fun getImageSource(): ImageDecoder.Source? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            return ImageDecoder.createSource(resources, R.drawable.ic_dlam)
        }
        return null
    }
}