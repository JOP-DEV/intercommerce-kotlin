package com.example.intercommerce_kotlin.features.products.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.intercommerce_kotlin.core.result.AppResult
import com.example.intercommerce_kotlin.features.cart.domain.usecase.AddProductToCartUseCase
import com.example.intercommerce_kotlin.features.cart.domain.usecase.ObserveCartUseCase
import com.example.intercommerce_kotlin.features.cart.domain.usecase.RemoveCartItemUseCase
import com.example.intercommerce_kotlin.features.cart.domain.usecase.UpdateCartItemQuantityUseCase
import com.example.intercommerce_kotlin.features.products.domain.usecase.GetProductDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val getProductDetailUseCase: GetProductDetailUseCase,
    private val addProductToCartUseCase: AddProductToCartUseCase,
    private val observeCartUseCase: ObserveCartUseCase,
    private val updateCartItemQuantityUseCase: UpdateCartItemQuantityUseCase,
    private val removeCartItemUseCase: RemoveCartItemUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductDetailUiState())
    val uiState: StateFlow<ProductDetailUiState> = _uiState.asStateFlow()
    private var currentProductId: Int? = null

    init {
        observeCart()
    }

    private fun observeCart() {
        viewModelScope.launch {
            observeCartUseCase().collect { items ->
                val productId = currentProductId ?: return@collect
                val quantity = items.firstOrNull { it.productId == productId }?.quantity ?: 0
                _uiState.update { it.copy(quantityInCart = quantity) }
            }
        }
    }

    fun loadProduct(productId: Int) {
        currentProductId = productId
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = getProductDetailUseCase(productId)) {
                is AppResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            product = result.data,
                            isOffline = false
                        )
                    }
                }

                is AppResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "No pudimos cargar el detalle del producto."
                        )
                    }
                }
            }
        }
    }

    fun selectImage(index: Int) {
        _uiState.update { it.copy(selectedImageIndex = index) }
    }

    fun increaseOrAddToCart() {
        val product = _uiState.value.product ?: return
        if (product.stock <= 0) return
        val currentQuantity = _uiState.value.quantityInCart
        if (currentQuantity >= product.stock) return

        viewModelScope.launch {
            if (currentQuantity <= 0) {
                addProductToCartUseCase(product, 1)
            } else {
                updateCartItemQuantityUseCase(product.id, currentQuantity + 1)
            }
        }
    }

    fun decreaseOrRemoveFromCart() {
        val product = _uiState.value.product ?: return
        val currentQuantity = _uiState.value.quantityInCart
        if (currentQuantity <= 0) return

        viewModelScope.launch {
            if (currentQuantity == 1) {
                removeCartItemUseCase(product.id)
            } else {
                updateCartItemQuantityUseCase(product.id, currentQuantity - 1)
            }
        }
    }

    fun consumeAddedMessage() {
        _uiState.update { it.copy(addedToCartMessage = null) }
    }
}
