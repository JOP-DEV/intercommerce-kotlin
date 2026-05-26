package com.example.intercommerce_kotlin.features.cart.presentation

import com.example.intercommerce_kotlin.features.cart.domain.model.CartItem
import com.example.intercommerce_kotlin.features.cart.domain.model.CartSummary

data class CartUiState(
    val items: List<CartItem> = emptyList(),
    val summary: CartSummary = CartSummary(
        subtotal = 0.0,
        discount = 0.0,
        tax = 0.0,
        total = 0.0,
        itemsCount = 0
    ),
    val isLoading: Boolean = true,
    val isEmpty: Boolean = false
)
