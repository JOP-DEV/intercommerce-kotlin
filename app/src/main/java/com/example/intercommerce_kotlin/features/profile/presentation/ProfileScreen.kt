package com.example.intercommerce_kotlin.features.profile.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

private val AccentOrange = Color(0xFFFF5A1F)

@Composable
fun ProfileRoute(
    onCartClick: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    ProfileScreen(
        state = state,
        onCartClick = onCartClick
    )
}

@Composable
private fun ProfileScreen(
    state: ProfileUiState,
    onCartClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7FA))
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 14.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        HeaderRow(cartItemsCount = state.cartCount, onCartClick = onCartClick)
        Spacer(modifier = Modifier.height(14.dp))
        ProfileTopCard()
        Spacer(modifier = Modifier.height(12.dp))
        MetricsRow(ordersCount = state.ordersCount, favoritesCount = state.favoritesCount, cartCount = state.cartCount)
        Spacer(modifier = Modifier.height(12.dp))
        OptionsCard()
        Spacer(modifier = Modifier.height(12.dp))
        LogoutButton()
        Spacer(modifier = Modifier.height(18.dp))
    }
}

@Composable
private fun HeaderRow(cartItemsCount: Int, onCartClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = com.example.intercommerce_kotlin.R.string.profile_title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Box {
            IconButton(onClick = onCartClick) {
                Icon(
                    imageVector = Icons.Outlined.ShoppingCart,
                    contentDescription = stringResource(id = com.example.intercommerce_kotlin.R.string.cd_cart),
                    tint = Color.Black
                )
            }
            if (cartItemsCount > 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(20.dp)
                        .background(AccentOrange, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = cartItemsCount.toString(),
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileTopCard() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(20.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .background(Color(0xFFFFEFE8), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(stringResource(id = com.example.intercommerce_kotlin.R.string.profile_initials), color = AccentOrange, fontWeight = FontWeight.Bold, fontSize = 28.sp)
        }
        Spacer(modifier = Modifier.size(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Jhon Doe",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "test@mail.com",
                color = Color(0xFF5D6470),
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(stringResource(id = com.example.intercommerce_kotlin.R.string.profile_tier), color = AccentOrange, style = MaterialTheme.typography.bodyMedium)
        }
        Text(stringResource(id = com.example.intercommerce_kotlin.R.string.profile_chevron), color = Color(0xFF6B7080), fontSize = 32.sp)
    }
}

@Composable
private fun MetricsRow(ordersCount: Int, favoritesCount: Int, cartCount: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        MetricCard(modifier = Modifier.weight(1f), icon = Icons.Outlined.ShoppingBag, value = ordersCount, label = stringResource(id = com.example.intercommerce_kotlin.R.string.profile_metric_orders))
        MetricCard(modifier = Modifier.weight(1f), icon = Icons.Outlined.PersonOutline, value = favoritesCount, label = stringResource(id = com.example.intercommerce_kotlin.R.string.profile_metric_favorites))
        MetricCard(modifier = Modifier.weight(1f), icon = Icons.Outlined.ShoppingCart, value = cartCount, label = stringResource(id = com.example.intercommerce_kotlin.R.string.profile_metric_in_cart))
    }
}

@Composable
private fun MetricCard(
    modifier: Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: Int,
    label: String
) {
    Column(
        modifier = modifier
            .background(Color.White, RoundedCornerShape(18.dp))
            .padding(vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .background(Color(0xFFFFEFE8), RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = AccentOrange)
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(value.toString(), color = AccentOrange, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Text(label, color = Color(0xFF5D6470), style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
private fun OptionsCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(20.dp))
            .padding(vertical = 8.dp)
    ) {
        OptionRow(
            icon = Icons.Outlined.ShoppingBag,
            title = stringResource(id = com.example.intercommerce_kotlin.R.string.profile_option_orders_title),
            subtitle = stringResource(id = com.example.intercommerce_kotlin.R.string.profile_option_orders_subtitle)
        )
        OptionRow(
            icon = Icons.Outlined.LocationOn,
            title = stringResource(id = com.example.intercommerce_kotlin.R.string.profile_option_addresses_title),
            subtitle = stringResource(id = com.example.intercommerce_kotlin.R.string.profile_option_addresses_subtitle)
        )
        OptionRow(
            icon = Icons.Outlined.NotificationsNone,
            title = stringResource(id = com.example.intercommerce_kotlin.R.string.profile_option_notifications_title),
            subtitle = stringResource(id = com.example.intercommerce_kotlin.R.string.profile_option_notifications_subtitle)
        )
        OptionRow(
            icon = Icons.Outlined.HelpOutline,
            title = stringResource(id = com.example.intercommerce_kotlin.R.string.profile_option_help_title),
            subtitle = stringResource(id = com.example.intercommerce_kotlin.R.string.profile_option_help_subtitle)
        )
    }
}

@Composable
private fun OptionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .background(Color(0xFFFFEFE8), RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = AccentOrange)
        }
        Spacer(modifier = Modifier.size(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
            Text(subtitle, color = Color(0xFF5D6470), style = MaterialTheme.typography.bodyLarge)
        }
        Text(stringResource(id = com.example.intercommerce_kotlin.R.string.profile_chevron), color = Color(0xFF6B7080), fontSize = 30.sp)
    }
}

@Composable
private fun LogoutButton() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, AccentOrange, RoundedCornerShape(20.dp))
            .padding(vertical = 14.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = Icons.Outlined.Logout, contentDescription = null, tint = AccentOrange)
        Spacer(modifier = Modifier.size(8.dp))
        Text(stringResource(id = com.example.intercommerce_kotlin.R.string.profile_logout), color = AccentOrange, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
    }
}
