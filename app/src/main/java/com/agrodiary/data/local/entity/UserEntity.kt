package com.agrodiary.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity for storing user authentication data.
 * Supports local authentication with PIN code or password.
 */
@Entity(
    tableName = "users",
    indices = [Index(value = ["username"], unique = true)]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val username: String,
    val passwordHash: String,
    val displayName: String,
    val farmName: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val photoUri: String? = null,
    val isActive: Boolean = true,
    val lastLoginAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
