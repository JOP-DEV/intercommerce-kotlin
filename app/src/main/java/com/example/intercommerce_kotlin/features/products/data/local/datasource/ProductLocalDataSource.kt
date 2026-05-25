package com.example.intercommerce_kotlin.features.products.data.local.datasource

import com.example.intercommerce_kotlin.features.products.data.local.entity.ProductEntity

interface ProductLocalDataSource {
    suspend fun upsertProducts(products: List<ProductEntity>)
    suspend fun getPagedProducts(limit: Int, offset: Int): List<ProductEntity>
    suspend fun searchProducts(query: String): List<ProductEntity>
    suspend fun getProductById(id: Int): ProductEntity?
    suspend fun countProducts(): Int
}
