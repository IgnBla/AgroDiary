package com.agrodiary.data.repository

import com.agrodiary.data.local.dao.ProductTransactionDao
import com.agrodiary.data.local.entity.ProductTransactionEntity
import com.agrodiary.data.local.entity.TransactionType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductTransactionRepository @Inject constructor(
    private val productTransactionDao: ProductTransactionDao
) {
    fun getAllTransactions(): Flow<List<ProductTransactionEntity>> =
        productTransactionDao.getAllTransactions()

    fun getTransactionsByProduct(productId: Long): Flow<List<ProductTransactionEntity>> =
        productTransactionDao.getTransactionsByProduct(productId)

    fun getTransactionsByDateRange(startDate: Long, endDate: Long): Flow<List<ProductTransactionEntity>> =
        productTransactionDao.getTransactionsByDateRange(startDate, endDate)

    fun getTransactionsByType(type: TransactionType): Flow<List<ProductTransactionEntity>> =
        productTransactionDao.getTransactionsByType(type)

    suspend fun getTransactionById(id: Long): ProductTransactionEntity? =
        productTransactionDao.getTransactionById(id)

    fun getRecentTransactionsByProduct(productId: Long, limit: Int = 10): Flow<List<ProductTransactionEntity>> =
        productTransactionDao.getRecentTransactionsByProduct(productId, limit)

    fun getTotalSalesInRange(startDate: Long, endDate: Long): Flow<Double?> =
        productTransactionDao.getTotalSalesInRange(startDate, endDate)

    suspend fun insertTransaction(transaction: ProductTransactionEntity): Long =
        productTransactionDao.insertTransaction(transaction)

    suspend fun deleteTransaction(transaction: ProductTransactionEntity) =
        productTransactionDao.deleteTransaction(transaction)

    suspend fun deleteTransactionById(id: Long) =
        productTransactionDao.deleteTransactionById(id)
}
