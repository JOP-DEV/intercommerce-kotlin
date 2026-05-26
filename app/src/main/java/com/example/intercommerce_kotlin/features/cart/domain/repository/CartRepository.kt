package com.example.intercommerce_kotlin.features.cart.domain.repository

import com.example.intercommerce_kotlin.features.cart.domain.model.CartItem
import com.example.intercommerce_kotlin.features.products.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface CartRepository {
    suspend fun addProduct(product: Product, quantity: Int)
    suspend fun updateQuantity(productId: Int, quantity: Int)
    suspend fun removeProduct(productId: Int)
    suspend fun clearCart()
    fun observeCart(): Flow<List<CartItem>>
}
