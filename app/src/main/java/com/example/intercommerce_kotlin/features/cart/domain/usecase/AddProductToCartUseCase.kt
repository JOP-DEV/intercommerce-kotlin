package com.example.intercommerce_kotlin.features.cart.domain.usecase

import com.example.intercommerce_kotlin.features.cart.domain.repository.CartRepository
import com.example.intercommerce_kotlin.features.products.domain.model.Product
import javax.inject.Inject

class AddProductToCartUseCase @Inject constructor(
    private val repository: CartRepository
) {
    suspend operator fun invoke(product: Product, quantity: Int) {
        repository.addProduct(product, quantity)
    }
}
