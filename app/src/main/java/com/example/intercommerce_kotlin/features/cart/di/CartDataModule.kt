package com.example.intercommerce_kotlin.features.cart.di

import com.example.intercommerce_kotlin.features.cart.data.local.datasource.CartLocalDataSource
import com.example.intercommerce_kotlin.features.cart.data.local.datasource.CartLocalDataSourceImpl
import com.example.intercommerce_kotlin.features.cart.data.repository.CartRepositoryImpl
import com.example.intercommerce_kotlin.features.cart.domain.repository.CartRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class CartDataModule {
    @Binds
    abstract fun bindCartLocalDataSource(impl: CartLocalDataSourceImpl): CartLocalDataSource

    @Binds
    abstract fun bindCartRepository(impl: CartRepositoryImpl): CartRepository
}
