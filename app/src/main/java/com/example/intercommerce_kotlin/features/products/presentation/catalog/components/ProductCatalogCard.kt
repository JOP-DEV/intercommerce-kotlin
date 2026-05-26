package com.example.intercommerce_kotlin.features.products.presentation.catalog.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.intercommerce_kotlin.features.products.domain.model.Product
import kotlin.math.roundToInt

private val AccentOrange = Color(0xFFFF5A1F)
private val PositiveGreen = Color(0xFF71A521)
private val CardBorder = Color(0xFFE8EAEE)

@Composable
fun ProductCatalogCard(
    product: Product,
    onClick: () -> Unit,
    onAddToCartClick: () -> Unit = {}
) {
    val discount = product.discountPercentage.roundToInt().coerceAtLeast(0)
    val discountedPrice = (product.price * (1 - (product.discountPercentage / 100))).coerceAtLeast(0.0)

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(10.dp)
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth()) {
                if (discount > 0) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(AccentOrange)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "-$discount%",
                            color = Color.White,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Icon(
                    modifier = Modifier.align(Alignment.TopEnd),
                    imageVector = Icons.Outlined.FavoriteBorder,
                    contentDescription = "Favorito",
                    tint = Color(0xFF44474F)
                )
            }

            AsyncImage(
                model = product.thumbnail,
                contentDescription = product.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(118.dp)
                    .padding(top = 6.dp),
                contentScale = ContentScale.Fit
            )

            Text(
                text = product.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .heightIn(min = 52.dp)
            )

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                Text(
                    text = "$${"%.2f".format(discountedPrice)}",
                    color = AccentOrange,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(6.dp))
                if (discount > 0) {
                    Text(
                        text = "$${"%.2f".format(product.price)}",
                        color = Color(0xFF8A8E98),
                        style = MaterialTheme.typography.bodyMedium,
                        textDecoration = TextDecoration.LineThrough
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "★ ${"%.1f".format(product.rating)}",
                    color = AccentOrange,
                    style = MaterialTheme.typography.bodySmall
                )
                Column(horizontalAlignment = Alignment.End) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(AccentOrange)
                    ) {
                        IconButton(
                            onClick = onAddToCartClick,
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ShoppingCart,
                                contentDescription = "Agregar al carrito",
                                tint = Color.White,
                                modifier = Modifier.size(17.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "● Stock: ${product.stock}",
                        color = PositiveGreen,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
fun ProductCatalogCardSkeleton() {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val alpha by transition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    val shimmerColor = Color.Gray.copy(alpha = alpha)

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
            .padding(10.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Box(
                modifier = Modifier
                    .width(52.dp)
                    .height(24.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(shimmerColor)
            )
            Box(
                modifier = Modifier
                    .width(22.dp)
                    .height(22.dp)
                    .clip(RoundedCornerShape(11.dp))
                    .background(shimmerColor)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(118.dp)
                .padding(top = 6.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(shimmerColor)
        )

        Spacer(modifier = Modifier.height(10.dp))
        Column(modifier = Modifier.height(52.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .height(16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(shimmerColor)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.62f)
                    .height(16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(shimmerColor)
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth(0.45f)
                .height(18.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(shimmerColor)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.35f)
                    .height(14.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(shimmerColor)
            )
            Column(horizontalAlignment = Alignment.End) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(shimmerColor)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .width(62.dp)
                        .height(14.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(shimmerColor)
                )
            }
        }
    }
}
