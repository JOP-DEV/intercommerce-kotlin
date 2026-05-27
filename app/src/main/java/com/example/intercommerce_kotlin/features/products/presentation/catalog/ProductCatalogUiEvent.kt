package com.example.intercommerce_kotlin.features.products.presentation.catalog

sealed interface ProductCatalogUiEvent {
    data object Retry : ProductCatalogUiEvent
    data class QueryChanged(val query: String) : ProductCatalogUiEvent
}
