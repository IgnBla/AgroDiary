package com.agrodiary.data.repository

import com.agrodiary.data.local.dao.FeedTransactionDao
import com.agrodiary.data.local.entity.FeedTransactionEntity
import com.agrodiary.data.local.entity.TransactionType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeedTransactionRepository @Inject constructor(
    private val feedTransactionDao: FeedTransactionDao
) {
    fun getAllTransactions(): Flow<List<FeedTransactionEntity>> =
        feedTransactionDao.getAllTransactions()

    fun getTransactionsByFeed(feedStockId: Long): Flow<List<FeedTransactionEntity>> =
        feedTransactionDao.getTransactionsByFeed(feedStockId)

    fun getTransactionsByDateRange(startDate: Long, endDate: Long): Flow<List<FeedTransactionEntity>> =
        feedTransactionDao.getTransactionsByDateRange(startDate, endDate)

    fun getTransactionsByType(type: TransactionType): Flow<List<FeedTransactionEntity>> =
        feedTransactionDao.getTransactionsByType(type)

    suspend fun getTransactionById(id: Long): FeedTransactionEntity? =
        feedTransactionDao.getTransactionById(id)

    fun getRecentTransactionsByFeed(feedStockId: Long, limit: Int = 10): Flow<List<FeedTransactionEntity>> =
        feedTransactionDao.getRecentTransactionsByFeed(feedStockId, limit)

    suspend fun insertTransaction(transaction: FeedTransactionEntity): Long =
        feedTransactionDao.insertTransaction(transaction)

    suspend fun deleteTransaction(transaction: FeedTransactionEntity) =
        feedTransactionDao.deleteTransaction(transaction)

    suspend fun deleteTransactionById(id: Long) =
        feedTransactionDao.deleteTransactionById(id)
}
