package com.example.intercommerce_kotlin.features.products.presentation.detail

import com.example.intercommerce_kotlin.R
import com.example.intercommerce_kotlin.core.error.AppError
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
import com.example.intercommerce_kotlin.features.products.domain.usecase.GetProductDetailUseCase
import com.example.intercommerce_kotlin.testutil.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProductDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `load product success updates ui state`() = runTest {
        val productRepo = FakeProductRepository()
        val cartRepo = FakeCartRepository()
        val expected = sampleProduct(id = 5)
        productRepo.detailResult = AppResult.Success(expected)

        val viewModel = buildViewModel(productRepo, cartRepo)

        viewModel.loadProduct(5)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(5, state.product?.id)
        assertNull(state.errorMessageRes)
    }

    @Test
    fun `load product error sets detail error message`() = runTest {
        val productRepo = FakeProductRepository()
        val cartRepo = FakeCartRepository()
        productRepo.detailResult = AppResult.Error(AppError.NetworkUnavailable)

        val viewModel = buildViewModel(productRepo, cartRepo)

        viewModel.loadProduct(8)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(R.string.detail_error_load_product, state.errorMessageRes)
    }

    @Test
    fun `observe cart updates quantity for current product`() = runTest {
        val productRepo = FakeProductRepository()
        val cartRepo = FakeCartRepository()
        productRepo.detailResult = AppResult.Success(sampleProduct(id = 10))

        val viewModel = buildViewModel(productRepo, cartRepo)
        viewModel.loadProduct(10)
        advanceUntilIdle()

        cartRepo.emit(
            listOf(
                CartItem(10, "Item", 9.0, "", 3, 0.0),
                CartItem(20, "Other", 5.0, "", 2, 0.0)
            )
        )
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(3, state.quantityInCart)
        assertEquals(5, state.cartItemsCount)
    }

    @Test
    fun `increase or add adds when quantity is zero`() = runTest {
        val productRepo = FakeProductRepository()
        val cartRepo = FakeCartRepository()
        productRepo.detailResult = AppResult.Success(sampleProduct(id = 7, stock = 10))

        val viewModel = buildViewModel(productRepo, cartRepo)
        viewModel.loadProduct(7)
        advanceUntilIdle()

        viewModel.increaseOrAddToCart()
        advanceUntilIdle()

        assertEquals(7, cartRepo.lastAddedProduct?.id)
        assertEquals(1, cartRepo.lastAddedQuantity)
    }

    @Test
    fun `increase or add updates quantity when already in cart`() = runTest {
        val productRepo = FakeProductRepository()
        val cartRepo = FakeCartRepository()
        productRepo.detailResult = AppResult.Success(sampleProduct(id = 9, stock = 10))

        val viewModel = buildViewModel(productRepo, cartRepo)
        viewModel.loadProduct(9)
        advanceUntilIdle()
        cartRepo.emit(listOf(CartItem(9, "Item", 9.0, "", 2, 0.0)))
        advanceUntilIdle()

        viewModel.increaseOrAddToCart()
        advanceUntilIdle()

        assertEquals(9, cartRepo.lastUpdatedProductId)
        assertEquals(3, cartRepo.lastUpdatedQuantity)
    }

    @Test
    fun `decrease or remove removes when quantity is one`() = runTest {
        val productRepo = FakeProductRepository()
        val cartRepo = FakeCartRepository()
        productRepo.detailResult = AppResult.Success(sampleProduct(id = 4, stock = 5))

        val viewModel = buildViewModel(productRepo, cartRepo)
        viewModel.loadProduct(4)
        advanceUntilIdle()
        cartRepo.emit(listOf(CartItem(4, "Item", 9.0, "", 1, 0.0)))
        advanceUntilIdle()

        viewModel.decreaseOrRemoveFromCart()
        advanceUntilIdle()

        assertEquals(4, cartRepo.lastRemovedProductId)
    }

    @Test
    fun `increase or add does nothing when stock is zero`() = runTest {
        val productRepo = FakeProductRepository()
        val cartRepo = FakeCartRepository()
        productRepo.detailResult = AppResult.Success(sampleProduct(id = 12, stock = 0))

        val viewModel = buildViewModel(productRepo, cartRepo)
        viewModel.loadProduct(12)
        advanceUntilIdle()

        viewModel.increaseOrAddToCart()
        advanceUntilIdle()

        assertNull(cartRepo.lastAddedProduct)
        assertNull(cartRepo.lastUpdatedProductId)
    }

    @Test
    fun `consume added message clears field`() = runTest {
        val productRepo = FakeProductRepository()
        val cartRepo = FakeCartRepository()
        val viewModel = buildViewModel(productRepo, cartRepo)

        viewModel.consumeAddedMessage()

        assertNull(viewModel.uiState.value.addedToCartMessage)
    }

    private fun buildViewModel(
        productRepo: FakeProductRepository,
        cartRepo: FakeCartRepository
    ): ProductDetailViewModel {
        return ProductDetailViewModel(
            getProductDetailUseCase = GetProductDetailUseCase(productRepo),
            addProductToCartUseCase = AddProductToCartUseCase(cartRepo),
            observeCartUseCase = ObserveCartUseCase(cartRepo),
            updateCartItemQuantityUseCase = UpdateCartItemQuantityUseCase(cartRepo),
            removeCartItemUseCase = RemoveCartItemUseCase(cartRepo)
        )
    }

    private fun sampleProduct(id: Int, stock: Int = 10) = Product(
        id = id,
        title = "Product $id",
        description = "desc",
        price = 10.0,
        discountPercentage = 5.0,
        rating = 4.2,
        stock = stock,
        brand = "Brand",
        category = "beauty",
        isFavorite = false,
        thumbnail = "thumb",
        images = emptyList()
    )

    private class FakeProductRepository : ProductRepository {
        var detailResult: AppResult<Product> = AppResult.Error(AppError.NotFound)

        override suspend fun getProducts(page: Int, limit: Int): AppResult<PagedProducts> =
            AppResult.Success(PagedProducts(emptyList(), isOffline = false, endReached = true))

        override suspend fun searchProducts(query: String): AppResult<List<Product>> =
            AppResult.Success(emptyList())

        override suspend fun getProductDetail(id: Int): AppResult<Product> = detailResult

        override suspend fun updateFavorite(productId: Int, isFavorite: Boolean) = Unit

        override fun observeFavoriteProducts(): Flow<List<Product>> = flowOf(emptyList())
    }

    private class FakeCartRepository : CartRepository {
        private val cartFlow = MutableStateFlow<List<CartItem>>(emptyList())

        var lastAddedProduct: Product? = null
        var lastAddedQuantity: Int? = null
        var lastUpdatedProductId: Int? = null
        var lastUpdatedQuantity: Int? = null
        var lastRemovedProductId: Int? = null

        fun emit(items: List<CartItem>) {
            cartFlow.value = items
        }

        override suspend fun addProduct(product: Product, quantity: Int) {
            lastAddedProduct = product
            lastAddedQuantity = quantity
        }

        override suspend fun updateQuantity(productId: Int, quantity: Int) {
            lastUpdatedProductId = productId
            lastUpdatedQuantity = quantity
        }

        override suspend fun removeProduct(productId: Int) {
            lastRemovedProductId = productId
        }

        override suspend fun clearCart() = Unit

        override fun observeCart(): Flow<List<CartItem>> = cartFlow
    }
}
