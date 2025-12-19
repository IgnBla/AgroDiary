package com.agrodiary.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "animals")
data class AnimalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val type: AnimalType,
    val breed: String? = null,
    val birthDate: Long? = null,
    val gender: String? = null,
    val weight: Float? = null,
    val status: AnimalStatus = AnimalStatus.ACTIVE,
    val notes: String? = null,
    val photoUri: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
