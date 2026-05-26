package com.example.intercommerce_kotlin.features.cart.data.repository

import com.example.intercommerce_kotlin.core.dispatchers.IoDispatcher
import com.example.intercommerce_kotlin.features.cart.data.local.datasource.CartLocalDataSource
import com.example.intercommerce_kotlin.features.cart.data.local.entity.CartItemEntity
import com.example.intercommerce_kotlin.features.cart.data.mapper.toDomain
import com.example.intercommerce_kotlin.features.cart.domain.model.CartItem
import com.example.intercommerce_kotlin.features.cart.domain.repository.CartRepository
import com.example.intercommerce_kotlin.features.products.domain.model.Product
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class CartRepositoryImpl @Inject constructor(
    private val localDataSource: CartLocalDataSource,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : CartRepository {

    override suspend fun addProduct(product: Product, quantity: Int) = withContext(ioDispatcher) {
        val existing = localDataSource.getByProductId(product.id)
        val now = System.currentTimeMillis()
        val nextQuantity = (existing?.quantity ?: 0) + quantity

        localDataSource.upsert(
            CartItemEntity(
                productId = product.id,
                title = product.title,
                price = product.price,
                thumbnail = product.thumbnail,
                quantity = nextQuantity,
                discountPercentage = product.discountPercentage,
                createdAt = existing?.createdAt ?: now,
                updatedAt = now
            )
        )
    }

    override fun observeCart(): Flow<List<CartItem>> =
        localDataSource.observeCartItems().map { list -> list.map { it.toDomain() } }
}
