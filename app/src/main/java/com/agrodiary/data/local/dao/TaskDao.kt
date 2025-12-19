package com.agrodiary.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.agrodiary.data.local.entity.TaskEntity
import com.agrodiary.data.local.entity.TaskPriority
import com.agrodiary.data.local.entity.TaskStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks ORDER BY dueDate ASC, priority DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE status = :status ORDER BY dueDate ASC, priority DESC")
    fun getTasksByStatus(status: TaskStatus): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE priority = :priority ORDER BY dueDate ASC")
    fun getTasksByPriority(priority: TaskPriority): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE assignedStaffId = :staffId ORDER BY dueDate ASC")
    fun getTasksByStaff(staffId: Long): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE animalId = :animalId ORDER BY dueDate ASC")
    fun getTasksByAnimal(animalId: Long): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE status IN ('NEW', 'IN_PROGRESS') AND dueDate <= :date ORDER BY dueDate ASC")
    fun getOverdueTasks(date: Long): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE status IN ('NEW', 'IN_PROGRESS') AND dueDate BETWEEN :now AND :futureDate ORDER BY dueDate ASC")
    fun getUpcomingTasks(now: Long, futureDate: Long): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: Long): TaskEntity?

    @Query("SELECT * FROM tasks WHERE id = :id")
    fun getTaskByIdFlow(id: Long): Flow<TaskEntity?>

    @Query("SELECT * FROM tasks WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' ORDER BY dueDate ASC")
    fun searchTasks(query: String): Flow<List<TaskEntity>>

    @Query("SELECT COUNT(*) FROM tasks WHERE status = :status")
    fun getTaskCountByStatus(status: TaskStatus): Flow<Int>

    @Query("SELECT COUNT(*) FROM tasks WHERE status IN ('NEW', 'IN_PROGRESS')")
    fun getActiveTaskCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteTaskById(id: Long)

    @Query("UPDATE tasks SET status = :status, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateTaskStatus(id: Long, status: TaskStatus, updatedAt: Long = System.currentTimeMillis())
}
