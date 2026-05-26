package com.example.intercommerce_kotlin.features.cart.domain.usecase

import com.example.intercommerce_kotlin.features.cart.domain.repository.CartRepository
import javax.inject.Inject

class UpdateCartItemQuantityUseCase @Inject constructor(
    private val repository: CartRepository
) {
    suspend operator fun invoke(productId: Int, quantity: Int) {
        repository.updateQuantity(productId = productId, quantity = quantity)
    }
}
