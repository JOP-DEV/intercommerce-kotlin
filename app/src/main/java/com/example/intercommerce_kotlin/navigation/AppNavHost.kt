package com.example.intercommerce_kotlin.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.intercommerce_kotlin.features.cart.presentation.CartRoute
import com.example.intercommerce_kotlin.features.products.presentation.catalog.ProductCatalogRoute
import com.example.intercommerce_kotlin.features.products.presentation.detail.ProductDetailRoute

@Composable
fun AppNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        modifier = modifier,
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
