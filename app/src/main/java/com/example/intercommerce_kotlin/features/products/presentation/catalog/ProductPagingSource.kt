package com.example.intercommerce_kotlin.features.products.presentation.catalog

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.intercommerce_kotlin.core.result.AppResult
import com.example.intercommerce_kotlin.features.products.domain.model.Product
import com.example.intercommerce_kotlin.features.products.domain.usecase.GetProductsUseCase

class ProductPagingSource(
    private val getProductsUseCase: GetProductsUseCase,
    private val onPageOfflineStatus: (Boolean) -> Unit
) : PagingSource<Int, Product>() {

    override fun getRefreshKey(state: PagingState<Int, Product>): Int? {
        return state.anchorPosition?.let { anchor ->
            val anchorPage = state.closestPageToPosition(anchor)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Product> {
        val page = params.key ?: 0

        return when (val result = getProductsUseCase(page)) {
            is AppResult.Success -> {
                onPageOfflineStatus(result.data.isOffline)
                val items = result.data.items
                val nextKey = if (result.data.endReached || items.isEmpty()) null else page + 1
                LoadResult.Page(
                    data = items,
                    prevKey = if (page == 0) null else page - 1,
                    nextKey = nextKey
                )
            }

            is AppResult.Error -> LoadResult.Error(Throwable(result.error.toString()))
        }
    }
}
