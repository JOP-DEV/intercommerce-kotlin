package com.example.intercommerce_kotlin.features.products.domain.usecase

import com.example.intercommerce_kotlin.features.products.domain.repository.ProductRepository
import javax.inject.Inject

class SearchProductsUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    suspend operator fun invoke(query: String) = repository.searchProducts(query)
}
