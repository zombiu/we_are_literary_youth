package com.example.we_youth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.LogUtils
import com.example.we_youth.data.RemoteDataSource
import com.example.we_youth.utils.UIState
import com.example.we_youth.net.entity.ApiArticle
import com.example.we_youth.net.entity.ApiPage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WanViewModel : ViewModel() {
    // UIState是loading时 值不重要
    private val _uiState: MutableStateFlow<UIState<ApiPage<ApiArticle>>> = MutableStateFlow<UIState<ApiPage<ApiArticle>>>(UIState.loading())

    // The UI collects from this StateFlow to get its state updates
    val uiState: StateFlow<UIState<ApiPage<ApiArticle>>> = _uiState.asStateFlow()

    fun getHomeArticle() {
        _uiState.value = UIState.loading()
        viewModelScope.launch(context = Dispatchers.Default) {
            var homeArticle = RemoteDataSource.wanApi.getHomeArticle(0)
            LogUtils.e("-->>$homeArticle")
//            _uiState.value = homeArticle
        }
    }

    fun getHomeArticleFullResult() {
        _uiState.value = UIState.loading()
        viewModelScope.launch(context = Dispatchers.Default) {
            var homeArticle = RemoteDataSource.wanApi.getHomeArticle2(0)
            LogUtils.e("-->>$homeArticle")
            _uiState.value = UIState.success(homeArticle.data)
        }
    }
}