package com.example.intercommerce_kotlin.features.products.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.intercommerce_kotlin.features.products.data.local.entity.ProductEntity

@Dao
interface ProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<ProductEntity>)

    @Query("SELECT * FROM products ORDER BY id LIMIT :limit OFFSET :offset")
    suspend fun getPagedProducts(limit: Int, offset: Int): List<ProductEntity>

    @Query("SELECT * FROM products WHERE title LIKE '%' || :query || '%' ORDER BY id")
    suspend fun searchProducts(query: String): List<ProductEntity>

    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    suspend fun getProductById(id: Int): ProductEntity?

    @Query("SELECT COUNT(id) FROM products")
    suspend fun countProducts(): Int
}
