package com.example.intercommerce_kotlin.core.network

import kotlinx.coroutines.flow.Flow

enum class ConnectionStatus {
    Available,
    Unavailable
}

interface ConnectivityObserver {
    val status: Flow<ConnectionStatus>
    fun isConnected(): Boolean
}
