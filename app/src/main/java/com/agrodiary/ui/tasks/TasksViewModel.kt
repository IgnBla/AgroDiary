package com.agrodiary.ui.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agrodiary.data.local.entity.TaskEntity
import com.agrodiary.data.local.entity.TaskPriority
import com.agrodiary.data.local.entity.TaskStatus
import com.agrodiary.data.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val repository: TaskRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedStatus = MutableStateFlow<TaskStatus?>(null)
    val selectedStatus = _selectedStatus.asStateFlow()
    
    private val _selectedPriority = MutableStateFlow<TaskPriority?>(null)
    val selectedPriority = _selectedPriority.asStateFlow()

    private val _uiState = MutableStateFlow(TasksUiState())
    val uiState = _uiState.asStateFlow()

    val tasks: StateFlow<List<TaskEntity>> = combine(
        _searchQuery,
        _selectedStatus,
        _selectedPriority
    ) { query, status, priority ->
        Triple(query, status, priority)
    }.flatMapLatest { (query, status, priority) ->
        // Note: Ideally the repository should handle complex filtering
        // For simplicity, we might filter in memory or extend repository
        // Here assuming repository has getAllTasks() and we filter in memory if repo doesn't support all combos
        // Or better, let's assume we use getAllTasks and filter here for now if simple, 
        // or use specific repository methods if available. 
        // Given the plan says "CRUD + getByStatus + getByStaff + getOverdue", 
        // we might not have a full filter query. 
        // Let's use getAll and filter in memory for prototype speed, or chain flows.
        repository.getAllTasks() 
    }.combine(_searchQuery) { tasks, query ->
        if (query.isBlank()) tasks else tasks.filter { it.title.contains(query, ignoreCase = true) || it.description?.contains(query, ignoreCase = true) == true }
    }.combine(_selectedStatus) { tasks, status ->
        if (status == null) tasks else tasks.filter { it.status == status }
    }.combine(_selectedPriority) { tasks, priority ->
        if (priority == null) tasks else tasks.filter { it.priority == priority }
    }
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedStatus(status: TaskStatus?) {
        _selectedStatus.value = status
    }
    
    fun setSelectedPriority(priority: TaskPriority?) {
        _selectedPriority.value = priority
    }

    fun addTask(task: TaskEntity, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                repository.insertTask(task)
                onSuccess()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun updateTask(task: TaskEntity, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                repository.updateTask(task)
                onSuccess()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
             try {
                repository.deleteTask(task)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
    
    suspend fun getTaskById(id: Long): TaskEntity? {
        return repository.getTaskById(id)
    }
    
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

data class TasksUiState(
    val isLoading: Boolean = false,
    val error: String? = null
)
