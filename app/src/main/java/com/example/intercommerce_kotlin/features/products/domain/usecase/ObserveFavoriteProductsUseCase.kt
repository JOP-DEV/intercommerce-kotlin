package com.example.intercommerce_kotlin.features.products.domain.usecase

import com.example.intercommerce_kotlin.features.products.domain.model.Product
import com.example.intercommerce_kotlin.features.products.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveFavoriteProductsUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    operator fun invoke(): Flow<List<Product>> = repository.observeFavoriteProducts()
}
