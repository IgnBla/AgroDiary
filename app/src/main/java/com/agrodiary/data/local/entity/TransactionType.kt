package com.agrodiary.data.local.entity
enum class TransactionType(val displayName: String) {
    INCOME("Приход"),
    EXPENSE("Расход"),
    PRODUCED("Произведено"),
    SOLD("Продано"),
    CONSUMED("Потреблено"),
    SPOILED("Испорчено")
}
