package com.agrodiary.data.repository

import com.agrodiary.data.local.dao.StaffDao
import com.agrodiary.data.local.entity.StaffEntity
import com.agrodiary.data.local.entity.StaffStatus
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StaffRepository @Inject constructor(
    private val staffDao: StaffDao
) {
    fun getAllStaff(): Flow<List<StaffEntity>> = staffDao.getAllStaff()

    fun getStaffByStatus(status: StaffStatus): Flow<List<StaffEntity>> =
        staffDao.getStaffByStatus(status)

    fun getActiveStaff(): Flow<List<StaffEntity>> = staffDao.getActiveStaff()

    suspend fun getStaffById(id: Long): StaffEntity? = staffDao.getStaffById(id)

    fun getStaffByIdFlow(id: Long): Flow<StaffEntity?> = staffDao.getStaffByIdFlow(id)

    fun searchStaff(query: String): Flow<List<StaffEntity>> = staffDao.searchStaff(query)

    fun getStaffCountByStatus(status: StaffStatus): Flow<Int> =
        staffDao.getStaffCountByStatus(status)

    suspend fun insertStaff(staff: StaffEntity): Long = staffDao.insertStaff(staff)

    suspend fun updateStaff(staff: StaffEntity) = staffDao.updateStaff(
        staff.copy(updatedAt = System.currentTimeMillis())
    )

    suspend fun deleteStaff(staff: StaffEntity) = staffDao.deleteStaff(staff)

    suspend fun deleteStaffById(id: Long) = staffDao.deleteStaffById(id)
}
