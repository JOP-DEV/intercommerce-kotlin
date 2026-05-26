package com.example.intercommerce_kotlin.features.products.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.intercommerce_kotlin.core.result.AppResult
import com.example.intercommerce_kotlin.features.cart.domain.usecase.AddProductToCartUseCase
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
    private val addProductToCartUseCase: AddProductToCartUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductDetailUiState())
    val uiState: StateFlow<ProductDetailUiState> = _uiState.asStateFlow()

    fun loadProduct(productId: Int) {
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

    fun increaseQuantity() {
        _uiState.update { state ->
            val stock = state.product?.stock ?: 0
            val maxQuantity = stock.coerceAtLeast(1)
            state.copy(quantity = (state.quantity + 1).coerceAtMost(maxQuantity))
        }
    }

    fun decreaseQuantity() {
        _uiState.update { state -> state.copy(quantity = (state.quantity - 1).coerceAtLeast(1)) }
    }

    fun addToCart() {
        val product = _uiState.value.product ?: return
        if (product.stock <= 0) return
        val quantity = _uiState.value.quantity

        viewModelScope.launch {
            addProductToCartUseCase(product, quantity)
            _uiState.update { it.copy(addedToCartMessage = "Producto agregado al carrito") }
        }
    }

    fun consumeAddedMessage() {
        _uiState.update { it.copy(addedToCartMessage = null) }
    }
}
