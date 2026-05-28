package com.example.intercommerce_kotlin.features.products.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val description: String,
    val price: Double,
    val discountPercentage: Double,
    val rating: Double,
    val stock: Int,
    val brand: String?,
    val category: String,
    val shippingInformation: String,
    val returnPolicy: String,
    val isFavorite: Boolean,
    val thumbnail: String,
    val images: String,
    val lastUpdatedAt: Long
)
