package com.example.intercommerce_kotlin.features.products.presentation.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.intercommerce_kotlin.R
import com.example.intercommerce_kotlin.features.products.presentation.detail.components.ProductDetailBottomBar
import kotlin.math.floor

private val AccentOrange = Color(0xFFFF5A1F)
private val PositiveGreen = Color(0xFF71A521)

@Composable
fun ProductDetailRoute(
    productId: Int,
    onBack: () -> Unit,
    onGoToCart: () -> Unit,
    viewModel: ProductDetailViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val haptic = LocalHapticFeedback.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(productId) {
        viewModel.loadProduct(productId)
    }

    LaunchedEffect(state.addedToCartMessage) {
        state.addedToCartMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.consumeAddedMessage()
        }
    }

    ProductDetailScreen(
        state = state,
        snackbarHostState = snackbarHostState,
        onBack = onBack,
        onGoToCart = onGoToCart,
        onSelectImage = viewModel::selectImage,
        onDecreaseOrRemove = viewModel::decreaseOrRemoveFromCart,
        onIncreaseOrAdd = {
            haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
            viewModel.increaseOrAddToCart()
        }
    )
}

@Composable
fun ProductDetailScreen(
    state: ProductDetailUiState,
    snackbarHostState: SnackbarHostState,
    onBack: () -> Unit,
    onGoToCart: () -> Unit,
    onSelectImage: (Int) -> Unit,
    onDecreaseOrRemove: () -> Unit,
    onIncreaseOrAdd: () -> Unit
) {
    val product = state.product

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Outlined.ArrowBack, contentDescription = stringResource(id = R.string.cd_back))
                }
                IconButton(onClick = onGoToCart) {
                    Icon(Icons.Outlined.ShoppingCart, contentDescription = stringResource(id = R.string.cd_cart))
                }
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            if (product != null) {
                ProductDetailBottomBar(
                    quantityInCart = state.quantityInCart,
                    canIncreaseQuantity = state.quantityInCart < product.stock,
                    onDecreaseOrRemove = onDecreaseOrRemove,
                    onIncreaseOrAdd = onIncreaseOrAdd
                )
            }
        }
    ) { innerPadding ->
        when {
            state.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            state.errorMessageRes != null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(stringResource(id = state.errorMessageRes))
                }
            }

            product != null -> {
                val images = if (product.images.isNotEmpty()) product.images else listOf(product.thumbnail)
                val selected = images.getOrElse(state.selectedImageIndex) { images.first() }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = PaddingValues(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(290.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .border(1.dp, Color(0xFFE8EAEE), RoundedCornerShape(18.dp))
                                .padding(12.dp)
                        ) {
                            AsyncImage(
                                model = selected,
                                contentDescription = product.title,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )
                            Icon(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .background(Color.White, CircleShape)
                                    .padding(6.dp),
                                imageVector = Icons.Outlined.FavoriteBorder,
                                contentDescription = stringResource(id = R.string.cd_favorite)
                            )
                        }
                    }

                    item {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            itemsIndexed(images) { index, image ->
                                AsyncImage(
                                    model = image,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(72.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .border(
                                            1.dp,
                                            if (index == state.selectedImageIndex) AccentOrange else Color(0xFFE8EAEE),
                                            RoundedCornerShape(12.dp)
                                        )
                                        .clickable { onSelectImage(index) }
                                        .padding(4.dp),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }

                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(18.dp))
                                .border(1.dp, Color(0xFFE8EAEE), RoundedCornerShape(18.dp))
                                .padding(14.dp)
                        ) {
                            Text(product.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = "$${"%.2f".format(product.price * (1 - product.discountPercentage / 100))}",
                                    color = AccentOrange,
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "$${"%.2f".format(product.price)}",
                                    color = Color(0xFF8A8E98),
                                    style = MaterialTheme.typography.titleMedium,
                                    textDecoration = TextDecoration.LineThrough
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(AccentOrange)
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("-${truncateToInt(product.discountPercentage)}%", color = Color.White)
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                            val stockLabel = when {
                                product.stock <= 0 -> stringResource(id = R.string.out_of_stock)
                                product.stock <= 10 -> stringResource(id = R.string.stock_last_units, product.stock)
                                else -> stringResource(id = R.string.stock_available_units, product.stock)
                            }
                            val stockColor = if (product.stock > 0) PositiveGreen else Color(0xFFB42318)

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("★ ${"%.1f".format(product.rating)}")
                                Text("•")
                                Text(stockLabel, color = stockColor)
                            }
                            Spacer(Modifier.height(10.dp))
                            Text(product.description, style = MaterialTheme.typography.bodyLarge, color = Color(0xFF545A67))
                            Spacer(Modifier.height(10.dp))
                            DetailRow(stringResource(id = R.string.label_brand), product.brand ?: "-")
                            DetailRow(stringResource(id = R.string.label_category), product.category)
                            DetailRow(stringResource(id = R.string.label_stock), product.stock.toString())
                            DetailRow(stringResource(id = R.string.label_shipping), stringResource(id = R.string.shipping_eta))
                            DetailRow(stringResource(id = R.string.label_return), stringResource(id = R.string.return_eta))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Icon(Icons.Outlined.Share, contentDescription = stringResource(id = R.string.cd_share))
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun truncateToInt(value: Double): Int = floor(value).toInt()

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontWeight = FontWeight.SemiBold)
        Text(value, color = Color(0xFF5B6170))
    }
}
