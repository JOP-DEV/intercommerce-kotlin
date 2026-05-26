package com.example.intercommerce_kotlin.features.products.presentation.detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

private val AccentOrange = Color(0xFFFF5A1F)
private val CartControlHeight = 42.dp
private val CartControlCorner = 21.dp

@Composable
fun ProductDetailBottomBar(
    quantityInCart: Int,
    canIncreaseQuantity: Boolean,
    onDecreaseOrRemove: () -> Unit,
    onIncreaseOrAdd: () -> Unit
) {
    val containerColor = if (canIncreaseQuantity || quantityInCart > 0) AccentOrange else Color(0xFFB8BCC5)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(CartControlHeight)
                .border(1.dp, Color(0xFFE4E6EA), RoundedCornerShape(CartControlCorner))
                .background(containerColor, RoundedCornerShape(CartControlCorner)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (quantityInCart <= 0) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(enabled = canIncreaseQuantity) { onIncreaseOrAdd() },
                    contentAlignment = Alignment.Center
                ) {
                    Box(modifier = Modifier.fillMaxHeight(), contentAlignment = Alignment.Center) {
                        Text(
                            text = if (canIncreaseQuantity) "Agregar al carrito" else "Agotado",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable { onDecreaseOrRemove() },
                    contentAlignment = Alignment.Center
                ) {
                    if (quantityInCart == 1) {
                        Icon(
                            imageVector = Icons.Outlined.DeleteOutline,
                            contentDescription = "Eliminar",
                            tint = Color.White
                        )
                    } else {
                        Text(
                            text = "−",
                            color = Color.White,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                VerticalSeparator()

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = quantityInCart.toString(),
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                VerticalSeparator()

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable(enabled = canIncreaseQuantity) { onIncreaseOrAdd() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "+",
                        color = if (canIncreaseQuantity) Color.White else Color.White.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun VerticalSeparator() {
    Box(
        modifier = Modifier
            .padding(vertical = 10.dp)
            .size(width = 1.dp, height = 22.dp)
            .background(Color.White.copy(alpha = 0.55f))
    )
}
