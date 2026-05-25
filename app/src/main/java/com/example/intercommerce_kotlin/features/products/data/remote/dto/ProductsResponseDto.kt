package com.example.intercommerce_kotlin.features.products.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProductsResponseDto(
    val products: List<ProductDto>,
    val total: Int,
    val skip: Int,
    val limit: Int
)
