package com.example.intercommerce_kotlin.features.products.presentation.detail

import com.example.intercommerce_kotlin.features.products.domain.model.Product

data class ProductDetailUiState(
    val isLoading: Boolean = true,
    val product: Product? = null,
    val selectedImageIndex: Int = 0,
    val quantity: Int = 1,
    val isOffline: Boolean = false,
    val errorMessage: String? = null,
    val addedToCartMessage: String? = null
)
