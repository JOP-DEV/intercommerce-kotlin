package com.example.intercommerce_kotlin.features.cart.domain.model

data class CartItem(
    val productId: Int,
    val title: String,
    val price: Double,
    val thumbnail: String,
    val quantity: Int,
    val discountPercentage: Double
)
