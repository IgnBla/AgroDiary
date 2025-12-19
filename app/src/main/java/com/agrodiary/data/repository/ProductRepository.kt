package com.agrodiary.data.repository

import com.agrodiary.data.local.dao.ProductDao
import com.agrodiary.data.local.entity.ProductCategory
import com.agrodiary.data.local.entity.ProductEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepository @Inject constructor(
    private val productDao: ProductDao
) {
    fun getAllProducts(): Flow<List<ProductEntity>> = productDao.getAllProducts()

    fun getProductsByCategory(category: ProductCategory): Flow<List<ProductEntity>> =
        productDao.getProductsByCategory(category)

    fun getExpiringProducts(daysAhead: Int = 7): Flow<List<ProductEntity>> {
        val futureDate = System.currentTimeMillis() + (daysAhead * 24 * 60 * 60 * 1000L)
        return productDao.getExpiringProducts(futureDate)
    }

    suspend fun getProductById(id: Long): ProductEntity? = productDao.getProductById(id)

    fun getProductByIdFlow(id: Long): Flow<ProductEntity?> = productDao.getProductByIdFlow(id)

    fun searchProducts(query: String): Flow<List<ProductEntity>> =
        productDao.searchProducts(query)

    suspend fun insertProduct(product: ProductEntity): Long = productDao.insertProduct(product)

    suspend fun updateProduct(product: ProductEntity) =
        productDao.updateProduct(product.copy(lastUpdated = System.currentTimeMillis()))

    suspend fun deleteProduct(product: ProductEntity) = productDao.deleteProduct(product)

    suspend fun deleteProductById(id: Long) = productDao.deleteProductById(id)

    suspend fun addQuantity(id: Long, quantity: Double) = productDao.addQuantity(id, quantity)

    suspend fun subtractQuantity(id: Long, quantity: Double) =
        productDao.subtractQuantity(id, quantity)
}
