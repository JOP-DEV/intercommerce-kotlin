package com.example.intercommerce_kotlin.features.products.domain.usecase

import com.example.intercommerce_kotlin.core.network.NetworkConstants
import com.example.intercommerce_kotlin.features.products.domain.repository.ProductRepository
import javax.inject.Inject

class GetProductsUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    suspend operator fun invoke(page: Int) = repository.getProducts(page, NetworkConstants.PAGE_LIMIT)
}
