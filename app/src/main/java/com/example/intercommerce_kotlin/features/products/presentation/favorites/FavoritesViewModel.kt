package com.example.intercommerce_kotlin.features.products.presentation.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.intercommerce_kotlin.features.cart.domain.usecase.AddProductToCartUseCase
import com.example.intercommerce_kotlin.features.cart.domain.usecase.ObserveCartUseCase
import com.example.intercommerce_kotlin.features.cart.domain.usecase.RemoveCartItemUseCase
import com.example.intercommerce_kotlin.features.cart.domain.usecase.UpdateCartItemQuantityUseCase
import com.example.intercommerce_kotlin.features.products.domain.usecase.ObserveFavoriteProductsUseCase
import com.example.intercommerce_kotlin.features.products.domain.usecase.UpdateProductFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val observeFavoriteProductsUseCase: ObserveFavoriteProductsUseCase,
    private val updateProductFavoriteUseCase: UpdateProductFavoriteUseCase,
    private val observeCartUseCase: ObserveCartUseCase,
    private val addProductToCartUseCase: AddProductToCartUseCase,
    private val updateCartItemQuantityUseCase: UpdateCartItemQuantityUseCase,
    private val removeCartItemUseCase: RemoveCartItemUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    init {
        observeFavorites()
        observeCart()
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            observeFavoriteProductsUseCase().collect { products ->
                _uiState.update { it.copy(products = products) }
            }
        }
    }

    private fun observeCart() {
        viewModelScope.launch {
            observeCartUseCase().collect { items ->
                _uiState.update {
                    it.copy(
                        cartItemsCount = items.sumOf { item -> item.quantity },
                        cartQuantities = items.associate { item -> item.productId to item.quantity }
                    )
                }
            }
        }
    }

    fun onFavoriteToggle(productId: Int) {
        viewModelScope.launch {
            updateProductFavoriteUseCase(productId = productId, isFavorite = false)
        }
    }

    fun increaseProductQuantity(productId: Int) {
        val current = _uiState.value.cartQuantities[productId] ?: 0
        val product = _uiState.value.products.firstOrNull { it.id == productId } ?: return
        if (current >= product.stock) return
        if (current == 0) {
            addProductToCart(product, 1)
            return
        }
        viewModelScope.launch {
            updateCartItemQuantityUseCase(productId = productId, quantity = current + 1)
        }
    }

    fun decreaseOrRemoveProduct(productId: Int) {
        val current = _uiState.value.cartQuantities[productId] ?: 0
        if (current <= 1) {
            viewModelScope.launch {
                removeCartItemUseCase(productId)
            }
            return
        }
        viewModelScope.launch {
            updateCartItemQuantityUseCase(productId = productId, quantity = current - 1)
        }
    }

    private fun addProductToCart(product: com.example.intercommerce_kotlin.features.products.domain.model.Product, quantity: Int) {
        viewModelScope.launch {
            addProductToCartUseCase(product, quantity)
        }
    }
}
