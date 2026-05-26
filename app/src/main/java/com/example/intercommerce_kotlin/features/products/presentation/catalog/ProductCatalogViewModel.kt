package com.example.intercommerce_kotlin.features.products.presentation.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.intercommerce_kotlin.R
import com.example.intercommerce_kotlin.core.network.ConnectionStatus
import com.example.intercommerce_kotlin.core.network.ConnectivityObserver
import com.example.intercommerce_kotlin.core.result.AppResult
import com.example.intercommerce_kotlin.features.cart.domain.usecase.AddProductToCartUseCase
import com.example.intercommerce_kotlin.features.cart.domain.usecase.ObserveCartUseCase
import com.example.intercommerce_kotlin.features.cart.domain.usecase.RemoveCartItemUseCase
import com.example.intercommerce_kotlin.features.cart.domain.usecase.UpdateCartItemQuantityUseCase
import com.example.intercommerce_kotlin.features.products.domain.usecase.GetProductsUseCase
import com.example.intercommerce_kotlin.features.products.domain.usecase.SearchProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.asSharedFlow
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
    private val removeCartItemUseCase: RemoveCartItemUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductCatalogUiState())
    val uiState: StateFlow<ProductCatalogUiState> = _uiState.asStateFlow()
    private val _uiEffect = MutableSharedFlow<ProductCatalogUiEffect>()
    val uiEffect: SharedFlow<ProductCatalogUiEffect> = _uiEffect.asSharedFlow()

    private var currentPage = 0
    private var previousConnectionStatus: ConnectionStatus? = null
    private var pendingConnectionRestoredMessage = false

    init {
        observeCart()
        observeConnectivity()
        onEvent(ProductCatalogUiEvent.LoadInitial)
    }

    private fun observeConnectivity() {
        viewModelScope.launch {
            connectivityObserver.status
                .distinctUntilChanged()
                .collect { status ->
                    val previous = previousConnectionStatus
                    previousConnectionStatus = status

                    if (previous == null) return@collect

                    if (previous == ConnectionStatus.Available && status == ConnectionStatus.Unavailable) {
                        val message = if (_uiState.value.products.isNotEmpty()) {
                            R.string.offline_with_cache
                        } else {
                            R.string.offline_without_cache
                        }
                        _uiEffect.emit(ProductCatalogUiEffect.ShowToast(message))
                    } else if (previous == ConnectionStatus.Unavailable && status == ConnectionStatus.Available) {
                        pendingConnectionRestoredMessage = true
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

    fun addProductToCart(productId: Int) {
        val product = _uiState.value.products.firstOrNull { it.id == productId } ?: return
        viewModelScope.launch {
            addProductToCartUseCase(product, 1)
        }
    }

    fun increaseProductQuantity(productId: Int) {
        val current = _uiState.value.cartQuantities[productId] ?: 0
        val product = _uiState.value.products.firstOrNull { it.id == productId } ?: return
        if (current >= product.stock) return
        if (current == 0) {
            addProductToCart(productId)
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

    fun onEvent(event: ProductCatalogUiEvent) {
        when (event) {
            ProductCatalogUiEvent.LoadInitial -> loadInitial()
            ProductCatalogUiEvent.LoadMore -> loadMore()
            ProductCatalogUiEvent.Retry -> retryCurrentRequest()
            is ProductCatalogUiEvent.QueryChanged -> onQueryChanged(event.query)
        }
    }

    private fun retryCurrentRequest() {
        if (_uiState.value.query.isBlank()) {
            loadInitial()
        } else {
            performSearch(_uiState.value.query.trim())
        }
    }

    private fun loadInitial() {
        viewModelScope.launch {
            currentPage = 0
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                    endReached = false
                )
            }
            when (val result = getProductsUseCase(currentPage)) {
                is AppResult.Success -> {
                    _uiState.update {
                        it.copy(
                            products = result.data.items,
                            isLoading = false,
                            isOffline = result.data.isOffline,
                            endReached = result.data.endReached
                        )
                    }
                    if (result.data.isOffline) {
                        _uiEffect.emit(ProductCatalogUiEffect.ShowToast(R.string.offline_with_cache))
                    }
                    emitConnectionRestoredIfNeeded()
                }
                is AppResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "No pudimos cargar los productos. Revisa tu conexión e inténtalo nuevamente."
                        )
                    }
                    if (!connectivityObserver.isConnected()) {
                        _uiEffect.emit(ProductCatalogUiEffect.ShowToast(R.string.offline_without_cache))
                    }
                }
            }
        }
    }

    private fun loadMore() {
        val state = _uiState.value
        if (state.isLoading || state.isLoadingMore || state.endReached || state.query.isNotBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingMore = true, errorMessage = null) }
            val nextPage = currentPage + 1
            when (val result = getProductsUseCase(nextPage)) {
                is AppResult.Success -> {
                    currentPage = nextPage
                    _uiState.update {
                        it.copy(
                            products = it.products + result.data.items,
                            isLoadingMore = false,
                            isOffline = result.data.isOffline,
                            endReached = result.data.endReached
                        )
                    }
                    if (result.data.isOffline) {
                        _uiEffect.emit(ProductCatalogUiEffect.ShowToast(R.string.offline_with_cache))
                    }
                    emitConnectionRestoredIfNeeded()
                }
                is AppResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoadingMore = false,
                            errorMessage = "No pudimos cargar más productos."
                        )
                    }
                }
            }
        }
    }

    private fun onQueryChanged(query: String) {
        _uiState.update { it.copy(query = query) }
        if (query.isBlank()) {
            loadInitial()
            return
        }

        performSearch(query.trim())
    }

    private fun performSearch(query: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
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
                    if (!connectivityObserver.isConnected()) {
                        _uiEffect.emit(ProductCatalogUiEffect.ShowToast(R.string.offline_with_cache))
                    }
                    emitConnectionRestoredIfNeeded()
                }
                is AppResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "No encontramos resultados para tu búsqueda."
                        )
                    }
                    if (!connectivityObserver.isConnected()) {
                        _uiEffect.emit(ProductCatalogUiEffect.ShowToast(R.string.offline_without_cache))
                    }
                }
            }
        }
    }

    private fun emitConnectionRestoredIfNeeded() {
        if (!pendingConnectionRestoredMessage) return
        pendingConnectionRestoredMessage = false
        viewModelScope.launch {
            _uiEffect.emit(ProductCatalogUiEffect.ShowToast(R.string.connection_restored))
        }
    }
}
