package com.example.intercommerce_kotlin.features.products.presentation.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.intercommerce_kotlin.R
import com.example.intercommerce_kotlin.core.network.NetworkConstants
import com.example.intercommerce_kotlin.core.network.ConnectionStatus
import com.example.intercommerce_kotlin.core.network.ConnectivityObserver
import com.example.intercommerce_kotlin.core.result.AppResult
import com.example.intercommerce_kotlin.features.cart.domain.usecase.AddProductToCartUseCase
import com.example.intercommerce_kotlin.features.cart.domain.usecase.ObserveCartUseCase
import com.example.intercommerce_kotlin.features.cart.domain.usecase.RemoveCartItemUseCase
import com.example.intercommerce_kotlin.features.cart.domain.usecase.UpdateCartItemQuantityUseCase
import com.example.intercommerce_kotlin.features.products.domain.usecase.GetProductsUseCase
import com.example.intercommerce_kotlin.features.products.domain.model.Product
import com.example.intercommerce_kotlin.features.products.domain.usecase.UpdateProductFavoriteUseCase
import com.example.intercommerce_kotlin.features.products.domain.usecase.SearchProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ProductCatalogViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase,
    private val searchProductsUseCase: SearchProductsUseCase,
    private val connectivityObserver: ConnectivityObserver,
    private val observeCartUseCase: ObserveCartUseCase,
    private val addProductToCartUseCase: AddProductToCartUseCase,
    private val updateCartItemQuantityUseCase: UpdateCartItemQuantityUseCase,
    private val removeCartItemUseCase: RemoveCartItemUseCase,
    private val updateProductFavoriteUseCase: UpdateProductFavoriteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductCatalogUiState())
    val uiState: StateFlow<ProductCatalogUiState> = _uiState.asStateFlow()

    private var previousConnectionStatus: ConnectionStatus? = null

    val pagedProducts: Flow<PagingData<com.example.intercommerce_kotlin.features.products.domain.model.Product>> =
        Pager(
            config = PagingConfig(
                pageSize = NetworkConstants.PAGE_LIMIT,
                initialLoadSize = NetworkConstants.PAGE_LIMIT,
                prefetchDistance = 2,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                ProductPagingSource(
                    getProductsUseCase = getProductsUseCase,
                    onPageOfflineStatus = { isOffline ->
                        _uiState.update { it.copy(isOffline = isOffline) }
                    }
                )
            }
        ).flow.cachedIn(viewModelScope)

    init {
        observeCart()
        observeConnectivity()
    }

    private fun observeConnectivity() {
        viewModelScope.launch {
            connectivityObserver.status
                .distinctUntilChanged()
                .collect { status ->
                    val previous = previousConnectionStatus
                    previousConnectionStatus = status

                    if (previous == null) return@collect

                    if (previous == ConnectionStatus.Unavailable && status == ConnectionStatus.Available) {
                        retryCurrentRequest()
                    }
                }
        }
    }

    private fun observeCart() {
        viewModelScope.launch {
            observeCartUseCase().collect { items ->
                _uiState.update {
                    it.copy(
                        cartItemsCount = items.sumOf { item -> item.quantity },
                        cartQuantities = items.associate { item -> item.productId to item.quantity }
                    )
                }
            }
        }
    }

    fun addProductToCart(product: Product) {
        viewModelScope.launch {
            addProductToCartUseCase(product, 1)
        }
    }

    fun increaseProductQuantity(product: Product) {
        val productId = product.id
        val current = _uiState.value.cartQuantities[productId] ?: 0
        if (current >= product.stock) return
        if (current == 0) {
            addProductToCart(product)
            return
        }
        viewModelScope.launch {
            updateCartItemQuantityUseCase(productId = productId, quantity = current + 1)
        }
    }

    fun decreaseOrRemoveProduct(productId: Int) {
        val current = _uiState.value.cartQuantities[productId] ?: 0
        if (current <= 1) {
            viewModelScope.launch {
                removeCartItemUseCase(productId)
            }
            return
        }
        viewModelScope.launch {
            updateCartItemQuantityUseCase(productId = productId, quantity = current - 1)
        }
    }

    fun onFavoriteToggle(product: Product) {
        val productId = product.id
        val nextValue = !product.isFavorite
        viewModelScope.launch {
            updateProductFavoriteUseCase(productId, nextValue)
            _uiState.update { state ->
                state.copy(
                    products = state.products.map {
                        if (it.id == productId) it.copy(isFavorite = nextValue) else it
                    }
                )
            }
        }
    }

    fun onEvent(event: ProductCatalogUiEvent) {
        when (event) {
            ProductCatalogUiEvent.Retry -> retryCurrentRequest()
            is ProductCatalogUiEvent.QueryChanged -> onQueryChanged(event.query)
        }
    }

    private fun retryCurrentRequest() {
        if (_uiState.value.query.isNotBlank()) {
            performSearch(_uiState.value.query.trim())
        }
    }

    private fun onQueryChanged(query: String) {
        _uiState.update { it.copy(query = query) }
        if (query.isBlank()) {
            _uiState.update { state ->
                state.copy(
                    products = emptyList(),
                    isLoading = false,
                    errorMessageRes = null
                )
            }
            return
        }

        performSearch(query.trim())
    }

    private fun performSearch(query: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessageRes = null) }
            when (val result = searchProductsUseCase(query)) {
                is AppResult.Success -> {
                    _uiState.update {
                        it.copy(
                            products = result.data,
                            isLoading = false,
                            isOffline = false,
                            endReached = true
                        )
                    }
                }
                is AppResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessageRes = R.string.catalog_error_search
                        )
                    }
                }
            }
        }
    }

}
