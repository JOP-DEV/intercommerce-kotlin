package com.example.intercommerce_kotlin.features.cart.domain.repository

import com.example.intercommerce_kotlin.features.cart.domain.model.CartItem
import com.example.intercommerce_kotlin.features.products.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface CartRepository {
    suspend fun addProduct(product: Product, quantity: Int)
    fun observeCart(): Flow<List<CartItem>>
}
