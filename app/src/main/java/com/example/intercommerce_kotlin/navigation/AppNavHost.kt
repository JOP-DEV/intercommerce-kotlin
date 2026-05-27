package com.example.intercommerce_kotlin.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.foundation.background
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.intercommerce_kotlin.features.cart.presentation.CartRoute
import com.example.intercommerce_kotlin.features.products.presentation.catalog.ProductCatalogRoute
import com.example.intercommerce_kotlin.features.products.presentation.detail.ProductDetailRoute
import com.example.intercommerce_kotlin.features.products.presentation.favorites.FavoritesRoute

@Composable
fun AppNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val bottomTabs = listOf(
        BottomNavItem(AppDestination.Catalog.route, "Inicio", Icons.Outlined.Home),
        BottomNavItem(AppDestination.Favorites.route, "Favoritos", Icons.Outlined.FavoriteBorder),
        BottomNavItem(AppDestination.Profile.route, "Perfil", Icons.Outlined.PersonOutline)
    )
    val shouldShowBottomBar = bottomTabs.any { it.route == currentRoute }

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (shouldShowBottomBar) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .navigationBarsPadding()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color(0xFFEDEEF2))
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(72.dp)
                            .padding(horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        bottomTabs.forEach { item ->
                            val selected = currentRoute == item.route
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        navController.navigate(item.route) {
                                            popUpTo(AppDestination.Catalog.route) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
                                    .padding(vertical = 4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.label,
                                    tint = if (selected) Color(0xFFFF5A1F) else Color(0xFF6B7080),
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(
                                    text = item.label,
                                    color = if (selected) Color(0xFFFF5A1F) else Color(0xFF6B7080),
                                    fontSize = 12.sp,
                                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            startDestination = AppDestination.Catalog.route
        ) {
            composable(
                route = AppDestination.Catalog.route,
                enterTransition = {
                    fadeIn(animationSpec = tween(220))
                },
                exitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { -it / 6 },
                        animationSpec = tween(220)
                    ) + fadeOut(animationSpec = tween(220))
                },
                popEnterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { -it / 6 },
                        animationSpec = tween(220)
                    ) + fadeIn(animationSpec = tween(220))
                },
                popExitTransition = {
                    fadeOut(animationSpec = tween(180))
                }
            ) {
                ProductCatalogRoute(
                    onProductClick = { productId ->
                        navController.navigate(AppDestination.ProductDetail.createRoute(productId))
                    },
                    onCartClick = {
                        navController.navigate(AppDestination.Cart.route)
                    }
                )
            }

            composable(route = AppDestination.Favorites.route) {
                FavoritesRoute(
                    onProductClick = { productId ->
                        navController.navigate(AppDestination.ProductDetail.createRoute(productId))
                    },
                    onCartClick = {
                        navController.navigate(AppDestination.Cart.route)
                    }
                )
            }

            composable(route = AppDestination.Profile.route) {
                PlaceholderScreen(label = "Perfil")
            }

            composable(
                route = AppDestination.ProductDetail.route,
                arguments = listOf(
                    navArgument(AppDestination.ProductDetail.ARG_PRODUCT_ID) {
                        type = NavType.IntType
                    }
                ),
                enterTransition = {
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(260)
                    ) + fadeIn(animationSpec = tween(260))
                },
                exitTransition = {
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(220)
                    ) + fadeOut(animationSpec = tween(220))
                },
                popEnterTransition = {
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(220)
                    ) + fadeIn(animationSpec = tween(220))
                },
                popExitTransition = {
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(220)
                    ) + fadeOut(animationSpec = tween(220))
                }
            ) { backStackEntry ->
                val productId =
                    backStackEntry.arguments?.getInt(AppDestination.ProductDetail.ARG_PRODUCT_ID) ?: return@composable
                ProductDetailRoute(
                    productId = productId,
                    onBack = { navController.popBackStack() },
                    onGoToCart = { navController.navigate(AppDestination.Cart.route) }
                )
            }

            composable(
                route = AppDestination.Cart.route,
                enterTransition = {
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(240)
                    ) + fadeIn(animationSpec = tween(240))
                },
                exitTransition = {
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(220)
                    ) + fadeOut(animationSpec = tween(220))
                },
                popEnterTransition = {
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(220)
                    ) + fadeIn(animationSpec = tween(220))
                },
                popExitTransition = {
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(220)
                    ) + fadeOut(animationSpec = tween(220))
                }
            ) {
                CartRoute(onBack = { navController.popBackStack() })
            }
        }
    }
}

private data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@Composable
private fun PlaceholderScreen(label: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = label)
    }
}
