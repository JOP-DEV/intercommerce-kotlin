package com.example.intercommerce_kotlin.features.products.data.local.datasource

import com.example.intercommerce_kotlin.features.products.data.local.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

interface ProductLocalDataSource {
    suspend fun upsertProducts(products: List<ProductEntity>)
    suspend fun getPagedProducts(limit: Int, offset: Int): List<ProductEntity>
    suspend fun searchProducts(query: String): List<ProductEntity>
    suspend fun getProductById(id: Int): ProductEntity?
    suspend fun countProducts(): Int
    suspend fun updateFavorite(productId: Int, isFavorite: Boolean)
    suspend fun getFavoriteStatus(productId: Int): Boolean?
    fun observeFavoriteProducts(): Flow<List<ProductEntity>>
}
