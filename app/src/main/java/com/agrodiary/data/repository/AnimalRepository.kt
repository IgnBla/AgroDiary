package com.agrodiary.data.repository

import com.agrodiary.data.local.dao.AnimalDao
import com.agrodiary.data.local.entity.ActivityLogEntity
import com.agrodiary.data.local.entity.ActivityLogType
import com.agrodiary.data.local.entity.AnimalEntity
import com.agrodiary.data.local.entity.AnimalStatus
import com.agrodiary.data.local.entity.AnimalType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnimalRepository @Inject constructor(
    private val animalDao: AnimalDao,
    private val activityLogRepository: ActivityLogRepository
) {
    fun getAllAnimals(): Flow<List<AnimalEntity>> = animalDao.getAllAnimals()

    fun getAnimalsByStatus(status: AnimalStatus): Flow<List<AnimalEntity>> =
        animalDao.getAnimalsByStatus(status)

    fun getAnimalsByType(type: AnimalType): Flow<List<AnimalEntity>> =
        animalDao.getAnimalsByType(type)

    fun getAnimalsByTypeAndStatus(type: AnimalType, status: AnimalStatus): Flow<List<AnimalEntity>> =
        animalDao.getAnimalsByTypeAndStatus(type, status)

    suspend fun getAnimalById(id: Long): AnimalEntity? = animalDao.getAnimalById(id)

    fun getAnimalByIdFlow(id: Long): Flow<AnimalEntity?> = animalDao.getAnimalByIdFlow(id)

    fun searchAnimals(query: String): Flow<List<AnimalEntity>> = animalDao.searchAnimals(query)

    fun getAnimalCountByStatus(status: AnimalStatus): Flow<Int> =
        animalDao.getAnimalCountByStatus(status)

    fun getTotalAnimalCount(): Flow<Int> = animalDao.getTotalAnimalCount()

    suspend fun insertAnimal(animal: AnimalEntity): Long {
        val id = animalDao.insertAnimal(animal)
        logActivity(
            type = ActivityLogType.ANIMAL_CREATED,
            details = animal.name,
            entityId = id
        )
        return id
    }

    suspend fun updateAnimal(animal: AnimalEntity) {
        val updatedAnimal = animal.copy(updatedAt = System.currentTimeMillis())
        animalDao.updateAnimal(updatedAnimal)
        logActivity(
            type = ActivityLogType.ANIMAL_UPDATED,
            details = updatedAnimal.name,
            entityId = updatedAnimal.id
        )
    }

    suspend fun deleteAnimal(animal: AnimalEntity) {
        animalDao.deleteAnimal(animal)
        logActivity(
            type = ActivityLogType.ANIMAL_DELETED,
            details = animal.name,
            entityId = animal.id
        )
    }

    suspend fun deleteAnimalById(id: Long) {
        val existing = animalDao.getAnimalById(id)
        animalDao.deleteAnimalById(id)
        logActivity(
            type = ActivityLogType.ANIMAL_DELETED,
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
                entityType = ENTITY_TYPE_ANIMAL,
                entityId = entityId
            )
        )
    }

    companion object {
        private const val ENTITY_TYPE_ANIMAL = "animal"
    }
}
