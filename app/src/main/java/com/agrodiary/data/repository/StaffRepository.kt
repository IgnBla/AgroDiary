package com.agrodiary.data.repository

import com.agrodiary.data.local.dao.StaffDao
import com.agrodiary.data.local.entity.ActivityLogEntity
import com.agrodiary.data.local.entity.ActivityLogType
import com.agrodiary.data.local.entity.StaffEntity
import com.agrodiary.data.local.entity.StaffStatus
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StaffRepository @Inject constructor(
    private val staffDao: StaffDao,
    private val activityLogRepository: ActivityLogRepository
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

    suspend fun insertStaff(staff: StaffEntity): Long {
        val id = staffDao.insertStaff(staff)
        logActivity(
            type = ActivityLogType.STAFF_CREATED,
            details = staff.name,
            entityId = id
        )
        return id
    }

    suspend fun updateStaff(staff: StaffEntity) {
        val updatedStaff = staff.copy(updatedAt = System.currentTimeMillis())
        staffDao.updateStaff(updatedStaff)
        logActivity(
            type = ActivityLogType.STAFF_UPDATED,
            details = updatedStaff.name,
            entityId = updatedStaff.id
        )
    }

    suspend fun deleteStaff(staff: StaffEntity) {
        staffDao.deleteStaff(staff)
        logActivity(
            type = ActivityLogType.STAFF_DELETED,
            details = staff.name,
            entityId = staff.id
        )
    }

    suspend fun deleteStaffById(id: Long) {
        val existing = staffDao.getStaffById(id)
        staffDao.deleteStaffById(id)
        logActivity(
            type = ActivityLogType.STAFF_DELETED,
            details = existing?.name ?: "ID: $id",
            entityId = id
        )
    }

    private suspend fun logActivity(
        type: ActivityLogType,
        details: String?,
        entityId: Long?
    ) {
        activityLogRepository.insertLog(
            ActivityLogEntity(
                type = type,
                details = details,
                entityType = ENTITY_TYPE_STAFF,
                entityId = entityId
            )
        )
    }

    companion object {
        private const val ENTITY_TYPE_STAFF = "staff"
    }
}
