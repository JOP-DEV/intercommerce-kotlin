package com.example.intercommerce_kotlin.features.cart.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.intercommerce_kotlin.features.cart.domain.model.CartItem
import com.example.intercommerce_kotlin.features.cart.domain.usecase.CalculateCartSummaryUseCase
import com.example.intercommerce_kotlin.features.cart.domain.usecase.ObserveCartUseCase
import com.example.intercommerce_kotlin.features.cart.domain.usecase.RemoveCartItemUseCase
import com.example.intercommerce_kotlin.features.cart.domain.usecase.UpdateCartItemQuantityUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class CartViewModel @Inject constructor(
    private val observeCartUseCase: ObserveCartUseCase,
    private val updateCartItemQuantityUseCase: UpdateCartItemQuantityUseCase,
    private val removeCartItemUseCase: RemoveCartItemUseCase,
    private val calculateCartSummaryUseCase: CalculateCartSummaryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            observeCartUseCase().collect { items ->
                updateState(items)
            }
        }
    }

    fun increaseQuantity(item: CartItem) {
        viewModelScope.launch {
            updateCartItemQuantityUseCase(item.productId, item.quantity + 1)
        }
    }

    fun decreaseQuantity(item: CartItem) {
        viewModelScope.launch {
            updateCartItemQuantityUseCase(item.productId, item.quantity - 1)
        }
    }

    fun removeItem(item: CartItem) {
        viewModelScope.launch {
            removeCartItemUseCase(item.productId)
        }
    }

    private fun updateState(items: List<CartItem>) {
        _uiState.update {
            it.copy(
                items = items,
                summary = calculateCartSummaryUseCase(items),
                isLoading = false,
                isEmpty = items.isEmpty()
            )
        }
    }
}
