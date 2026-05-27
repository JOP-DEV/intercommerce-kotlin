package com.example.intercommerce_kotlin.features.products.presentation.favorites

import com.example.intercommerce_kotlin.features.products.domain.model.Product

data class FavoritesUiState(
    val products: List<Product> = emptyList(),
    val cartItemsCount: Int = 0,
    val cartQuantities: Map<Int, Int> = emptyMap()
)
