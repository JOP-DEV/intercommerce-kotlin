package com.example.intercommerce_kotlin.features.cart.data.mapper

import com.example.intercommerce_kotlin.features.cart.data.local.entity.CartItemEntity
import com.example.intercommerce_kotlin.features.cart.domain.model.CartItem

fun CartItemEntity.toDomain(): CartItem = CartItem(
    productId = productId,
    title = title,
    price = price,
    thumbnail = thumbnail,
    quantity = quantity,
    discountPercentage = discountPercentage
)
