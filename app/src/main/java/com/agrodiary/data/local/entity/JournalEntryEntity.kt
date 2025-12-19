package com.agrodiary.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "journal_entries",
    foreignKeys = [
        ForeignKey(
            entity = AnimalEntity::class,
            parentColumns = ["id"],
            childColumns = ["relatedAnimalId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = StaffEntity::class,
            parentColumns = ["id"],
            childColumns = ["relatedStaffId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["relatedAnimalId"]),
        Index(value = ["relatedStaffId"]),
        Index(value = ["date"]),
        Index(value = ["entryType"])
    ]
)
data class JournalEntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: Long = System.currentTimeMillis(),
    val entryType: JournalEntryType,
    val description: String,
    val relatedAnimalId: Long? = null,
    val relatedStaffId: Long? = null,
    val amount: Double? = null,
    val notes: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
