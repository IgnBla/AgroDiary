package com.agrodiary.data.repository

import com.agrodiary.data.local.dao.TaskDao
import com.agrodiary.data.local.entity.TaskEntity
import com.agrodiary.data.local.entity.TaskPriority
import com.agrodiary.data.local.entity.TaskStatus
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepository @Inject constructor(
    private val taskDao: TaskDao
) {
    fun getAllTasks(): Flow<List<TaskEntity>> = taskDao.getAllTasks()

    fun getTasksByStatus(status: TaskStatus): Flow<List<TaskEntity>> =
        taskDao.getTasksByStatus(status)

    fun getTasksByPriority(priority: TaskPriority): Flow<List<TaskEntity>> =
        taskDao.getTasksByPriority(priority)

    fun getTasksByStaff(staffId: Long): Flow<List<TaskEntity>> =
        taskDao.getTasksByStaff(staffId)

    fun getTasksByAnimal(animalId: Long): Flow<List<TaskEntity>> =
        taskDao.getTasksByAnimal(animalId)

    fun getOverdueTasks(date: Long = System.currentTimeMillis()): Flow<List<TaskEntity>> =
        taskDao.getOverdueTasks(date)

    fun getUpcomingTasks(daysAhead: Int = 3): Flow<List<TaskEntity>> {
        val now = System.currentTimeMillis()
        val futureDate = now + (daysAhead * 24 * 60 * 60 * 1000L)
        return taskDao.getUpcomingTasks(now, futureDate)
    }

    suspend fun getTaskById(id: Long): TaskEntity? = taskDao.getTaskById(id)

    fun getTaskByIdFlow(id: Long): Flow<TaskEntity?> = taskDao.getTaskByIdFlow(id)

    fun searchTasks(query: String): Flow<List<TaskEntity>> = taskDao.searchTasks(query)

    fun getTaskCountByStatus(status: TaskStatus): Flow<Int> =
        taskDao.getTaskCountByStatus(status)

    fun getActiveTaskCount(): Flow<Int> = taskDao.getActiveTaskCount()

    suspend fun insertTask(task: TaskEntity): Long = taskDao.insertTask(task)

    suspend fun updateTask(task: TaskEntity) = taskDao.updateTask(
        task.copy(updatedAt = System.currentTimeMillis())
    )

    suspend fun deleteTask(task: TaskEntity) = taskDao.deleteTask(task)

    suspend fun deleteTaskById(id: Long) = taskDao.deleteTaskById(id)

    suspend fun updateTaskStatus(id: Long, status: TaskStatus) {
        taskDao.updateTaskStatus(id, status)
    }

    suspend fun completeTask(id: Long) {
        taskDao.updateTaskStatus(id, TaskStatus.COMPLETED)
    }
}
