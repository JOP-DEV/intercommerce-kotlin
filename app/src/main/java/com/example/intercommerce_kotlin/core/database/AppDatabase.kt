package com.example.intercommerce_kotlin.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.intercommerce_kotlin.features.products.data.local.dao.ProductDao
import com.example.intercommerce_kotlin.features.products.data.local.entity.ProductEntity

@Database(
    entities = [ProductEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
}
