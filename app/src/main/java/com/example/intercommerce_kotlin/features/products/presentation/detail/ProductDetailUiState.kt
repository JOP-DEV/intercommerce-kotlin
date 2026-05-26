package com.example.intercommerce_kotlin.features.products.presentation.detail

import androidx.annotation.StringRes
import com.example.intercommerce_kotlin.features.products.domain.model.Product

data class ProductDetailUiState(
    val isLoading: Boolean = true,
    val product: Product? = null,
    val selectedImageIndex: Int = 0,
    val quantityInCart: Int = 0,
    val isOffline: Boolean = false,
    @StringRes val errorMessageRes: Int? = null,
    val addedToCartMessage: String? = null
)
