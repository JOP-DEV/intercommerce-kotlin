package com.example.intercommerce_kotlin.features.products.data.remote.service

import com.example.intercommerce_kotlin.features.products.data.remote.dto.ProductDto
import com.example.intercommerce_kotlin.features.products.data.remote.dto.ProductsResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductApi {
    @GET("products")
    suspend fun getProducts(
        @Query("limit") limit: Int,
        @Query("skip") skip: Int
    ): ProductsResponseDto

    @GET("products/search")
    suspend fun searchProducts(
        @Query("q") query: String
    ): ProductsResponseDto

    @GET("products/{id}")
    suspend fun getProductDetail(@Path("id") id: Int): ProductDto
}
