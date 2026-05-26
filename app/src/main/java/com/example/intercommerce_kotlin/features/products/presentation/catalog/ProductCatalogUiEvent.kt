package com.example.intercommerce_kotlin.features.products.presentation.catalog

sealed interface ProductCatalogUiEvent {
    data object LoadInitial : ProductCatalogUiEvent
    data object LoadMore : ProductCatalogUiEvent
    data object Retry : ProductCatalogUiEvent
    data class QueryChanged(val query: String) : ProductCatalogUiEvent
}
