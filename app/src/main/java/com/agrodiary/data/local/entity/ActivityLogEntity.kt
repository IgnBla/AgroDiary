package com.agrodiary.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "activity_logs",
    indices = [
        Index(value = ["createdAt"]),
        Index(value = ["entityType"]),
        Index(value = ["entityId"])
    ]
)
data class ActivityLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: ActivityLogType,
    val details: String? = null,
    val entityType: String? = null,
    val entityId: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)
