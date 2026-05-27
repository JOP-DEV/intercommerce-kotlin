package com.example.intercommerce_kotlin.navigation

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController

sealed class AppDestination(val route: String) {
    data object Catalog : AppDestination("catalog")
    data object Favorites : AppDestination("favorites")
    data object Profile : AppDestination("profile")
    data object Cart : AppDestination("cart")

    data object ProductDetail : AppDestination("productDetail/{productId}") {
        const val ARG_PRODUCT_ID = "productId"
        private const val ROUTE_BASE = "productDetail"

        data class Args(val productId: Int)

        fun routeFor(args: Args): String {
            require(args.productId > 0) { "productId must be > 0" }
            return "$ROUTE_BASE/${args.productId}"
        }

        fun parse(backStackEntry: NavBackStackEntry): Args {
            val productId = requireNotNull(backStackEntry.arguments?.getInt(ARG_PRODUCT_ID)) {
                "Missing argument: $ARG_PRODUCT_ID"
            }
            require(productId > 0) { "Invalid productId: $productId" }
            return Args(productId)
        }
    }
}

fun NavController.navigateToCatalog() = navigate(AppDestination.Catalog.route)

fun NavController.navigateToFavorites() = navigate(AppDestination.Favorites.route)

fun NavController.navigateToProfile() = navigate(AppDestination.Profile.route)

fun NavController.navigateToCart() = navigate(AppDestination.Cart.route)

fun NavController.navigateToProductDetail(productId: Int) =
    navigate(AppDestination.ProductDetail.routeFor(AppDestination.ProductDetail.Args(productId)))
