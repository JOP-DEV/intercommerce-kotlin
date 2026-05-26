package com.example.intercommerce_kotlin.features.cart.data.local.datasource

import com.example.intercommerce_kotlin.features.cart.data.local.entity.CartItemEntity
import kotlinx.coroutines.flow.Flow

interface CartLocalDataSource {
    suspend fun upsert(item: CartItemEntity)
    suspend fun getByProductId(productId: Int): CartItemEntity?
    suspend fun updateQuantity(productId: Int, quantity: Int)
    suspend fun removeByProductId(productId: Int)
    suspend fun clear()
    fun observeCartItems(): Flow<List<CartItemEntity>>
}
