package com.example.we_youth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.LogUtils
import com.example.we_youth.data.RemoteDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WanViewModel : ViewModel() {

    fun getHomeArticle() {
        viewModelScope.launch(context = Dispatchers.Default) {
            var homeArticle = RemoteDataSource.wanApi.getHomeArticle(0)
            LogUtils.e("-->>$homeArticle")
        }
    }
}