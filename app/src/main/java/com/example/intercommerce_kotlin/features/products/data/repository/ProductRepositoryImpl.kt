package com.example.intercommerce_kotlin.features.products.data.repository

import com.example.intercommerce_kotlin.core.dispatchers.IoDispatcher
import com.example.intercommerce_kotlin.core.error.AppError
import com.example.intercommerce_kotlin.core.result.AppResult
import com.example.intercommerce_kotlin.features.products.data.local.datasource.ProductLocalDataSource
import com.example.intercommerce_kotlin.features.products.data.mapper.toDomain
import com.example.intercommerce_kotlin.features.products.data.mapper.toEntity
import com.example.intercommerce_kotlin.features.products.data.remote.datasource.ProductRemoteDataSource
import com.example.intercommerce_kotlin.features.products.domain.model.PagedProducts
import com.example.intercommerce_kotlin.features.products.domain.model.Product
import com.example.intercommerce_kotlin.features.products.domain.repository.ProductRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val remoteDataSource: ProductRemoteDataSource,
    private val localDataSource: ProductLocalDataSource,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ProductRepository {

    override suspend fun getProducts(page: Int, limit: Int): AppResult<PagedProducts> =
        withContext(ioDispatcher) {
            val skip = page * limit
            try {
                val remoteResponse = remoteDataSource.getProducts(limit = limit, skip = skip)
                localDataSource.upsertProducts(remoteResponse.products.map { it.toEntity(System.currentTimeMillis()) })

                val localPage = localDataSource.getPagedProducts(limit = limit, offset = skip)
                AppResult.Success(
                    PagedProducts(
                        items = localPage.map { it.toDomain() },
                        isOffline = false,
                        endReached = localPage.size < limit
                    )
                )
            } catch (throwable: Throwable) {
                val localPage = localDataSource.getPagedProducts(limit = limit, offset = skip)
                if (localPage.isNotEmpty()) {
                    AppResult.Success(
                        PagedProducts(
                            items = localPage.map { it.toDomain() },
                            isOffline = true,
                            endReached = localPage.size < limit
                        )
                    )
                } else {
                    AppResult.Error(throwable.toAppError())
                }
            }
        }

    override suspend fun searchProducts(query: String): AppResult<List<Product>> = withContext(ioDispatcher) {
        try {
            val remoteResponse = remoteDataSource.searchProducts(query)
            val entities = remoteResponse.products.map { it.toEntity(System.currentTimeMillis()) }
            localDataSource.upsertProducts(entities)
            AppResult.Success(entities.map { it.toDomain() })
        } catch (_: Throwable) {
            val cached = localDataSource.searchProducts(query)
            if (cached.isNotEmpty()) {
                AppResult.Success(cached.map { it.toDomain() })
            } else {
                AppResult.Error(AppError.NetworkUnavailable)
            }
        }
    }

    override suspend fun getProductDetail(id: Int): AppResult<Product> = withContext(ioDispatcher) {
        val local = localDataSource.getProductById(id)
        try {
            val remote = remoteDataSource.getProductDetail(id)
            val entity = remote.toEntity(System.currentTimeMillis())
            localDataSource.upsertProducts(listOf(entity))
            AppResult.Success(entity.toDomain())
        } catch (throwable: Throwable) {
            if (local != null) {
                AppResult.Success(local.toDomain())
            } else {
                AppResult.Error(throwable.toAppError())
            }
        }
    }

    private fun Throwable.toAppError(): AppError = when (this) {
        is SocketTimeoutException -> AppError.Timeout
        is IOException -> AppError.NetworkUnavailable
        is SerializationException -> AppError.Serialization
        is HttpException -> when (code()) {
            404 -> AppError.NotFound
            in 500..599 -> AppError.Server
            else -> AppError.Unknown(this)
        }
        else -> AppError.Unknown(this)
    }
}
