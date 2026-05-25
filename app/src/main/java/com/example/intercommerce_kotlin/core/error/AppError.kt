package com.example.intercommerce_kotlin.core.error

sealed interface AppError {
    data object NetworkUnavailable : AppError
    data object Timeout : AppError
    data object NotFound : AppError
    data object Server : AppError
    data object Serialization : AppError
    data object LocalDatabase : AppError
    data class Unknown(val cause: Throwable? = null) : AppError
}
