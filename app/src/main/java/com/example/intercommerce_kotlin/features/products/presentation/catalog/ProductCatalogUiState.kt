package com.example.intercommerce_kotlin.features.products.presentation.catalog

import com.example.intercommerce_kotlin.features.products.domain.model.Product

data class ProductCatalogUiState(
    val products: List<Product> = emptyList(),
    val query: String = "",
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val isOffline: Boolean = false,
    val endReached: Boolean = false,
    val errorMessage: String? = null
)
