package com.example.we_youth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.LogUtils
import com.example.we_youth.data.LocalDataSource
import com.example.we_youth.utils.UIState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NetViewModel : ViewModel() {
    val sharedFlow = MutableSharedFlow<Int>(replay = 1, extraBufferCapacity = 0, onBufferOverflow = BufferOverflow.SUSPEND)

    // UIState是loading时 值不重要
    private val _uiState: MutableStateFlow<UIState<Int>> = MutableStateFlow<UIState<Int>>(UIState.loading())

    // The UI collects from this StateFlow to get its state updates
    val uiState: StateFlow<UIState<Int>> = _uiState


    fun requestData() {
        Result
        viewModelScope.launch(context = Dispatchers.IO) {
            val randoms = (0..2).random()
            when (randoms) {
                0 -> {
                    var success = UIState.success(0)
                    _uiState.value = success

                }
                1 -> {
                    _uiState.value = UIState.failure(-1, "错误状态")
                }
                2 -> {
                    _uiState.value = UIState.loading()
                }
            }
            LogUtils.e("-->>requestData ${_uiState.value}")
        }
    }

    fun getCallbackFlow(): Flow<Int> {
        return LocalDataSource.getCallbackFlow()
    }
}