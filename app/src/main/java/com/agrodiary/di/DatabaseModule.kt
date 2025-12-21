package com.agrodiary.di

import android.content.Context
import androidx.room.Room
import com.agrodiary.data.local.AppDatabase
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
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
            .addMigrations(
                AppDatabase.MIGRATION_1_2,
                AppDatabase.MIGRATION_2_3
            )
            .build()
    }

    @Provides
    @Singleton
    fun provideActivityLogDao(database: AppDatabase): ActivityLogDao {
        return database.activityLogDao()
    }

    @Provides
    @Singleton
    fun provideAnimalDao(database: AppDatabase): AnimalDao {
        return database.animalDao()
    }

    @Provides
    @Singleton
    fun provideStaffDao(database: AppDatabase): StaffDao {
        return database.staffDao()
    }

    @Provides
    @Singleton
    fun provideTaskDao(database: AppDatabase): TaskDao {
        return database.taskDao()
    }

    @Provides
    @Singleton
    fun provideJournalDao(database: AppDatabase): JournalDao {
        return database.journalDao()
    }

    @Provides
    @Singleton
    fun provideFeedStockDao(database: AppDatabase): FeedStockDao {
        return database.feedStockDao()
    }

    @Provides
    @Singleton
    fun provideFeedTransactionDao(database: AppDatabase): FeedTransactionDao {
        return database.feedTransactionDao()
    }

    @Provides
    @Singleton
    fun provideProductDao(database: AppDatabase): ProductDao {
        return database.productDao()
    }

    @Provides
    @Singleton
    fun provideProductTransactionDao(database: AppDatabase): ProductTransactionDao {
        return database.productTransactionDao()
    }

    @Provides
    @Singleton
    fun provideUserDao(database: AppDatabase): UserDao {
        return database.userDao()
    }
}
