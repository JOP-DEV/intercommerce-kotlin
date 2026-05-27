package com.example.intercommerce_kotlin.features.products.data.repository

import android.util.Log
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
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
    private companion object {
        const val TAG = "ProductRepository"
    }

    override suspend fun getProducts(page: Int, limit: Int): AppResult<PagedProducts> =
        withContext(ioDispatcher) {
            val skip = page * limit
            try {
                val remoteResponse = remoteDataSource.getProducts(limit = limit, skip = skip)
                Log.d(
                    TAG,
                    "GET /products success -> page=$page, limit=$limit, skip=$skip, total=${remoteResponse.total}, items=${remoteResponse.products.size}"
                )
                val now = System.currentTimeMillis()
                val entities = remoteResponse.products.map { dto ->
                    val isFavorite = localDataSource.getFavoriteStatus(dto.id) ?: false
                    dto.toEntity(now = now, isFavorite = isFavorite)
                }
                localDataSource.upsertProducts(entities)

                val localPage = localDataSource.getPagedProducts(limit = limit, offset = skip)
                Log.d(TAG, "Local page mapped -> page=$page, items=${localPage.size}")
                AppResult.Success(
                    PagedProducts(
                        items = localPage.map { it.toDomain() },
                        isOffline = false,
                        endReached = localPage.size < limit
                    )
                )
            } catch (throwable: Throwable) {
                Log.e(TAG, "GET /products failed -> page=$page, using fallback", throwable)
                val localPage = localDataSource.getPagedProducts(limit = limit, offset = skip)
                if (localPage.isNotEmpty()) {
                    Log.d(TAG, "Offline fallback hit -> page=$page, cachedItems=${localPage.size}")
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
        val normalizedQuery = query.trim()
        try {
            val remoteResponse = remoteDataSource.searchProducts(query)
            Log.d(
                TAG,
                "GET /products/search success -> query='$query', remoteItems=${remoteResponse.products.size}"
            )
            val entities = remoteResponse.products
                .filter { dto ->
                    dto.title.contains(normalizedQuery, ignoreCase = true)
                }
                .map { dto ->
                    val isFavorite = localDataSource.getFavoriteStatus(dto.id) ?: false
                    dto.toEntity(now = System.currentTimeMillis(), isFavorite = isFavorite)
                }
            localDataSource.upsertProducts(entities)
            AppResult.Success(entities.map { it.toDomain() })
        } catch (_: Throwable) {
            val cached = localDataSource.searchProducts(normalizedQuery)
            Log.d(TAG, "GET /products/search fallback -> query='$query', cachedItems=${cached.size}")
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
            Log.d(TAG, "GET /products/$id success -> title='${remote.title}'")
            val entity = remote.toEntity(
                now = System.currentTimeMillis(),
                isFavorite = localDataSource.getFavoriteStatus(id) ?: false
            )
            localDataSource.upsertProducts(listOf(entity))
            AppResult.Success(entity.toDomain())
        } catch (throwable: Throwable) {
            Log.e(TAG, "GET /products/$id failed -> localAvailable=${local != null}", throwable)
            if (local != null) {
                AppResult.Success(local.toDomain())
            } else {
                AppResult.Error(throwable.toAppError())
            }
        }
    }

    override suspend fun updateFavorite(productId: Int, isFavorite: Boolean) {
        withContext(ioDispatcher) {
            localDataSource.updateFavorite(productId, isFavorite)
        }
    }

    override fun observeFavoriteProducts(): Flow<List<Product>> =
        localDataSource.observeFavoriteProducts().map { entities ->
            entities.map { it.toDomain() }
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
