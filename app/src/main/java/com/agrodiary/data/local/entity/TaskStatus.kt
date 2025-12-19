package com.agrodiary.data.local.entity

enum class TaskStatus(val displayName: String) {
    NEW("Новая"),
    IN_PROGRESS("В работе"),
    COMPLETED("Выполнена"),
    CANCELLED("Отменена")
}
