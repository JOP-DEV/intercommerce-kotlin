package com.example.intercommerce_kotlin.core.presentation

import androidx.annotation.StringRes

sealed interface AppConnectivityUiEffect {
    data class ShowToast(@StringRes val messageRes: Int) : AppConnectivityUiEffect
}
