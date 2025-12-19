package com.agrodiary.data.local.entity

enum class TaskPriority(val displayName: String, val level: Int) {
    LOW("Низкий", 1),
    MEDIUM("Средний", 2),
    HIGH("Высокий", 3),
    URGENT("Срочный", 4)
}
