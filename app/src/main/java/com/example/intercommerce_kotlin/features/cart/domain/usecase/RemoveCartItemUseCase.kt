package com.example.intercommerce_kotlin.features.cart.domain.usecase

import com.example.intercommerce_kotlin.features.cart.domain.repository.CartRepository
import javax.inject.Inject

class RemoveCartItemUseCase @Inject constructor(
    private val repository: CartRepository
) {
    suspend operator fun invoke(productId: Int) {
        repository.removeProduct(productId)
    }
}
