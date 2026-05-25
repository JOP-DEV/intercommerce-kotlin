package com.example.intercommerce_kotlin.core.result

import com.example.intercommerce_kotlin.core.error.AppError

sealed interface AppResult<out T> {
    data class Success<T>(val data: T) : AppResult<T>
    data class Error(val error: AppError) : AppResult<Nothing>
}
