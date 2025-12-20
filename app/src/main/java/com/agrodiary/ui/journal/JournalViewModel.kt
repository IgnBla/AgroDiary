package com.agrodiary.ui.journal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agrodiary.data.local.entity.AnimalEntity
import com.agrodiary.data.local.entity.JournalEntryEntity
import com.agrodiary.data.local.entity.JournalEntryType
import com.agrodiary.data.local.entity.StaffEntity
import com.agrodiary.data.repository.AnimalRepository
import com.agrodiary.data.repository.JournalRepository
import com.agrodiary.data.repository.StaffRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JournalViewModel @Inject constructor(
    private val repository: JournalRepository,
    private val animalRepository: AnimalRepository,
    private val staffRepository: StaffRepository
) : ViewModel() {

    private val _selectedType = MutableStateFlow<JournalEntryType?>(null)
    val selectedType = _selectedType.asStateFlow()
    
    private val _selectedDate = MutableStateFlow<Long?>(null)
    val selectedDate = _selectedDate.asStateFlow()

    private val _uiState = MutableStateFlow(JournalUiState())
    val uiState = _uiState.asStateFlow()

    // Dropdown data sources
    val animals = animalRepository.getAllAnimals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val staff = staffRepository.getAllStaff()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val entries = combine(
        repository.getAllEntries(),
        _selectedType,
        _selectedDate
    ) { entries, type, date ->
        var filtered = entries
        if (type != null) {
            filtered = filtered.filter { it.entryType == type }
        }
        if (date != null) {
             filtered = filtered.filter { entry -> 
                 val entryDay = entry.date / (24 * 60 * 60 * 1000)
                 val selectedDay = date / (24 * 60 * 60 * 1000)
                 entryDay == selectedDay
             }
        }
        filtered
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun setSelectedType(type: JournalEntryType?) {
        _selectedType.value = type
    }
    
    fun setSelectedDate(date: Long?) {
        _selectedDate.value = date
    }

    fun addEntry(entry: JournalEntryEntity, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                repository.insertEntry(entry)
                onSuccess()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun updateEntry(entry: JournalEntryEntity, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                repository.updateEntry(entry)
                onSuccess()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
    
    fun deleteEntry(entry: JournalEntryEntity) {
        viewModelScope.launch {
            try {
                repository.deleteEntry(entry)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    suspend fun getEntryById(id: Long): JournalEntryEntity? {
        return repository.getEntryById(id)
    }

    suspend fun getAnimalById(id: Long): AnimalEntity? {
        return animalRepository.getAnimalById(id)
    }

    suspend fun getStaffById(id: Long): StaffEntity? {
        return staffRepository.getStaffById(id)
    }
}

data class JournalUiState(
    val isLoading: Boolean = false,
    val error: String? = null
)