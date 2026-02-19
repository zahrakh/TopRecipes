package com.food.toprecipes.presentation.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Base ViewModel with UiState and UiEffect pattern.
 *
 * @param UiState The state class that represents the UI state
 * @param UiEffect The sealed class/interface that represents one-time UI effects (e.g., navigation, showing snackbars)
 */
abstract class BaseViewModel<UiState, UiEffect> : ViewModel() {

    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(initialState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _uiEffect: MutableSharedFlow<UiEffect> = MutableSharedFlow()
    val uiEffect: SharedFlow<UiEffect> = _uiEffect.asSharedFlow()

    /**
     * Returns the initial state for the ViewModel
     */
    protected abstract fun initialState(): UiState

    /**
     * Updates the current UI state
     */
    protected fun setState(update: UiState.() -> UiState) {
        _uiState.value = _uiState.value.update()
    }

    /**
     * Emits a one-time UI effect
     */
    protected fun setEffect(effect: UiEffect) {
        viewModelScope.launch {
            _uiEffect.emit(effect)
        }
    }
}
