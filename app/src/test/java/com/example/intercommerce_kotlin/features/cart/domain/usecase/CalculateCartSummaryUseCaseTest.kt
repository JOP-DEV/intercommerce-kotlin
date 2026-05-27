package com.example.intercommerce_kotlin.features.cart.domain.usecase

import com.example.intercommerce_kotlin.features.cart.domain.model.CartItem
import org.junit.Assert.assertEquals
import org.junit.Test

class CalculateCartSummaryUseCaseTest {

    private val useCase = CalculateCartSummaryUseCase()

    @Test
    fun `calculate totals with discount and tax`() {
        val items = listOf(
            CartItem(
                productId = 1,
                title = "Product A",
                price = 100.0,
                thumbnail = "",
                quantity = 2,
                discountPercentage = 10.0
            ),
            CartItem(
                productId = 2,
                title = "Product B",
                price = 50.0,
                thumbnail = "",
                quantity = 1,
                discountPercentage = 20.0
            )
        )

        val summary = useCase(items)

        assertEquals(250.0, summary.subtotal, 0.0001)
        assertEquals(30.0, summary.discount, 0.0001)
        assertEquals(41.8, summary.tax, 0.0001)
        assertEquals(261.8, summary.total, 0.0001)
        assertEquals(3, summary.itemsCount)
    }

    @Test
    fun `return zero summary for empty cart`() {
        val summary = useCase(emptyList())

        assertEquals(0.0, summary.subtotal, 0.0001)
        assertEquals(0.0, summary.discount, 0.0001)
        assertEquals(0.0, summary.tax, 0.0001)
        assertEquals(0.0, summary.total, 0.0001)
        assertEquals(0, summary.itemsCount)
    }
}
