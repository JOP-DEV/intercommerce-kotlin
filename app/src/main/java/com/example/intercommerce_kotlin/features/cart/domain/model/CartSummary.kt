package com.example.intercommerce_kotlin.features.cart.domain.model

data class CartSummary(
    val subtotal: Double,
    val discount: Double,
    val tax: Double,
    val total: Double,
    val itemsCount: Int
)
