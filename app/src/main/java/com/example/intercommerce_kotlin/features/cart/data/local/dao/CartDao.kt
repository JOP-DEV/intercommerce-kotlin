package com.example.intercommerce_kotlin.features.cart.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.intercommerce_kotlin.features.cart.data.local.entity.CartItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: CartItemEntity)

    @Query("SELECT * FROM cart_items WHERE productId = :productId LIMIT 1")
    suspend fun getByProductId(productId: Int): CartItemEntity?

    @Query("UPDATE cart_items SET quantity = :quantity, updatedAt = :updatedAt WHERE productId = :productId")
    suspend fun updateQuantity(productId: Int, quantity: Int, updatedAt: Long)

    @Query("DELETE FROM cart_items WHERE productId = :productId")
    suspend fun removeByProductId(productId: Int)

    @Query("DELETE FROM cart_items")
    suspend fun clear()

    @Query("SELECT * FROM cart_items ORDER BY updatedAt DESC")
    fun observeCartItems(): Flow<List<CartItemEntity>>
}
