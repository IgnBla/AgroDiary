package com.agrodiary.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.agrodiary.data.local.entity.StaffEntity
import com.agrodiary.data.local.entity.StaffStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface StaffDao {

    @Query("SELECT * FROM staff ORDER BY name ASC")
    fun getAllStaff(): Flow<List<StaffEntity>>

    @Query("SELECT * FROM staff WHERE status = :status ORDER BY name ASC")
    fun getStaffByStatus(status: StaffStatus): Flow<List<StaffEntity>>

    @Query("SELECT * FROM staff WHERE status = 'ACTIVE' ORDER BY name ASC")
    fun getActiveStaff(): Flow<List<StaffEntity>>

    @Query("SELECT * FROM staff WHERE id = :id")
    suspend fun getStaffById(id: Long): StaffEntity?

    @Query("SELECT * FROM staff WHERE id = :id")
    fun getStaffByIdFlow(id: Long): Flow<StaffEntity?>

    @Query("SELECT * FROM staff WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchStaff(query: String): Flow<List<StaffEntity>>

    @Query("SELECT COUNT(*) FROM staff WHERE status = :status")
    fun getStaffCountByStatus(status: StaffStatus): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStaff(staff: StaffEntity): Long

    @Update
    suspend fun updateStaff(staff: StaffEntity)

    @Delete
    suspend fun deleteStaff(staff: StaffEntity)

    @Query("DELETE FROM staff WHERE id = :id")
    suspend fun deleteStaffById(id: Long)
}
