package com.example.intercommerce_kotlin.features.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.intercommerce_kotlin.features.cart.domain.usecase.ObserveCartUseCase
import com.example.intercommerce_kotlin.features.products.domain.usecase.ObserveFavoriteProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val observeCartUseCase: ObserveCartUseCase,
    private val observeFavoriteProductsUseCase: ObserveFavoriteProductsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            observeCartUseCase().collect { items ->
                _uiState.update {
                    it.copy(
                        cartCount = items.sumOf { item -> item.quantity },
                        ordersCount = 0
                    )
                }
            }
        }

        viewModelScope.launch {
            observeFavoriteProductsUseCase().collect { products ->
                _uiState.update { it.copy(favoritesCount = products.size) }
            }
        }
    }
}

data class ProfileUiState(
    val cartCount: Int = 0,
    val favoritesCount: Int = 0,
    val ordersCount: Int = 0
)
