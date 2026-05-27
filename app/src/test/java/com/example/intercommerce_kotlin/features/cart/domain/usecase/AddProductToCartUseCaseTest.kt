package com.example.intercommerce_kotlin.features.cart.domain.usecase

import com.example.intercommerce_kotlin.features.cart.domain.model.CartItem
import com.example.intercommerce_kotlin.features.cart.domain.repository.CartRepository
import com.example.intercommerce_kotlin.features.products.domain.model.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class AddProductToCartUseCaseTest {

    @Test
    fun `delegate add product to repository with same payload`() = runTest {
        val repository = FakeCartRepository()
        val useCase = AddProductToCartUseCase(repository)
        val product = Product(
            id = 7,
            title = "Lipstick",
            description = "desc",
            price = 19.99,
            discountPercentage = 5.0,
            rating = 4.5,
            stock = 20,
            brand = "Brand",
            category = "beauty",
            isFavorite = false,
            thumbnail = "thumb",
            images = emptyList()
        )

        useCase(product, 3)

        assertNotNull(repository.lastAddedProduct)
        assertEquals(7, repository.lastAddedProduct?.id)
        assertEquals(3, repository.lastAddedQuantity)
    }

    private class FakeCartRepository : CartRepository {
        var lastAddedProduct: Product? = null
        var lastAddedQuantity: Int? = null

        override suspend fun addProduct(product: Product, quantity: Int) {
            lastAddedProduct = product
            lastAddedQuantity = quantity
        }

        override suspend fun updateQuantity(productId: Int, quantity: Int) = Unit

        override suspend fun removeProduct(productId: Int) = Unit

        override suspend fun clearCart() = Unit

        override fun observeCart(): Flow<List<CartItem>> = flowOf(emptyList())
    }
}
