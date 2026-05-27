package com.example.intercommerce_kotlin.features.products.presentation.favorites

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
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.intercommerce_kotlin.R
import com.example.intercommerce_kotlin.features.products.presentation.catalog.components.ProductCatalogCard

private val AccentOrange = Color(0xFFFF5A1F)

@Composable
fun FavoritesRoute(
    onProductClick: (Int) -> Unit,
    onCartClick: () -> Unit,
    viewModel: FavoritesViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    FavoritesScreen(
        state = state,
        onProductClick = onProductClick,
        onCartClick = onCartClick,
        onFavoriteClick = viewModel::onFavoriteToggle,
        onIncreaseClick = viewModel::increaseProductQuantity,
        onDecreaseOrRemoveClick = viewModel::decreaseOrRemoveProduct
    )
}

@Composable
private fun FavoritesScreen(
    state: FavoritesUiState,
    onProductClick: (Int) -> Unit,
    onCartClick: () -> Unit,
    onFavoriteClick: (Int) -> Unit,
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
        HeaderRow(onCartClick = onCartClick, cartItemsCount = state.cartItemsCount)
        Spacer(modifier = Modifier.height(14.dp))

        if (state.products.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = stringResource(id = R.string.favorites_empty),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            return
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(items = state.products, key = { it.id }) { product ->
                ProductCatalogCard(
                    product = product,
                    quantityInCart = state.cartQuantities[product.id] ?: 0,
                    onClick = { onProductClick(product.id) },
                    onFavoriteClick = { onFavoriteClick(product.id) },
                    onIncreaseClick = { onIncreaseClick(product.id) },
                    onDecreaseOrRemoveClick = { onDecreaseOrRemoveClick(product.id) }
                )
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
        Text(
            text = stringResource(id = R.string.favorites_title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

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
