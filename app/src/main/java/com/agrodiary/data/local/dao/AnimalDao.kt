package com.agrodiary.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.agrodiary.data.local.entity.AnimalEntity
import com.agrodiary.data.local.entity.AnimalStatus
import com.agrodiary.data.local.entity.AnimalType
import kotlinx.coroutines.flow.Flow

@Dao
interface AnimalDao {

    @Query("SELECT * FROM animals ORDER BY name ASC")
    fun getAllAnimals(): Flow<List<AnimalEntity>>

    @Query("SELECT * FROM animals WHERE status = :status ORDER BY name ASC")
    fun getAnimalsByStatus(status: AnimalStatus): Flow<List<AnimalEntity>>

    @Query("SELECT * FROM animals WHERE type = :type ORDER BY name ASC")
    fun getAnimalsByType(type: AnimalType): Flow<List<AnimalEntity>>

    @Query("SELECT * FROM animals WHERE type = :type AND status = :status ORDER BY name ASC")
    fun getAnimalsByTypeAndStatus(type: AnimalType, status: AnimalStatus): Flow<List<AnimalEntity>>

    @Query("SELECT * FROM animals WHERE id = :id")
    suspend fun getAnimalById(id: Long): AnimalEntity?

    @Query("SELECT * FROM animals WHERE id = :id")
    fun getAnimalByIdFlow(id: Long): Flow<AnimalEntity?>

    @Query("SELECT * FROM animals WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchAnimals(query: String): Flow<List<AnimalEntity>>

    @Query("SELECT COUNT(*) FROM animals WHERE status = :status")
    fun getAnimalCountByStatus(status: AnimalStatus): Flow<Int>

    @Query("SELECT COUNT(*) FROM animals")
    fun getTotalAnimalCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnimal(animal: AnimalEntity): Long

    @Update
    suspend fun updateAnimal(animal: AnimalEntity)

    @Delete
    suspend fun deleteAnimal(animal: AnimalEntity)

    @Query("DELETE FROM animals WHERE id = :id")
    suspend fun deleteAnimalById(id: Long)
}
