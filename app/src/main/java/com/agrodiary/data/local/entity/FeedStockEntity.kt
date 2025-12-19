package com.agrodiary.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "feed_stocks",
    indices = [
        Index(value = ["category"])
    ]
)
data class FeedStockEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val category: FeedCategory,
    val currentQuantity: Double = 0.0,
    val unit: MeasureUnit = MeasureUnit.KILOGRAM,
    val minQuantity: Double = 0.0,
    val pricePerUnit: Double? = null,
    val notes: String? = null,
    val lastUpdated: Long = System.currentTimeMillis()
)
