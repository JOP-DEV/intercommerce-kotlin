package com.example.intercommerce_kotlin.features.products.data.mapper

import com.example.intercommerce_kotlin.features.products.data.local.entity.ProductEntity
import com.example.intercommerce_kotlin.features.products.data.remote.dto.ProductDto
import com.example.intercommerce_kotlin.features.products.domain.model.Product

fun ProductDto.toEntity(
    now: Long,
    isFavorite: Boolean = false
): ProductEntity = ProductEntity(
    id = id,
    title = title,
    description = description,
    price = price,
    discountPercentage = discountPercentage,
    rating = rating,
    stock = stock,
    brand = brand,
    category = category,
    isFavorite = isFavorite,
    thumbnail = thumbnail,
    images = images.joinToString(separator = "|"),
    lastUpdatedAt = now
)

fun ProductEntity.toDomain(): Product = Product(
    id = id,
    title = title,
    description = description,
    price = price,
    discountPercentage = discountPercentage,
    rating = rating,
    stock = stock,
    brand = brand,
    category = category,
    isFavorite = isFavorite,
    thumbnail = thumbnail,
    images = if (images.isBlank()) emptyList() else images.split("|")
)
