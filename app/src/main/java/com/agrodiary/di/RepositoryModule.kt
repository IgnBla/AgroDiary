package com.agrodiary.di

import com.agrodiary.data.local.dao.AnimalDao
import com.agrodiary.data.local.dao.FeedStockDao
import com.agrodiary.data.local.dao.FeedTransactionDao
import com.agrodiary.data.local.dao.JournalDao
import com.agrodiary.data.local.dao.ProductDao
import com.agrodiary.data.local.dao.ProductTransactionDao
import com.agrodiary.data.local.dao.StaffDao
import com.agrodiary.data.local.dao.TaskDao
import com.agrodiary.data.repository.AnimalRepository
import com.agrodiary.data.repository.FeedStockRepository
import com.agrodiary.data.repository.FeedTransactionRepository
import com.agrodiary.data.repository.JournalRepository
import com.agrodiary.data.repository.ProductRepository
import com.agrodiary.data.repository.ProductTransactionRepository
import com.agrodiary.data.repository.StaffRepository
import com.agrodiary.data.repository.TaskRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAnimalRepository(animalDao: AnimalDao): AnimalRepository {
        return AnimalRepository(animalDao)
    }

    @Provides
    @Singleton
    fun provideStaffRepository(staffDao: StaffDao): StaffRepository {
        return StaffRepository(staffDao)
    }

    @Provides
    @Singleton
    fun provideTaskRepository(taskDao: TaskDao): TaskRepository {
        return TaskRepository(taskDao)
    }

    @Provides
    @Singleton
    fun provideJournalRepository(journalDao: JournalDao): JournalRepository {
        return JournalRepository(journalDao)
    }

    @Provides
    @Singleton
    fun provideFeedStockRepository(feedStockDao: FeedStockDao): FeedStockRepository {
        return FeedStockRepository(feedStockDao)
    }

    @Provides
    @Singleton
    fun provideFeedTransactionRepository(feedTransactionDao: FeedTransactionDao): FeedTransactionRepository {
        return FeedTransactionRepository(feedTransactionDao)
    }

    @Provides
    @Singleton
    fun provideProductRepository(productDao: ProductDao): ProductRepository {
        return ProductRepository(productDao)
    }

    @Provides
    @Singleton
    fun provideProductTransactionRepository(productTransactionDao: ProductTransactionDao): ProductTransactionRepository {
        return ProductTransactionRepository(productTransactionDao)
    }
}
