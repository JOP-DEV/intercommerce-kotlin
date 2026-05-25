package com.example.intercommerce_kotlin.features.products.domain.model

data class PagedProducts(
    val items: List<Product>,
    val isOffline: Boolean,
    val endReached: Boolean
)
