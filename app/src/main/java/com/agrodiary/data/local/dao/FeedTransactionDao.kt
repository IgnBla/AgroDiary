package com.agrodiary.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.agrodiary.data.local.entity.FeedTransactionEntity
import com.agrodiary.data.local.entity.TransactionType
import kotlinx.coroutines.flow.Flow

@Dao
interface FeedTransactionDao {

    @Query("SELECT * FROM feed_transactions ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<FeedTransactionEntity>>

    @Query("SELECT * FROM feed_transactions WHERE feedStockId = :feedStockId ORDER BY date DESC")
    fun getTransactionsByFeed(feedStockId: Long): Flow<List<FeedTransactionEntity>>

    @Query("SELECT * FROM feed_transactions WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getTransactionsByDateRange(startDate: Long, endDate: Long): Flow<List<FeedTransactionEntity>>

    @Query("SELECT * FROM feed_transactions WHERE type = :type ORDER BY date DESC")
    fun getTransactionsByType(type: TransactionType): Flow<List<FeedTransactionEntity>>

    @Query("SELECT * FROM feed_transactions WHERE id = :id")
    suspend fun getTransactionById(id: Long): FeedTransactionEntity?

    @Query("SELECT * FROM feed_transactions WHERE feedStockId = :feedStockId ORDER BY date DESC LIMIT :limit")
    fun getRecentTransactionsByFeed(feedStockId: Long, limit: Int): Flow<List<FeedTransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: FeedTransactionEntity): Long

    @Delete
    suspend fun deleteTransaction(transaction: FeedTransactionEntity)

    @Query("DELETE FROM feed_transactions WHERE id = :id")
    suspend fun deleteTransactionById(id: Long)
}
