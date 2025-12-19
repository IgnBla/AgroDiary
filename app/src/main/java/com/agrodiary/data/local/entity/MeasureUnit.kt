package com.agrodiary.data.local.entity

enum class MeasureUnit(val displayName: String, val shortName: String) {
    KILOGRAM("Килограмм", "кг"),
    GRAM("Грамм", "г"),
    LITER("Литр", "л"),
    PIECE("Штука", "шт"),
    BAG("Мешок", "мешок"),
    TON("Тонна", "т"),
    DOZEN("Десяток", "дес")
}
