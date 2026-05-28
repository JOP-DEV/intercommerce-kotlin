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
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.intercommerce_kotlin.core.ui.components.AppNetworkImage
import com.example.intercommerce_kotlin.R
import com.example.intercommerce_kotlin.features.products.domain.model.Product
import kotlin.math.floor

private val AccentOrange = Color(0xFFFF5A1F)
private val CardBorder = Color(0xFFE8EAEE)
private val CartControlHeight = 40.dp
private val CartControlCorner = 20.dp

@Composable
fun ProductCatalogCard(
    product: Product,
    quantityInCart: Int,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit = {},
    onIncreaseClick: () -> Unit = {},
    onDecreaseOrRemoveClick: () -> Unit = {}
) {
    val discount = truncateToInt(product.discountPercentage).coerceAtLeast(0)
    val discountedPrice = (product.price * (1 - (product.discountPercentage / 100))).coerceAtLeast(0.0)
    val availableStock = (product.stock - quantityInCart).coerceAtLeast(0)
    val canIncreaseQuantity = availableStock > 0

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
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .clickable(onClick = onFavoriteClick),
                    imageVector = if (product.isFavorite) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = stringResource(id = R.string.cd_favorite),
                    tint = if (product.isFavorite) AccentOrange else Color(0xFF44474F)
                )
            }

            AppNetworkImage(
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

            Text(
                text = "★ ${"%.1f".format(product.rating)}",
                color = AccentOrange,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 6.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))
            if (quantityInCart <= 0) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(CartControlHeight)
                        .clip(RoundedCornerShape(CartControlCorner))
                        .background(if (canIncreaseQuantity) AccentOrange else Color(0xFFB8BCC5))
                        .clickable(enabled = canIncreaseQuantity) { onIncreaseClick() },
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (canIncreaseQuantity) {
                            stringResource(id = R.string.add_to_cart)
                        } else {
                            stringResource(id = R.string.out_of_stock)
                        },
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(CartControlHeight)
                        .clip(RoundedCornerShape(CartControlCorner))
                        .background(AccentOrange),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onDecreaseOrRemoveClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        if (quantityInCart == 1) {
                            Icon(
                                imageVector = Icons.Outlined.DeleteOutline,
                                contentDescription = stringResource(id = R.string.cd_delete),
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

                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
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
                            .clickable(enabled = canIncreaseQuantity) { onIncreaseClick() },
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
}

private fun truncateToInt(value: Double): Int = floor(value).toInt()

@Composable
private fun VerticalSeparator() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(22.dp)
            .background(Color.White.copy(alpha = 0.55f))
    )
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
                .fillMaxWidth(0.42f)
                .height(20.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(shimmerColor)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth(0.30f)
                .height(14.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(shimmerColor)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(CartControlHeight)
                .clip(RoundedCornerShape(CartControlCorner))
                .background(shimmerColor),
            verticalAlignment = Alignment.CenterVertically
        ) {}
    }
}
