package com.agrodiary.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.agrodiary.data.local.entity.FeedCategory
import com.agrodiary.data.local.entity.FeedStockEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FeedStockDao {

    @Query("SELECT * FROM feed_stocks ORDER BY name ASC")
    fun getAllFeedStocks(): Flow<List<FeedStockEntity>>

    @Query("SELECT * FROM feed_stocks WHERE category = :category ORDER BY name ASC")
    fun getFeedStocksByCategory(category: FeedCategory): Flow<List<FeedStockEntity>>

    @Query("SELECT * FROM feed_stocks WHERE currentQuantity <= minQuantity ORDER BY name ASC")
    fun getLowStockFeeds(): Flow<List<FeedStockEntity>>

    @Query("SELECT * FROM feed_stocks WHERE id = :id")
    suspend fun getFeedStockById(id: Long): FeedStockEntity?

    @Query("SELECT * FROM feed_stocks WHERE id = :id")
    fun getFeedStockByIdFlow(id: Long): Flow<FeedStockEntity?>

    @Query("SELECT * FROM feed_stocks WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchFeedStocks(query: String): Flow<List<FeedStockEntity>>

    @Query("SELECT COUNT(*) FROM feed_stocks WHERE currentQuantity <= minQuantity")
    fun getLowStockCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeedStock(feedStock: FeedStockEntity): Long

    @Update
    suspend fun updateFeedStock(feedStock: FeedStockEntity)

    @Delete
    suspend fun deleteFeedStock(feedStock: FeedStockEntity)

    @Query("DELETE FROM feed_stocks WHERE id = :id")
    suspend fun deleteFeedStockById(id: Long)

    @Query("UPDATE feed_stocks SET currentQuantity = currentQuantity + :quantity, lastUpdated = :updatedAt WHERE id = :id")
    suspend fun addQuantity(id: Long, quantity: Double, updatedAt: Long = System.currentTimeMillis())

    @Query("UPDATE feed_stocks SET currentQuantity = currentQuantity - :quantity, lastUpdated = :updatedAt WHERE id = :id")
    suspend fun subtractQuantity(id: Long, quantity: Double, updatedAt: Long = System.currentTimeMillis())
}
