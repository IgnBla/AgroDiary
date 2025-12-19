package com.agrodiary.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.agrodiary.data.local.entity.ProductTransactionEntity
import com.agrodiary.data.local.entity.TransactionType
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductTransactionDao {

    @Query("SELECT * FROM product_transactions ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<ProductTransactionEntity>>

    @Query("SELECT * FROM product_transactions WHERE productId = :productId ORDER BY date DESC")
    fun getTransactionsByProduct(productId: Long): Flow<List<ProductTransactionEntity>>

    @Query("SELECT * FROM product_transactions WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getTransactionsByDateRange(startDate: Long, endDate: Long): Flow<List<ProductTransactionEntity>>

    @Query("SELECT * FROM product_transactions WHERE type = :type ORDER BY date DESC")
    fun getTransactionsByType(type: TransactionType): Flow<List<ProductTransactionEntity>>

    @Query("SELECT * FROM product_transactions WHERE id = :id")
    suspend fun getTransactionById(id: Long): ProductTransactionEntity?

    @Query("SELECT * FROM product_transactions WHERE productId = :productId ORDER BY date DESC LIMIT :limit")
    fun getRecentTransactionsByProduct(productId: Long, limit: Int): Flow<List<ProductTransactionEntity>>

    @Query("SELECT SUM(totalPrice) FROM product_transactions WHERE type = 'SOLD' AND date BETWEEN :startDate AND :endDate")
    fun getTotalSalesInRange(startDate: Long, endDate: Long): Flow<Double?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: ProductTransactionEntity): Long

    @Delete
    suspend fun deleteTransaction(transaction: ProductTransactionEntity)

    @Query("DELETE FROM product_transactions WHERE id = :id")
    suspend fun deleteTransactionById(id: Long)
}
