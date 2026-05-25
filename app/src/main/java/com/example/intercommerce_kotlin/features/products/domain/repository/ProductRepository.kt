package com.example.intercommerce_kotlin.features.products.domain.repository

import com.example.intercommerce_kotlin.core.result.AppResult
import com.example.intercommerce_kotlin.features.products.domain.model.PagedProducts
import com.example.intercommerce_kotlin.features.products.domain.model.Product

interface ProductRepository {
    suspend fun getProducts(page: Int, limit: Int): AppResult<PagedProducts>
    suspend fun searchProducts(query: String): AppResult<List<Product>>
    suspend fun getProductDetail(id: Int): AppResult<Product>
}
