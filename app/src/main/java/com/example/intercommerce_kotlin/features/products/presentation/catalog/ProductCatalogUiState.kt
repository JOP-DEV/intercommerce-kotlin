package com.example.intercommerce_kotlin.features.products.presentation.catalog

import androidx.annotation.StringRes
import com.example.intercommerce_kotlin.features.products.domain.model.Product

data class ProductCatalogUiState(
    val products: List<Product> = emptyList(),
    val cartItemsCount: Int = 0,
    val cartQuantities: Map<Int, Int> = emptyMap(),
    val query: String = "",
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val isOffline: Boolean = false,
    val endReached: Boolean = false,
    @StringRes val errorMessageRes: Int? = null
)
