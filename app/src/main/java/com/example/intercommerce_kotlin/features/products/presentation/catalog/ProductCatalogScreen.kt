package com.example.intercommerce_kotlin.features.products.presentation.catalog

import androidx.compose.material3.Button
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.intercommerce_kotlin.R
import com.example.intercommerce_kotlin.features.products.domain.model.Product
import com.example.intercommerce_kotlin.features.products.presentation.catalog.components.ProductCatalogCard
import com.example.intercommerce_kotlin.features.products.presentation.catalog.components.ProductCatalogCardSkeleton

private val AccentOrange = Color(0xFFFF5A1F)

@Composable
fun ProductCatalogRoute(
    onProductClick: (Int) -> Unit,
    onCartClick: () -> Unit,
    viewModel: ProductCatalogViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val pagedProducts = viewModel.pagedProducts.collectAsLazyPagingItems()

    ProductCatalogScreen(
        state = state,
        pagedProducts = pagedProducts,
        onQueryChange = { viewModel.onEvent(ProductCatalogUiEvent.QueryChanged(it)) },
        onRetryClick = { viewModel.onEvent(ProductCatalogUiEvent.Retry) },
        onProductClick = onProductClick,
        onCartClick = onCartClick,
        onFavoriteClick = { product -> viewModel.onFavoriteToggle(product) },
        onIncreaseClick = { product -> viewModel.increaseProductQuantity(product) },
        onDecreaseOrRemoveClick = { productId -> viewModel.decreaseOrRemoveProduct(productId) }
    )
}

@Composable
fun ProductCatalogScreen(
    state: ProductCatalogUiState,
    pagedProducts: androidx.paging.compose.LazyPagingItems<com.example.intercommerce_kotlin.features.products.domain.model.Product>,
    onQueryChange: (String) -> Unit,
    onRetryClick: () -> Unit,
    onProductClick: (Int) -> Unit,
    onCartClick: () -> Unit,
    onFavoriteClick: (Product) -> Unit,
    onIncreaseClick: (Product) -> Unit,
    onDecreaseOrRemoveClick: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 14.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))
        HeaderRow(
            onCartClick = onCartClick,
            cartItemsCount = state.cartItemsCount
        )
        Spacer(modifier = Modifier.height(14.dp))

        SearchRow(
            query = state.query,
            onQueryChange = onQueryChange
        )

        Spacer(modifier = Modifier.height(12.dp))

        val isPagedMode = state.query.isBlank()
        val isInitialPagedLoading = isPagedMode && pagedProducts.loadState.refresh is LoadState.Loading
        val pagedError = if (isPagedMode) pagedProducts.loadState.refresh as? LoadState.Error else null

        if (state.errorMessageRes != null && state.products.isEmpty() && !isPagedMode) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = stringResource(id = state.errorMessageRes))
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = onRetryClick) {
                        Text(text = androidx.compose.ui.res.stringResource(id = R.string.retry))
                    }
                }
            }
            return
        }

        if (!isPagedMode && state.products.isEmpty() && !state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = androidx.compose.ui.res.stringResource(id = R.string.catalog_empty_search),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            return
        }

        if (isInitialPagedLoading || state.isLoading) {
            ShimmerGrid()
            return
        }

        if (pagedError != null && isPagedMode && pagedProducts.itemCount == 0) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = stringResource(id = R.string.catalog_error_load_products))
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { pagedProducts.retry() }) {
                        Text(text = stringResource(id = R.string.retry))
                    }
                }
            }
            return
        }

        CatalogPullToRefresh(
            isRefreshing = isPagedMode && pagedProducts.loadState.refresh is LoadState.Loading,
            onRefresh = {
                if (isPagedMode) pagedProducts.refresh() else onRetryClick()
            }
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (state.isOffline) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Text(
                            text = stringResource(id = R.string.catalog_offline_banner),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }

                if (isPagedMode) {
                    items(count = pagedProducts.itemCount) { index ->
                        val product = pagedProducts[index] ?: return@items
                        val effectiveFavorite = state.favoriteOverrides[product.id] ?: product.isFavorite
                        ProductCatalogCard(
                            product = product.copy(isFavorite = effectiveFavorite),
                            quantityInCart = state.cartQuantities[product.id] ?: 0,
                            onClick = { onProductClick(product.id) },
                            onFavoriteClick = { onFavoriteClick(product) },
                            onIncreaseClick = { onIncreaseClick(product) },
                            onDecreaseOrRemoveClick = { onDecreaseOrRemoveClick(product.id) }
                        )
                    }
                } else {
                    items(items = state.products, key = { it.id }) { product ->
                        val effectiveFavorite = state.favoriteOverrides[product.id] ?: product.isFavorite
                        ProductCatalogCard(
                            product = product.copy(isFavorite = effectiveFavorite),
                            quantityInCart = state.cartQuantities[product.id] ?: 0,
                            onClick = { onProductClick(product.id) },
                            onFavoriteClick = { onFavoriteClick(product) },
                            onIncreaseClick = { onIncreaseClick(product) },
                            onDecreaseOrRemoveClick = { onDecreaseOrRemoveClick(product.id) }
                        )
                    }
                }

                if (isPagedMode && pagedProducts.loadState.append is LoadState.Loading) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CatalogPullToRefresh(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    content: @Composable () -> Unit
) {
    PullToRefreshBox(
        modifier = Modifier.fillMaxSize(),
        isRefreshing = isRefreshing,
        onRefresh = onRefresh
    ) {
        content()
    }
}

@Composable
private fun HeaderRow(onCartClick: () -> Unit, cartItemsCount: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text(
                    text = stringResource(id = R.string.hello_short),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF646B77)
                )
                Text(
                    text = "Jhon",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF646B77)
                )
            }
        }

        Box {
            IconButton(onClick = onCartClick) {
                Icon(
                    imageVector = Icons.Outlined.ShoppingCart,
                    contentDescription = stringResource(id = R.string.cd_cart),
                    tint = Color.Black
                )
            }
            if (cartItemsCount > 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(AccentOrange),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = cartItemsCount.toString(),
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchRow(query: String, onQueryChange: (String) -> Unit) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = query,
        onValueChange = onQueryChange,
        singleLine = true,
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = stringResource(id = R.string.cd_search)
            )
        },
        shape = RoundedCornerShape(18.dp),
        placeholder = { Text(stringResource(id = R.string.search_products_placeholder)) }
    )
}

@Composable
private fun ShimmerGrid() {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(8) {
            ProductCatalogCardSkeleton()
        }
    }
}
