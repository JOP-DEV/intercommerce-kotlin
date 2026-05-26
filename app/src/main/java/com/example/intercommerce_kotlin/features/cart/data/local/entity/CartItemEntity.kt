package com.example.intercommerce_kotlin.features.cart.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey val productId: Int,
    val title: String,
    val price: Double,
    val thumbnail: String,
    val quantity: Int,
    val discountPercentage: Double,
    val createdAt: Long,
    val updatedAt: Long
)
