package com.agrodiary.data.local.entity

enum class ActivityLogType(val displayName: String) {
    ANIMAL_CREATED("Animal created"),
    ANIMAL_UPDATED("Animal updated"),
    ANIMAL_DELETED("Animal deleted"),
    STAFF_CREATED("Staff created"),
    STAFF_UPDATED("Staff updated"),
    STAFF_DELETED("Staff deleted")
}
