package com.agrodiary.data.local.entity

enum class ActivityLogType(val displayName: String) {
    ANIMAL_CREATED("Создано животное"),
    ANIMAL_UPDATED("Обновлено животное"),
    ANIMAL_DELETED("Удалено животное"),
    STAFF_CREATED("Создан сотрудник"),
    STAFF_UPDATED("Обновлен сотрудник"),
    STAFF_DELETED("Удален сотрудник")
}
