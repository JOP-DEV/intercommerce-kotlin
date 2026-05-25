package com.example.intercommerce_kotlin.features.products.data.remote.datasource

import com.example.intercommerce_kotlin.features.products.data.remote.dto.ProductDto
import com.example.intercommerce_kotlin.features.products.data.remote.dto.ProductsResponseDto

interface ProductRemoteDataSource {
    suspend fun getProducts(limit: Int, skip: Int): ProductsResponseDto
    suspend fun searchProducts(query: String): ProductsResponseDto
    suspend fun getProductDetail(id: Int): ProductDto
}
