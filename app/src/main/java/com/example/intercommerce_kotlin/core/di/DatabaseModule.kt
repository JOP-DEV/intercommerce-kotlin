package com.example.intercommerce_kotlin.core.di

import android.content.Context
import androidx.room.Room
import com.example.intercommerce_kotlin.core.database.AppDatabase
import com.example.intercommerce_kotlin.features.products.data.local.dao.ProductDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "intercommerce.db"
        ).build()
    }

    @Provides
    fun provideProductDao(database: AppDatabase): ProductDao = database.productDao()
}
