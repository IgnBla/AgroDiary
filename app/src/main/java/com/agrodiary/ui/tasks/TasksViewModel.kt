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
        repository.getAllTasks(),
        _searchQuery,
        _selectedStatus,
        _selectedPriority
    ) { allTasks, query, status, priority ->
        allTasks
            .filter { task ->
                query.isBlank() || task.title.contains(query, ignoreCase = true) ||
                    task.description?.contains(query, ignoreCase = true) == true
            }
            .filter { task -> status == null || task.status == status }
            .filter { task -> priority == null || task.priority == priority }
    }.stateIn(
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
