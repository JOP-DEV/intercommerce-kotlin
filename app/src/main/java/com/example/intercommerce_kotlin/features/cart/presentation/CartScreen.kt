package com.example.intercommerce_kotlin.features.cart.presentation

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.intercommerce_kotlin.R
import com.example.intercommerce_kotlin.core.ui.components.AppNetworkImage
import com.example.intercommerce_kotlin.features.cart.domain.model.CartItem
import com.example.intercommerce_kotlin.features.cart.presentation.components.NoConnectionBottomSheet

private val AccentOrange = Color(0xFFFF5A1F)
private val PositiveGreen = Color(0xFF71A521)

@Composable
fun CartRoute(onBack: () -> Unit, viewModel: CartViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    CartScreen(
        state = state,
        onBack = onBack,
        onIncrease = viewModel::increaseQuantity,
        onDecrease = viewModel::decreaseQuantity,
        onRemove = viewModel::removeItem,
        onCheckout = viewModel::onCheckoutClick,
        onDismissNoConnectionSheet = viewModel::dismissNoConnectionSheet
    )
}

@Composable
fun CartScreen(
    state: CartUiState,
    onBack: () -> Unit,
    onIncrease: (CartItem) -> Unit,
    onDecrease: (CartItem) -> Unit,
    onRemove: (CartItem) -> Unit,
    onCheckout: () -> Unit,
    onDismissNoConnectionSheet: () -> Unit
) {
    if (state.showNoConnectionSheet) {
        NoConnectionBottomSheet(
            onRetry = onCheckout,
            onDismiss = onDismissNoConnectionSheet
        )
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Outlined.ArrowBack, contentDescription = stringResource(id = R.string.cd_back))
                    }
                    Text(stringResource(id = R.string.cart_title), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { innerPadding ->
        when {
            state.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            state.isEmpty -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(stringResource(id = R.string.cart_empty))
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.items, key = { it.productId }) { item ->
                            CartItemCard(
                                item = item,
                                onIncrease = { onIncrease(item) },
                                onDecrease = { onDecrease(item) },
                                onRemove = { onRemove(item) }
                            )
                        }
                    }

                    SummaryCard(
                        itemsCount = state.summary.itemsCount,
                        subtotal = state.summary.subtotal,
                        discount = state.summary.discount,
                        tax = state.summary.tax,
                        total = state.summary.total,
                        onCheckout = onCheckout
                    )
                }
            }
        }
    }
}

@Composable
private fun CartItemCard(
    item: CartItem,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFFE8EAEE), RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AppNetworkImage(
                model = item.thumbnail,
                contentDescription = item.title,
                modifier = Modifier.size(88.dp)
            )
            Spacer(Modifier.size(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.title, style = MaterialTheme.typography.titleLarge)
                Text(
                    "$${"%.2f".format(item.price * (1 - item.discountPercentage / 100.0))}",
                    color = AccentOrange,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    QuantityButton(label = "-", onClick = onDecrease)
                    Text(item.quantity.toString(), style = MaterialTheme.typography.titleLarge)
                    QuantityButton(label = "+", onClick = onIncrease)
                }
            }
            IconButton(onClick = onRemove) {
                Icon(Icons.Outlined.DeleteOutline, contentDescription = stringResource(id = R.string.cd_delete))
            }
        }
    }
}

@Composable
private fun QuantityButton(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(34.dp)
            .border(1.dp, Color(0xFFDCE0E6), RoundedCornerShape(10.dp))
            .background(Color.White)
            .padding(2.dp)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = AccentOrange,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 2.dp)
        )
    }
}

@Composable
private fun SummaryCard(
    itemsCount: Int,
    subtotal: Double,
    discount: Double,
    tax: Double,
    total: Double,
    onCheckout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 10.dp)
            .border(1.dp, Color(0xFFE8EAEE), RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        SummaryRow(stringResource(id = R.string.cart_summary_subtotal, itemsCount), "$${"%.2f".format(subtotal)}")
        SummaryRow(stringResource(id = R.string.cart_summary_discount), "- $${"%.2f".format(discount)}", valueColor = PositiveGreen)
        SummaryRow(stringResource(id = R.string.cart_summary_tax), "$${"%.2f".format(tax)}")
        Spacer(Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(stringResource(id = R.string.cart_summary_total), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(
                "$${"%.2f".format(total)}",
                style = MaterialTheme.typography.headlineMedium,
                color = AccentOrange,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(12.dp))
        Button(
            onClick = onCheckout,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = AccentOrange)
        ) {
            Text(stringResource(id = R.string.cart_checkout))
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String, valueColor: Color = Color(0xFF141922)) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color(0xFF434A57))
        Text(value, color = valueColor, fontWeight = FontWeight.SemiBold)
    }
}
