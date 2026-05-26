package com.example.intercommerce_kotlin.navigation

sealed class AppDestination(val route: String) {
    data object Catalog : AppDestination("catalog")
    data object Cart : AppDestination("cart")
    data object ProductDetail : AppDestination("productDetail/{productId}") {
        const val ARG_PRODUCT_ID = "productId"
        fun createRoute(productId: Int): String = "productDetail/$productId"
    }
}
