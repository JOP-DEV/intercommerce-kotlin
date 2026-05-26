package com.example.intercommerce_kotlin.features.cart.domain.usecase

import com.example.intercommerce_kotlin.features.cart.domain.model.CartItem
import com.example.intercommerce_kotlin.features.cart.domain.model.CartSummary
import javax.inject.Inject

class CalculateCartSummaryUseCase @Inject constructor() {
    private val taxRate = 0.19

    operator fun invoke(items: List<CartItem>): CartSummary {
        val subtotal = items.sumOf { it.price * it.quantity }
        val discount = items.sumOf { (it.price * it.quantity) * (it.discountPercentage / 100.0) }
        val taxable = (subtotal - discount).coerceAtLeast(0.0)
        val tax = taxable * taxRate
        val total = taxable + tax
        val itemsCount = items.sumOf { it.quantity }

        return CartSummary(
            subtotal = subtotal,
            discount = discount,
            tax = tax,
            total = total,
            itemsCount = itemsCount
        )
    }
}
