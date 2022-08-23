package com.example.we_youth.ui

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.LogUtils
import com.example.we_youth.databinding.ActivitySampleBinding
import com.example.we_youth.databinding.ItemArticleBinding
import com.example.we_youth.net.entity.ApiArticle
import com.example.we_youth.utils.UIState
import com.example.we_youth.viewmodel.WanViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class SampleActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    lateinit var binding: ActivitySampleBinding
    val wanViewModel by viewModels<WanViewModel>()
    lateinit var sampleListAdapter: SampleListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySampleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()
    }

    private fun initUI() {

        sampleListAdapter = SampleListAdapter(this)
        binding.recyclerView.adapter = sampleListAdapter

        wanViewModel.getHomeArticle()

        launch(context = Dispatchers.Main) {
            // 注意：repeatOnLifecycle API 仅在 androidx.lifecycle:lifecycle-runtime-ktx:2.4.0 库及更高版本中提供。
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // 处于STARTED状态时会开始收集流，并且在RESUMED状态时保持收集，最终在Fragment进入STOPPED状态时结束收集过程
                wanViewModel.uiState.collect() { state ->
                    when (state) {
                        is UIState.Success -> {
                            Log.e("-->>", "state= ${state.isSuccess}")
                            LogUtils.e("-->>请求成功1=" + state.data)
                            sampleListAdapter.setNewData(state.data.datas)
                        }
                        is UIState.Failure -> {
                            LogUtils.e("-->>请求失败1=$state")

                        }
                        is UIState.Loading -> {
                            LogUtils.e("-->>请求中1=$state")

                        }
                    }
                }
            }
        }

        LogUtils.e("-->>当前屏幕方向=${isScreenPortrait(this)}")
    }


    private fun isScreenPortrait(ctx: Context): Boolean {
        return ctx.resources.configuration.orientation === Configuration.ORIENTATION_PORTRAIT
    }

    override fun onContentChanged() {
        super.onContentChanged()

    }
}


class SampleListAdapter(var context: Activity) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var layoutInflater: LayoutInflater = LayoutInflater.from(context)
    var datas = emptyList<ApiArticle>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var itemArticleBinding = ItemArticleBinding.inflate(layoutInflater)
        return ArticleViewHolder(itemArticleBinding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ArticleViewHolder) {
            holder.refresh(datas[position])
        }
    }

    override fun getItemCount(): Int {
        return datas.size
    }

    fun setNewData(articles: List<ApiArticle>) {
        datas = articles
        notifyDataSetChanged()
    }

    inner class ArticleViewHolder(var binding: ItemArticleBinding) : RecyclerView.ViewHolder(binding.root) {

        fun refresh(article: ApiArticle) {
            binding.tvTitle.text = article.title
        }
    }

}
