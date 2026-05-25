package com.example.intercommerce_kotlin.features.products.data.remote.datasource

import com.example.intercommerce_kotlin.features.products.data.remote.dto.ProductDto
import com.example.intercommerce_kotlin.features.products.data.remote.dto.ProductsResponseDto
import com.example.intercommerce_kotlin.features.products.data.remote.service.ProductApi
import javax.inject.Inject

class ProductRemoteDataSourceImpl @Inject constructor(
    private val api: ProductApi
) : ProductRemoteDataSource {
    override suspend fun getProducts(limit: Int, skip: Int): ProductsResponseDto = api.getProducts(limit, skip)

    override suspend fun searchProducts(query: String): ProductsResponseDto = api.searchProducts(query)

    override suspend fun getProductDetail(id: Int): ProductDto = api.getProductDetail(id)
}
