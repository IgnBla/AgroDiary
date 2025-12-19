package com.agrodiary.data.local.entity

enum class JournalEntryType(val displayName: String) {
    FEEDING("Кормление"),
    HEALTH_CHECK("Осмотр здоровья"),
    VACCINATION("Вакцинация"),
    BREEDING("Разведение"),
    WEIGHT_MEASURE("Взвешивание"),
    PURCHASE("Покупка"),
    SALE("Продажа"),
    TREATMENT("Лечение"),
    BIRTH("Рождение"),
    TASK_COMPLETED("Задача выполнена"),
    OTHER("Другое")
}
