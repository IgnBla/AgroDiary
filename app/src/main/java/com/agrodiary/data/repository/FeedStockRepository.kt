package com.agrodiary.data.repository

import com.agrodiary.data.local.dao.FeedStockDao
import com.agrodiary.data.local.entity.FeedCategory
import com.agrodiary.data.local.entity.FeedStockEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeedStockRepository @Inject constructor(
    private val feedStockDao: FeedStockDao
) {
    fun getAllFeedStocks(): Flow<List<FeedStockEntity>> = feedStockDao.getAllFeedStocks()

    fun getFeedStocksByCategory(category: FeedCategory): Flow<List<FeedStockEntity>> =
        feedStockDao.getFeedStocksByCategory(category)

    fun getLowStockFeeds(): Flow<List<FeedStockEntity>> = feedStockDao.getLowStockFeeds()

    suspend fun getFeedStockById(id: Long): FeedStockEntity? = feedStockDao.getFeedStockById(id)

    fun getFeedStockByIdFlow(id: Long): Flow<FeedStockEntity?> =
        feedStockDao.getFeedStockByIdFlow(id)

    fun searchFeedStocks(query: String): Flow<List<FeedStockEntity>> =
        feedStockDao.searchFeedStocks(query)

    fun getLowStockCount(): Flow<Int> = feedStockDao.getLowStockCount()

    suspend fun insertFeedStock(feedStock: FeedStockEntity): Long =
        feedStockDao.insertFeedStock(feedStock)

    suspend fun updateFeedStock(feedStock: FeedStockEntity) =
        feedStockDao.updateFeedStock(feedStock.copy(lastUpdated = System.currentTimeMillis()))

    suspend fun deleteFeedStock(feedStock: FeedStockEntity) =
        feedStockDao.deleteFeedStock(feedStock)

    suspend fun deleteFeedStockById(id: Long) = feedStockDao.deleteFeedStockById(id)

    suspend fun addQuantity(id: Long, quantity: Double) =
        feedStockDao.addQuantity(id, quantity)

    suspend fun subtractQuantity(id: Long, quantity: Double) =
        feedStockDao.subtractQuantity(id, quantity)
}
