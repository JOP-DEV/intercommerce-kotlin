package com.example.intercommerce_kotlin.features.products.di

import com.example.intercommerce_kotlin.features.products.data.local.datasource.ProductLocalDataSource
import com.example.intercommerce_kotlin.features.products.data.local.datasource.ProductLocalDataSourceImpl
import com.example.intercommerce_kotlin.features.products.data.remote.datasource.ProductRemoteDataSource
import com.example.intercommerce_kotlin.features.products.data.remote.datasource.ProductRemoteDataSourceImpl
import com.example.intercommerce_kotlin.features.products.data.remote.service.ProductApi
import com.example.intercommerce_kotlin.features.products.data.repository.ProductRepositoryImpl
import com.example.intercommerce_kotlin.features.products.domain.repository.ProductRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProductServiceModule {
    @Provides
    @Singleton
    fun provideProductApi(retrofit: Retrofit): ProductApi = retrofit.create(ProductApi::class.java)
}

@Module
@InstallIn(SingletonComponent::class)
abstract class ProductDataModule {

    @Binds
    abstract fun bindProductRemoteDataSource(
        impl: ProductRemoteDataSourceImpl
    ): ProductRemoteDataSource

    @Binds
    abstract fun bindProductLocalDataSource(
        impl: ProductLocalDataSourceImpl
    ): ProductLocalDataSource

    @Binds
    abstract fun bindProductRepository(
        impl: ProductRepositoryImpl
    ): ProductRepository
}
