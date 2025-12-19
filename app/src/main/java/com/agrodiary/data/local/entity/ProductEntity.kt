package com.agrodiary.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "products",
    indices = [
        Index(value = ["category"])
    ]
)
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val category: ProductCategory,
    val currentQuantity: Double = 0.0,
    val unit: MeasureUnit = MeasureUnit.KILOGRAM,
    val pricePerUnit: Double? = null,
    val productionDate: Long? = null,
    val expirationDate: Long? = null,
    val notes: String? = null,
    val lastUpdated: Long = System.currentTimeMillis()
)
