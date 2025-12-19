package com.agrodiary.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "product_transactions",
    foreignKeys = [
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["productId"]),
        Index(value = ["date"])
    ]
)
data class ProductTransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val productId: Long,
    val type: TransactionType,
    val quantity: Double,
    val date: Long = System.currentTimeMillis(),
    val pricePerUnit: Double? = null,
    val totalPrice: Double? = null,
    val buyerName: String? = null,
    val notes: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
