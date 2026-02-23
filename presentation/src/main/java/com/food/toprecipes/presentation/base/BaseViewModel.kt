package com.food.toprecipes.presentation.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Base ViewModel with UiState and UiEffect pattern for state management
abstract class BaseViewModel<UiState, UiEffect> : ViewModel() {

    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(initialState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _uiEffect: MutableSharedFlow<UiEffect> = MutableSharedFlow()//todo consider to change it to Channel
    val uiEffect: SharedFlow<UiEffect> = _uiEffect.asSharedFlow()

    protected abstract fun initialState(): UiState

    protected fun setState(update: UiState.() -> UiState) { //Function Type with Receiver
        //.update is Atomic which makes it Thread_safee
        _uiState.update { currentState->
            currentState.update()
        }
    }

    protected fun setEffect(effect: UiEffect) {
        viewModelScope.launch {
            _uiEffect.emit(effect)
        }
    }
}
