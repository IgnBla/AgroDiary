package com.agrodiary.data.repository

import com.agrodiary.data.local.dao.JournalDao
import com.agrodiary.data.local.entity.JournalEntryEntity
import com.agrodiary.data.local.entity.JournalEntryType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JournalRepository @Inject constructor(
    private val journalDao: JournalDao
) {
    fun getAllEntries(): Flow<List<JournalEntryEntity>> = journalDao.getAllEntries()

    fun getEntriesByDateRange(startDate: Long, endDate: Long): Flow<List<JournalEntryEntity>> =
        journalDao.getEntriesByDateRange(startDate, endDate)

    fun getEntriesByType(type: JournalEntryType): Flow<List<JournalEntryEntity>> =
        journalDao.getEntriesByType(type)

    fun getEntriesByAnimal(animalId: Long): Flow<List<JournalEntryEntity>> =
        journalDao.getEntriesByAnimal(animalId)

    fun getEntriesByStaff(staffId: Long): Flow<List<JournalEntryEntity>> =
        journalDao.getEntriesByStaff(staffId)

    suspend fun getEntryById(id: Long): JournalEntryEntity? = journalDao.getEntryById(id)

    fun getEntryByIdFlow(id: Long): Flow<JournalEntryEntity?> = journalDao.getEntryByIdFlow(id)

    fun searchEntries(query: String): Flow<List<JournalEntryEntity>> =
        journalDao.searchEntries(query)

    fun getRecentEntries(limit: Int = 10): Flow<List<JournalEntryEntity>> =
        journalDao.getRecentEntries(limit)

    fun getEntryCountByType(type: JournalEntryType): Flow<Int> =
        journalDao.getEntryCountByType(type)

    suspend fun insertEntry(entry: JournalEntryEntity): Long = journalDao.insertEntry(entry)

    suspend fun updateEntry(entry: JournalEntryEntity) = journalDao.updateEntry(entry)

    suspend fun deleteEntry(entry: JournalEntryEntity) = journalDao.deleteEntry(entry)

    suspend fun deleteEntryById(id: Long) = journalDao.deleteEntryById(id)
}
