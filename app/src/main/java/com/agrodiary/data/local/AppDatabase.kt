package com.agrodiary.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.agrodiary.data.local.converter.Converters
import com.agrodiary.data.local.dao.ActivityLogDao
import com.agrodiary.data.local.dao.AnimalDao
import com.agrodiary.data.local.dao.FeedStockDao
import com.agrodiary.data.local.dao.FeedTransactionDao
import com.agrodiary.data.local.dao.JournalDao
import com.agrodiary.data.local.dao.ProductDao
import com.agrodiary.data.local.dao.ProductTransactionDao
import com.agrodiary.data.local.dao.StaffDao
import com.agrodiary.data.local.dao.TaskDao
import com.agrodiary.data.local.dao.UserDao
import com.agrodiary.data.local.entity.ActivityLogEntity
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
        ActivityLogEntity::class,
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
    version = 3,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun activityLogDao(): ActivityLogDao
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

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "CREATE TABLE IF NOT EXISTS `users` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `username` TEXT NOT NULL, `passwordHash` TEXT NOT NULL, `displayName` TEXT NOT NULL, `farmName` TEXT, `email` TEXT, `phone` TEXT, `photoUri` TEXT, `isActive` INTEGER NOT NULL, `lastLoginAt` INTEGER, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL)"
                )
                database.execSQL(
                    "CREATE UNIQUE INDEX IF NOT EXISTS `index_users_username` ON `users` (`username`)"
                )
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE `users` ADD COLUMN `passwordSalt` TEXT")
                database.execSQL(
                    "CREATE TABLE IF NOT EXISTS `activity_logs` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `type` TEXT NOT NULL, `details` TEXT, `entityType` TEXT, `entityId` INTEGER, `createdAt` INTEGER NOT NULL)"
                )
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_activity_logs_createdAt` ON `activity_logs` (`createdAt`)"
                )
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_activity_logs_entityType` ON `activity_logs` (`entityType`)"
                )
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_activity_logs_entityId` ON `activity_logs` (`entityId`)"
                )
            }
        }
    }
}
