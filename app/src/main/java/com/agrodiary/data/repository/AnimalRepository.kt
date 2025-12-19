package com.agrodiary.data.repository

import com.agrodiary.data.local.dao.AnimalDao
import com.agrodiary.data.local.entity.AnimalEntity
import com.agrodiary.data.local.entity.AnimalStatus
import com.agrodiary.data.local.entity.AnimalType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnimalRepository @Inject constructor(
    private val animalDao: AnimalDao
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

    suspend fun insertAnimal(animal: AnimalEntity): Long = animalDao.insertAnimal(animal)

    suspend fun updateAnimal(animal: AnimalEntity) = animalDao.updateAnimal(
        animal.copy(updatedAt = System.currentTimeMillis())
    )

    suspend fun deleteAnimal(animal: AnimalEntity) = animalDao.deleteAnimal(animal)

    suspend fun deleteAnimalById(id: Long) = animalDao.deleteAnimalById(id)
}
