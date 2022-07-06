package com.example.we_youth.viewmodel;

import androidx.lifecycle.ViewModel;

import com.example.we_youth.utils.UIState;

import kotlinx.coroutines.flow.MutableStateFlow;
import kotlinx.coroutines.flow.StateFlow;

public class JavaViewModel extends ViewModel {

    // flow api 不能在java中使用
//    private MutableStateFlow<UIState<Integer>> _uiState =  MutableStateFlow(UIState.Companion.loading());

    // The UI collects from this StateFlow to get its state updates
//    public StateFlow<UIState<Integer>> uiState = _uiState;
}
