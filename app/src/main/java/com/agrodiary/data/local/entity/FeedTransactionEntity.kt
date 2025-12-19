package com.agrodiary.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "feed_transactions",
    foreignKeys = [
        ForeignKey(
            entity = FeedStockEntity::class,
            parentColumns = ["id"],
            childColumns = ["feedStockId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["feedStockId"]),
        Index(value = ["date"])
    ]
)
data class FeedTransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val feedStockId: Long,
    val type: TransactionType,
    val quantity: Double,
    val date: Long = System.currentTimeMillis(),
    val pricePerUnit: Double? = null,
    val notes: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
