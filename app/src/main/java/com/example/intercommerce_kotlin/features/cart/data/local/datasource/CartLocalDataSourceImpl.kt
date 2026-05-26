package com.example.intercommerce_kotlin.features.cart.data.local.datasource

import com.example.intercommerce_kotlin.features.cart.data.local.dao.CartDao
import com.example.intercommerce_kotlin.features.cart.data.local.entity.CartItemEntity
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class CartLocalDataSourceImpl @Inject constructor(
    private val dao: CartDao
) : CartLocalDataSource {
    override suspend fun upsert(item: CartItemEntity) = dao.upsert(item)

    override suspend fun getByProductId(productId: Int): CartItemEntity? = dao.getByProductId(productId)

    override suspend fun updateQuantity(productId: Int, quantity: Int) {
        dao.updateQuantity(productId = productId, quantity = quantity, updatedAt = System.currentTimeMillis())
    }

    override suspend fun removeByProductId(productId: Int) = dao.removeByProductId(productId)

    override suspend fun clear() = dao.clear()

    override fun observeCartItems(): Flow<List<CartItemEntity>> = dao.observeCartItems()
}
