package com.agrodiary.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "staff")
data class StaffEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val position: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val hireDate: Long? = null,
    val salary: Double? = null,
    val status: StaffStatus = StaffStatus.ACTIVE,
    val photoUri: String? = null,
    val notes: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
