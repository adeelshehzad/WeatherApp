package com.example.weathercompose.data.model

sealed class UIState {
    data object Loading : UIState()
    data object Success : UIState()
    data class Error(val throwable: Throwable) : UIState()
}