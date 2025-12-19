package com.agrodiary.data.local.entity

enum class TransactionType(val displayName: String) {
    // For feed
    INCOME("Приход"),
    EXPENSE("Расход"),

    // For products
    PRODUCED("Произведено"),
    SOLD("Продано"),
    CONSUMED("Потреблено"),
    SPOILED("Испорчено")
}
