package com.example.intercommerce_kotlin.features.cart.domain.usecase

import com.example.intercommerce_kotlin.features.cart.domain.repository.CartRepository
import javax.inject.Inject

class ObserveCartUseCase @Inject constructor(
    private val repository: CartRepository
) {
    operator fun invoke() = repository.observeCart()
}
