package com.example.intercommerce_kotlin.features.products.presentation.catalog

import com.example.intercommerce_kotlin.R
import com.example.intercommerce_kotlin.core.error.AppError
import com.example.intercommerce_kotlin.core.network.ConnectionStatus
import com.example.intercommerce_kotlin.core.network.ConnectivityObserver
import com.example.intercommerce_kotlin.core.result.AppResult
import com.example.intercommerce_kotlin.features.cart.domain.model.CartItem
import com.example.intercommerce_kotlin.features.cart.domain.repository.CartRepository
import com.example.intercommerce_kotlin.features.cart.domain.usecase.AddProductToCartUseCase
import com.example.intercommerce_kotlin.features.cart.domain.usecase.ObserveCartUseCase
import com.example.intercommerce_kotlin.features.cart.domain.usecase.RemoveCartItemUseCase
import com.example.intercommerce_kotlin.features.cart.domain.usecase.UpdateCartItemQuantityUseCase
import com.example.intercommerce_kotlin.features.products.domain.model.PagedProducts
import com.example.intercommerce_kotlin.features.products.domain.model.Product
import com.example.intercommerce_kotlin.features.products.domain.repository.ProductRepository
import com.example.intercommerce_kotlin.features.products.domain.usecase.GetProductsUseCase
import com.example.intercommerce_kotlin.features.products.domain.usecase.SearchProductsUseCase
import com.example.intercommerce_kotlin.features.products.domain.usecase.UpdateProductFavoriteUseCase
import com.example.intercommerce_kotlin.testutil.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProductCatalogViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `query changed success updates products and clears loading`() = runTest {
        val productRepo = FakeProductRepository()
        val cartRepo = FakeCartRepository()
        val connectivity = FakeConnectivityObserver()
        val expected = sampleProduct(id = 1, title = "Lipstick")
        productRepo.searchResult = AppResult.Success(listOf(expected))

        val viewModel = buildViewModel(productRepo, cartRepo, connectivity)

        viewModel.onEvent(ProductCatalogUiEvent.QueryChanged("lip"))
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("lip", state.query)
        assertFalse(state.isLoading)
        assertEquals(1, state.products.size)
        assertEquals(expected.id, state.products.first().id)
        assertEquals(true, state.endReached)
        assertEquals(null, state.errorMessageRes)
    }

    @Test
    fun `query changed error exposes catalog search error`() = runTest {
        val productRepo = FakeProductRepository()
        val cartRepo = FakeCartRepository()
        val connectivity = FakeConnectivityObserver()
        productRepo.searchResult = AppResult.Error(AppError.NetworkUnavailable)

        val viewModel = buildViewModel(productRepo, cartRepo, connectivity)

        viewModel.onEvent(ProductCatalogUiEvent.QueryChanged("abc"))
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(R.string.catalog_error_search, state.errorMessageRes)
    }

    @Test
    fun `observe cart updates count and quantities map`() = runTest {
        val productRepo = FakeProductRepository()
        val cartRepo = FakeCartRepository()
        val connectivity = FakeConnectivityObserver()
        val viewModel = buildViewModel(productRepo, cartRepo, connectivity)

        cartRepo.emit(
            listOf(
                CartItem(1, "A", 10.0, "", 2, 0.0),
                CartItem(2, "B", 12.0, "", 3, 0.0)
            )
        )
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(5, state.cartItemsCount)
        assertEquals(2, state.cartQuantities[1])
        assertEquals(3, state.cartQuantities[2])
    }

    @Test
    fun `increase quantity calls add when product is not in cart`() = runTest {
        val productRepo = FakeProductRepository()
        val cartRepo = FakeCartRepository()
        val connectivity = FakeConnectivityObserver()
        val product = sampleProduct(id = 10, title = "Cream")
        productRepo.searchResult = AppResult.Success(listOf(product))

        val viewModel = buildViewModel(productRepo, cartRepo, connectivity)
        viewModel.onEvent(ProductCatalogUiEvent.QueryChanged("cream"))
        advanceUntilIdle()

        viewModel.increaseProductQuantity(product)
        advanceUntilIdle()

        assertEquals(10, cartRepo.lastAddedProduct?.id)
        assertEquals(1, cartRepo.lastAddedQuantity)
    }

    @Test
    fun `decrease quantity removes when quantity is one`() = runTest {
        val productRepo = FakeProductRepository()
        val cartRepo = FakeCartRepository()
        val connectivity = FakeConnectivityObserver()
        val viewModel = buildViewModel(productRepo, cartRepo, connectivity)

        cartRepo.emit(listOf(CartItem(7, "P", 5.0, "", 1, 0.0)))
        advanceUntilIdle()

        viewModel.decreaseOrRemoveProduct(7)
        advanceUntilIdle()

        assertEquals(7, cartRepo.lastRemovedProductId)
    }

    @Test
    fun `favorite toggle updates repository and ui state`() = runTest {
        val productRepo = FakeProductRepository()
        val cartRepo = FakeCartRepository()
        val connectivity = FakeConnectivityObserver()
        val product = sampleProduct(id = 3, title = "Mask", isFavorite = false)
        productRepo.searchResult = AppResult.Success(listOf(product))

        val viewModel = buildViewModel(productRepo, cartRepo, connectivity)
        viewModel.onEvent(ProductCatalogUiEvent.QueryChanged("mask"))
        advanceUntilIdle()

        viewModel.onFavoriteToggle(product)
        advanceUntilIdle()

        assertEquals(3, productRepo.lastFavoriteProductId)
        assertTrue(productRepo.lastFavoriteValue ?: false)
        assertTrue(viewModel.uiState.value.products.first().isFavorite)
    }

    private fun buildViewModel(
        productRepo: FakeProductRepository,
        cartRepo: FakeCartRepository,
        connectivity: FakeConnectivityObserver
    ): ProductCatalogViewModel {
        return ProductCatalogViewModel(
            getProductsUseCase = GetProductsUseCase(productRepo),
            searchProductsUseCase = SearchProductsUseCase(productRepo),
            connectivityObserver = connectivity,
            observeCartUseCase = ObserveCartUseCase(cartRepo),
            addProductToCartUseCase = AddProductToCartUseCase(cartRepo),
            updateCartItemQuantityUseCase = UpdateCartItemQuantityUseCase(cartRepo),
            removeCartItemUseCase = RemoveCartItemUseCase(cartRepo),
            updateProductFavoriteUseCase = UpdateProductFavoriteUseCase(productRepo)
        )
    }

    private fun sampleProduct(
        id: Int,
        title: String,
        isFavorite: Boolean = false,
        stock: Int = 10
    ) = Product(
        id = id,
        title = title,
        description = "desc",
        price = 12.0,
        discountPercentage = 10.0,
        rating = 4.5,
        stock = stock,
        brand = "brand",
        category = "beauty",
        shippingInformation = "Ships in 2 weeks",
        returnPolicy = "7 days return policy",
        isFavorite = isFavorite,
        thumbnail = "thumb",
        images = emptyList()
    )

    private class FakeProductRepository : ProductRepository {
        var searchResult: AppResult<List<Product>> = AppResult.Success(emptyList())
        var lastFavoriteProductId: Int? = null
        var lastFavoriteValue: Boolean? = null

        override suspend fun getProducts(page: Int, limit: Int): AppResult<PagedProducts> {
            return AppResult.Success(PagedProducts(emptyList(), isOffline = false, endReached = true))
        }

        override suspend fun searchProducts(query: String): AppResult<List<Product>> = searchResult

        override suspend fun getProductDetail(id: Int): AppResult<Product> =
            AppResult.Error(AppError.NotFound)

        override suspend fun updateFavorite(productId: Int, isFavorite: Boolean) {
            lastFavoriteProductId = productId
            lastFavoriteValue = isFavorite
        }

        override fun observeFavoriteProducts(): Flow<List<Product>> = flowOf(emptyList())
    }

    private class FakeCartRepository : CartRepository {
        private val cartFlow = MutableStateFlow<List<CartItem>>(emptyList())

        var lastAddedProduct: Product? = null
        var lastAddedQuantity: Int? = null
        var lastRemovedProductId: Int? = null

        fun emit(items: List<CartItem>) {
            cartFlow.value = items
        }

        override suspend fun addProduct(product: Product, quantity: Int) {
            lastAddedProduct = product
            lastAddedQuantity = quantity
        }

        override suspend fun updateQuantity(productId: Int, quantity: Int) = Unit

        override suspend fun removeProduct(productId: Int) {
            lastRemovedProductId = productId
        }

        override suspend fun clearCart() = Unit

        override fun observeCart(): Flow<List<CartItem>> = cartFlow
    }

    private class FakeConnectivityObserver : ConnectivityObserver {
        override val status: StateFlow<ConnectionStatus> =
            MutableStateFlow(ConnectionStatus.Available)

        override fun isConnected(): Boolean = true
    }
}
