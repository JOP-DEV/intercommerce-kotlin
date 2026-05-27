package com.example.intercommerce_kotlin.features.products.domain.model

data class Product(
    val id: Int,
    val title: String,
    val description: String,
    val price: Double,
    val discountPercentage: Double,
    val rating: Double,
    val stock: Int,
    val brand: String?,
    val category: String,
    val isFavorite: Boolean,
    val thumbnail: String,
    val images: List<String>
)
