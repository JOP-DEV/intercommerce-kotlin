package com.example.intercommerce_kotlin.features.cart.presentation

import com.example.intercommerce_kotlin.core.network.ConnectionStatus
import com.example.intercommerce_kotlin.core.network.ConnectivityObserver
import com.example.intercommerce_kotlin.features.cart.domain.model.CartItem
import com.example.intercommerce_kotlin.features.cart.domain.repository.CartRepository
import com.example.intercommerce_kotlin.features.cart.domain.usecase.CalculateCartSummaryUseCase
import com.example.intercommerce_kotlin.features.cart.domain.usecase.ObserveCartUseCase
import com.example.intercommerce_kotlin.features.cart.domain.usecase.RemoveCartItemUseCase
import com.example.intercommerce_kotlin.features.cart.domain.usecase.UpdateCartItemQuantityUseCase
import com.example.intercommerce_kotlin.features.products.domain.model.Product
import com.example.intercommerce_kotlin.testutil.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CartViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `observe cart updates ui state summary`() = runTest {
        val repository = FakeCartRepository()
        val connectivity = FakeConnectivityObserver(isConnected = true)
        val viewModel = CartViewModel(
            observeCartUseCase = ObserveCartUseCase(repository),
            updateCartItemQuantityUseCase = UpdateCartItemQuantityUseCase(repository),
            removeCartItemUseCase = RemoveCartItemUseCase(repository),
            calculateCartSummaryUseCase = CalculateCartSummaryUseCase(),
            connectivityObserver = connectivity
        )

        repository.emit(
            listOf(
                CartItem(
                    productId = 1,
                    title = "A",
                    price = 10.0,
                    thumbnail = "",
                    quantity = 2,
                    discountPercentage = 10.0
                )
            )
        )
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertFalse(state.isEmpty)
        assertEquals(20.0, state.summary.subtotal, 0.0001)
        assertEquals(2.0, state.summary.discount, 0.0001)
        assertEquals(18.0 * 0.19, state.summary.tax, 0.0001)
    }

    @Test
    fun `checkout offline shows no connection sheet`() = runTest {
        val repository = FakeCartRepository()
        val connectivity = FakeConnectivityObserver(isConnected = false)
        val viewModel = CartViewModel(
            observeCartUseCase = ObserveCartUseCase(repository),
            updateCartItemQuantityUseCase = UpdateCartItemQuantityUseCase(repository),
            removeCartItemUseCase = RemoveCartItemUseCase(repository),
            calculateCartSummaryUseCase = CalculateCartSummaryUseCase(),
            connectivityObserver = connectivity
        )

        viewModel.onCheckoutClick()

        assertTrue(viewModel.uiState.value.showNoConnectionSheet)
    }

    private class FakeCartRepository : CartRepository {
        private val cartFlow = MutableStateFlow<List<CartItem>>(emptyList())

        fun emit(items: List<CartItem>) {
            cartFlow.value = items
        }

        override suspend fun addProduct(product: Product, quantity: Int) = Unit

        override suspend fun updateQuantity(productId: Int, quantity: Int) = Unit

        override suspend fun removeProduct(productId: Int) = Unit

        override suspend fun clearCart() = Unit

        override fun observeCart(): Flow<List<CartItem>> = cartFlow
    }

    private class FakeConnectivityObserver(
        private val isConnected: Boolean
    ) : ConnectivityObserver {
        override val status: StateFlow<ConnectionStatus> = MutableStateFlow(
            if (isConnected) ConnectionStatus.Available else ConnectionStatus.Unavailable
        )

        override fun isConnected(): Boolean = isConnected
    }
}
