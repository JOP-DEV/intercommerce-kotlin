package com.example.intercommerce_kotlin.core.di

import com.example.intercommerce_kotlin.core.network.ConnectivityObserver
import com.example.intercommerce_kotlin.core.network.NetworkConnectivityObserver
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ConnectivityModule {

    @Binds
    @Singleton
    abstract fun bindConnectivityObserver(
        observer: NetworkConnectivityObserver
    ): ConnectivityObserver
}
