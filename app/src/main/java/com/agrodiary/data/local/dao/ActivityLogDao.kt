package com.agrodiary.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.agrodiary.data.local.entity.ActivityLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityLogDao {

    @Query("SELECT * FROM activity_logs ORDER BY createdAt DESC")
    fun getAllLogs(): Flow<List<ActivityLogEntity>>

    @Insert
    suspend fun insertLog(log: ActivityLogEntity): Long

    @Query("DELETE FROM activity_logs")
    suspend fun clearAll()
}
