package com.agrodiary.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.agrodiary.data.local.converter.Converters
import com.agrodiary.data.local.dao.AnimalDao
import com.agrodiary.data.local.dao.FeedStockDao
import com.agrodiary.data.local.dao.FeedTransactionDao
import com.agrodiary.data.local.dao.JournalDao
import com.agrodiary.data.local.dao.ProductDao
import com.agrodiary.data.local.dao.ProductTransactionDao
import com.agrodiary.data.local.dao.StaffDao
import com.agrodiary.data.local.dao.TaskDao
import com.agrodiary.data.local.dao.UserDao
import com.agrodiary.data.local.entity.AnimalEntity
import com.agrodiary.data.local.entity.FeedStockEntity
import com.agrodiary.data.local.entity.FeedTransactionEntity
import com.agrodiary.data.local.entity.JournalEntryEntity
import com.agrodiary.data.local.entity.ProductEntity
import com.agrodiary.data.local.entity.ProductTransactionEntity
import com.agrodiary.data.local.entity.StaffEntity
import com.agrodiary.data.local.entity.TaskEntity
import com.agrodiary.data.local.entity.UserEntity

@Database(
    entities = [
        AnimalEntity::class,
        StaffEntity::class,
        TaskEntity::class,
        JournalEntryEntity::class,
        FeedStockEntity::class,
        FeedTransactionEntity::class,
        ProductEntity::class,
        ProductTransactionEntity::class,
        UserEntity::class
    ],
    version = 2,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun animalDao(): AnimalDao
    abstract fun staffDao(): StaffDao
    abstract fun taskDao(): TaskDao
    abstract fun journalDao(): JournalDao
    abstract fun feedStockDao(): FeedStockDao
    abstract fun feedTransactionDao(): FeedTransactionDao
    abstract fun productDao(): ProductDao
    abstract fun productTransactionDao(): ProductTransactionDao
    abstract fun userDao(): UserDao

    companion object {
        const val DATABASE_NAME = "agrodiary_database"
    }
}
