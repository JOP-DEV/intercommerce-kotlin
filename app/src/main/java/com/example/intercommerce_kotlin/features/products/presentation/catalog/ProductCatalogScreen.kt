package com.example.intercommerce_kotlin.features.products.presentation.catalog

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
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.intercommerce_kotlin.features.products.presentation.catalog.components.ProductCatalogCard
import com.example.intercommerce_kotlin.features.products.presentation.catalog.components.ProductCatalogCardSkeleton
import kotlinx.coroutines.flow.distinctUntilChanged

private val AccentOrange = Color(0xFFFF5A1F)

@Composable
fun ProductCatalogRoute(
    onProductClick: (Int) -> Unit,
    onCartClick: () -> Unit,
    viewModel: ProductCatalogViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val gridState = rememberLazyGridState()

    HandleLoadMore(gridState = gridState) {
        viewModel.onEvent(ProductCatalogUiEvent.LoadMore)
    }

    ProductCatalogScreen(
        state = state,
        gridState = gridState,
        onQueryChange = { viewModel.onEvent(ProductCatalogUiEvent.QueryChanged(it)) },
        onProductClick = onProductClick,
        onCartClick = onCartClick,
        onIncreaseClick = { productId -> viewModel.increaseProductQuantity(productId) },
        onDecreaseOrRemoveClick = { productId -> viewModel.decreaseOrRemoveProduct(productId) }
    )
}

@Composable
private fun HandleLoadMore(gridState: LazyGridState, onLoadMore: () -> Unit) {
    LaunchedEffect(gridState) {
        snapshotFlow { gridState.layoutInfo }
            .distinctUntilChanged()
            .collect { layoutInfo ->
                val totalItems = layoutInfo.totalItemsCount
                val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                if (totalItems > 0 && lastVisibleItem >= totalItems - 4) {
                    onLoadMore()
                }
            }
    }
}

@Composable
fun ProductCatalogScreen(
    state: ProductCatalogUiState,
    gridState: LazyGridState,
    onQueryChange: (String) -> Unit,
    onProductClick: (Int) -> Unit,
    onCartClick: () -> Unit,
    onIncreaseClick: (Int) -> Unit,
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

        if (state.errorMessage != null && state.products.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = state.errorMessage)
            }
            return
        }

        if (state.query.isNotBlank() && state.products.isEmpty() && !state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "No se encontraron productos",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            return
        }

        val products = state.products
        if (state.isLoading && products.isEmpty()) {
            ShimmerGrid()
            return
        }

        LazyVerticalGrid(
            state = gridState,
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (state.isOffline) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text(
                        text = "Mostrando datos guardados (modo offline)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }

            items(items = products, key = { it.id }) { product ->
                ProductCatalogCard(
                    product = product,
                    quantityInCart = state.cartQuantities[product.id] ?: 0,
                    onClick = { onProductClick(product.id) },
                    onIncreaseClick = { onIncreaseClick(product.id) },
                    onDecreaseOrRemoveClick = { onDecreaseOrRemoveClick(product.id) }
                )
            }

            if (state.isLoadingMore) {
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

@Composable
private fun HeaderRow(onCartClick: () -> Unit, cartItemsCount: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = {}) {
                Icon(imageVector = Icons.Outlined.Menu, contentDescription = "Menu")
            }
            Column {
                Text(
                    text = "Hola!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF646B77)
                )
            }
        }

        Box {
            IconButton(onClick = onCartClick) {
                Icon(
                    imageVector = Icons.Outlined.ShoppingCart,
                    contentDescription = "Cart",
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
                contentDescription = "Buscar"
            )
        },
        shape = RoundedCornerShape(18.dp),
        placeholder = { Text("Buscar productos...") }
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
