package com.example.intercommerce_kotlin.features.products.data.local.datasource

import com.example.intercommerce_kotlin.features.products.data.local.dao.ProductDao
import com.example.intercommerce_kotlin.features.products.data.local.entity.ProductEntity
import javax.inject.Inject

class ProductLocalDataSourceImpl @Inject constructor(
    private val dao: ProductDao
) : ProductLocalDataSource {
    override suspend fun upsertProducts(products: List<ProductEntity>) {
        dao.insertAll(products)
    }

    override suspend fun getPagedProducts(limit: Int, offset: Int): List<ProductEntity> =
        dao.getPagedProducts(limit, offset)

    override suspend fun searchProducts(query: String): List<ProductEntity> = dao.searchProducts(query)

    override suspend fun getProductById(id: Int): ProductEntity? = dao.getProductById(id)

    override suspend fun countProducts(): Int = dao.countProducts()
}
