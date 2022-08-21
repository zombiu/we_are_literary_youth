package com.example.we_youth.ui

import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.ScreenUtils
import com.example.we_youth.R
import com.example.we_youth.databinding.ActivityKeyboardBinding
import com.example.we_youth.databinding.ItemMessageSampleBinding
import com.example.we_youth.widget.KeyboardStatePopupWindow
import com.example.we_youth.widget.TranslationAnimUtils
import java.util.*

class KeyboardActivity : AppCompatActivity() {
    lateinit var binding: ActivityKeyboardBinding
    lateinit var messageAdapter: MessageAdapter
    var handle = Handler()

    /*val translateAnimation by lazy(LazyThreadSafetyMode.NONE) {
        TranslationAnimUtils(binding.lvMessage)
    }*/
    lateinit var translateAnimation: TranslationAnimUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKeyboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
    }

    private fun initView() {

        translateAnimation = TranslationAnimUtils(binding.lvMessage)
        messageAdapter = MessageAdapter(this)
        binding.lvMessage.adapter = messageAdapter

        var keyboardStatePopupWindow = KeyboardStatePopupWindow(this, window.decorView)
        keyboardStatePopupWindow.setOnKeyboardStateListener(object : KeyboardStatePopupWindow.OnKeyboardStateListener {
            override fun onOpened(keyboardHeight: Int) {
                binding.etInput.translationY = -keyboardHeight.toFloat()
                transListView(keyboardHeight)
                binding.lvMessage
                binding.lvMessage.setSelection(ListView.FOCUS_DOWN);  //刷新到底部
            }

            override fun onClosed() {
                binding.etInput.translationY = 0f
                translateAnimation.getTransAnimatoion(0f).start()
            }

        })

        binding.lvMessage.onItemLongClickListener = object : AdapterView.OnItemLongClickListener {
            override fun onItemLongClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long): Boolean {
                messageAdapter.datas.removeAt(position)
                messageAdapter.notifyDataSetChanged()
                handle.postDelayed({
                    transListView(keyboardStatePopupWindow.keyboardHeight)
                }, 50)
                return true
            }

        }
    }

    private var lastChildXY = IntArray(2)
    private var lastTransDiff = 0

    private fun transListView(keyboardHeight: Int) {
        /*binding.lvMessage.apply {
            var visibleCount = lastVisiblePosition - firstVisiblePosition
            if (messageAdapter.count >= visibleCount) {
                // todo 后期改成动画
//                translationY = keyboardHeight.toFloat()
//                getTransAnimatoion(binding.lvMessage.translationY, keyboardHeight.toFloat())
                translateAnimation.getTransAnimatoion(keyboardHeight.toFloat()).start()
            } else {
                var oldTranslationY = translationY
                var lastChild = getChildAt(lastVisiblePosition)
                lastChild.getLocationOnScreen(lastChildXY)

                *//*var lastChildBottomY = lastChildXY[1] + lastChild.height - oldTranslationY

                var useEdgeY = ScreenUtils.getScreenHeight() - (keyboardHeight + binding.etInput.height)
                var diff = lastChildBottomY - useEdgeY*//*
                // 这里需要计算出 去除偏移之后 最后一个child 底部y的坐标
                var lastChildBottomY = lastChildXY[1] + lastChild.height - oldTranslationY
                // 输入法+底部输入框 弹出后边界的Y坐标
                var useEdgeY = ScreenUtils.getScreenHeight() - (keyboardHeight + binding.etInput.height)
                // 这里需要计算出 去除偏移之后 应该移动的距离了
                var diff = useEdgeY - lastChildBottomY
                if (diff < 0) {
                    // todo 改成动画的形式
//                    translationY = diff
//                    getTransAnimatoion(oldTranslationY, diff).start()
                    translateAnimation.getTransAnimatoion(diff).start()
                }
            }
        }*/
        var lvMessage = binding.lvMessage

        var visibleCount = lvMessage.lastVisiblePosition - lvMessage.firstVisiblePosition + 1
        if (messageAdapter.count > visibleCount) {
            // todo 后期改成动画
//                translationY = keyboardHeight.toFloat()
//                getTransAnimatoion(binding.lvMessage.translationY, keyboardHeight.toFloat())
            translateAnimation.getTransAnimatoion(-keyboardHeight.toFloat()).start()
        } else {
            var oldTranslationY = lvMessage.translationY
            var lastChild = lvMessage.getChildAt(lvMessage.lastVisiblePosition)
            lastChild.getLocationOnScreen(lastChildXY)

            /*var lastChildBottomY = lastChildXY[1] + lastChild.height - oldTranslationY

            var useEdgeY = ScreenUtils.getScreenHeight() - (keyboardHeight + binding.etInput.height)
            var diff = lastChildBottomY - useEdgeY*/
            // 这里需要计算出 去除偏移之后 最后一个child 底部y的坐标
            var lastChildBottomY = lastChildXY[1] + lastChild.height - oldTranslationY
            // 输入法+底部输入框 弹出后边界的Y坐标
            var useEdgeY = ScreenUtils.getScreenHeight() - (keyboardHeight + binding.etInput.height)
            // 这里需要计算出 去除偏移之后 应该移动的距离了
            var diff = lastChildBottomY - useEdgeY
            if (diff > 0) {
                // todo 改成动画的形式
//                    translationY = diff
//                    getTransAnimatoion(oldTranslationY, diff).start()
                translateAnimation.getTransAnimatoion(-diff).start()
            }
        }

    }

    /*private fun  getUseSpce() : Int {
        return
    }*/

    /*private fun getTransAnimatoion(fromValue: Float, toValue: Float): ObjectAnimator {
        return ObjectAnimator.ofFloat(
            binding.lvMessage,
            "translationY",
            fromValue,
            toValue
        )
    }*/
}

class MessageAdapter : BaseAdapter {

    constructor(context: Context) : super()


    var datas = getDatas()
    override fun getCount(): Int {
        return datas.size
    }

    override fun getItem(position: Int): Any {
        return datas.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        if (convertView == null) {
            var itemView = View.inflate(parent.context, R.layout.item_message_sample, null)
            var itemBinding = ItemMessageSampleBinding.bind(itemView)
            var viewHolder = ViewHolder(itemBinding)

            viewHolder.refresh(position)
            itemView.tag = viewHolder

            return itemView
        } else {
            var tag = convertView.tag
            if (tag is ViewHolder) {
                tag.refresh(position)
            }
        }
        return convertView
    }

    inner class ViewHolder(var itemBinding: ItemMessageSampleBinding) {
        fun refresh(pos: Int) {
            var get = datas.get(pos)
            itemBinding.tvTitle.text = "标题=$get"
        }
    }

}

fun getDatas(): MutableList<Int> {
    var datas = ArrayList<Int>()
    for (i in 0 until 12) {
        datas.add(i)
    }
    return datas
}