package com.example.intercommerce_kotlin.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.intercommerce_kotlin.features.cart.data.local.dao.CartDao
import com.example.intercommerce_kotlin.features.cart.data.local.entity.CartItemEntity
import com.example.intercommerce_kotlin.features.products.data.local.dao.ProductDao
import com.example.intercommerce_kotlin.features.products.data.local.entity.ProductEntity

@Database(
    entities = [ProductEntity::class, CartItemEntity::class],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun cartDao(): CartDao
}
