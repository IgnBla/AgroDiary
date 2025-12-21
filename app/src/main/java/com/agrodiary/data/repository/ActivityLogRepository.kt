package com.agrodiary.data.repository

import com.agrodiary.data.local.dao.ActivityLogDao
import com.agrodiary.data.local.entity.ActivityLogEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActivityLogRepository @Inject constructor(
    private val activityLogDao: ActivityLogDao
) {
    fun getAllLogs(): Flow<List<ActivityLogEntity>> = activityLogDao.getAllLogs()

    suspend fun insertLog(log: ActivityLogEntity): Long = activityLogDao.insertLog(log)

    suspend fun clearAll() = activityLogDao.clearAll()
}
