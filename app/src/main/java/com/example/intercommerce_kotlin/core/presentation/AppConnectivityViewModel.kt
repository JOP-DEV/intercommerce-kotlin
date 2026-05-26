package com.example.intercommerce_kotlin.core.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.intercommerce_kotlin.R
import com.example.intercommerce_kotlin.core.network.ConnectionStatus
import com.example.intercommerce_kotlin.core.network.ConnectivityObserver
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@HiltViewModel
class AppConnectivityViewModel @Inject constructor(
    private val connectivityObserver: ConnectivityObserver
) : ViewModel() {

    private val _uiEffect = MutableSharedFlow<AppConnectivityUiEffect>()
    val uiEffect: SharedFlow<AppConnectivityUiEffect> = _uiEffect.asSharedFlow()

    private var previousStatus: ConnectionStatus? = null

    init {
        observeConnectivity()
    }

    private fun observeConnectivity() {
        viewModelScope.launch {
            connectivityObserver.status
                .distinctUntilChanged()
                .collect { status ->
                    val previous = previousStatus
                    previousStatus = status

                    if (previous == null) return@collect

                    if (previous == ConnectionStatus.Available && status == ConnectionStatus.Unavailable) {
                        _uiEffect.emit(AppConnectivityUiEffect.ShowToast(R.string.offline_global))
                    }

                    if (previous == ConnectionStatus.Unavailable && status == ConnectionStatus.Available) {
                        _uiEffect.emit(AppConnectivityUiEffect.ShowToast(R.string.connection_restored))
                    }
                }
        }
    }
}
