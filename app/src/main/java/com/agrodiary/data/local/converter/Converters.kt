package com.agrodiary.data.local.converter

import androidx.room.TypeConverter
import com.agrodiary.data.local.entity.ActivityLogType
import com.agrodiary.data.local.entity.AnimalStatus
import com.agrodiary.data.local.entity.AnimalType
import com.agrodiary.data.local.entity.FeedCategory
import com.agrodiary.data.local.entity.JournalEntryType
import com.agrodiary.data.local.entity.MeasureUnit
import com.agrodiary.data.local.entity.ProductCategory
import com.agrodiary.data.local.entity.StaffStatus
import com.agrodiary.data.local.entity.TaskPriority
import com.agrodiary.data.local.entity.TaskStatus
import com.agrodiary.data.local.entity.TransactionType

class Converters {

    // AnimalType converters
    @TypeConverter
    fun fromAnimalType(value: AnimalType): String = value.name

    @TypeConverter
    fun toAnimalType(value: String): AnimalType = AnimalType.valueOf(value)

    // AnimalStatus converters
    @TypeConverter
    fun fromAnimalStatus(value: AnimalStatus): String = value.name

    @TypeConverter
    fun toAnimalStatus(value: String): AnimalStatus = AnimalStatus.valueOf(value)

    // StaffStatus converters
    @TypeConverter
    fun fromStaffStatus(value: StaffStatus): String = value.name

    @TypeConverter
    fun toStaffStatus(value: String): StaffStatus = StaffStatus.valueOf(value)

    // TaskPriority converters
    @TypeConverter
    fun fromTaskPriority(value: TaskPriority): String = value.name

    @TypeConverter
    fun toTaskPriority(value: String): TaskPriority = TaskPriority.valueOf(value)

    // TaskStatus converters
    @TypeConverter
    fun fromTaskStatus(value: TaskStatus): String = value.name

    @TypeConverter
    fun toTaskStatus(value: String): TaskStatus = TaskStatus.valueOf(value)

    // JournalEntryType converters
    @TypeConverter
    fun fromJournalEntryType(value: JournalEntryType): String = value.name

    @TypeConverter
    fun toJournalEntryType(value: String): JournalEntryType = JournalEntryType.valueOf(value)

    // FeedCategory converters
    @TypeConverter
    fun fromFeedCategory(value: FeedCategory): String = value.name

    @TypeConverter
    fun toFeedCategory(value: String): FeedCategory = FeedCategory.valueOf(value)

    // ProductCategory converters
    @TypeConverter
    fun fromProductCategory(value: ProductCategory): String = value.name

    @TypeConverter
    fun toProductCategory(value: String): ProductCategory = ProductCategory.valueOf(value)

    // TransactionType converters
    @TypeConverter
    fun fromTransactionType(value: TransactionType): String = value.name

    @TypeConverter
    fun toTransactionType(value: String): TransactionType = TransactionType.valueOf(value)

    // MeasureUnit converters
    @TypeConverter
    fun fromMeasureUnit(value: MeasureUnit): String = value.name

    @TypeConverter
    fun toMeasureUnit(value: String): MeasureUnit = MeasureUnit.valueOf(value)

    // ActivityLogType converters
    @TypeConverter
    fun fromActivityLogType(value: ActivityLogType): String = value.name

    @TypeConverter
    fun toActivityLogType(value: String): ActivityLogType = ActivityLogType.valueOf(value)
}
