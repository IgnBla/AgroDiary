package com.agrodiary.ui.staff
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agrodiary.data.local.entity.StaffEntity
import com.agrodiary.data.local.entity.StaffStatus
import com.agrodiary.data.repository.StaffRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class StaffViewModel @Inject constructor(
    private val repository: StaffRepository
) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    private val _selectedStatus = MutableStateFlow<StaffStatus?>(null)
    val selectedStatus: StateFlow<StaffStatus?> = _selectedStatus.asStateFlow()
    private val _uiState = MutableStateFlow(StaffUiState())
    val uiState: StateFlow<StaffUiState> = _uiState.asStateFlow()
    @OptIn(ExperimentalCoroutinesApi::class)
    val staff: StateFlow<List<StaffEntity>> = combine(
        _searchQuery,
        _selectedStatus
    ) { query, status ->
        Pair(query, status)
    }.flatMapLatest { (query, status) ->
        when {
            query.isNotBlank() -> repository.searchStaff(query)
            status != null -> repository.getStaffByStatus(status)
            else -> repository.getAllStaff()
        }
    }.catch { exception ->
        _uiState.update { it.copy(error = exception.message) }
        emit(emptyList())
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }
    fun setSelectedStatus(status: StaffStatus?) {
        _selectedStatus.value = status
    }
    fun clearFilters() {
        _searchQuery.value = ""
        _selectedStatus.value = null
    }
    suspend fun getStaffById(id: Long): StaffEntity? {
        return try {
            repository.getStaffById(id)
        } catch (e: Exception) {
            _uiState.update {
                it.copy(error = "Ошибка получения сотрудника: ${e.message}")
            }
            null
        }
    }
    fun getStaffByIdFlow(id: Long): StateFlow<StaffEntity?> {
        return repository.getStaffByIdFlow(id)
            .catch { exception ->
                _uiState.update {
                    it.copy(error = "Ошибка получения сотрудника: ${exception.message}")
                }
                emit(null)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = null
            )
    }
    fun getActiveStaff(): StateFlow<List<StaffEntity>> {
        return repository.getActiveStaff()
            .catch { exception ->
                _uiState.update {
                    it.copy(error = "Ошибка загрузки активных сотрудников: ${exception.message}")
                }
                emit(emptyList())
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }
    fun addStaff(staff: StaffEntity, onSuccess: ((Long) -> Unit)? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val id = repository.insertStaff(staff)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        successMessage = "Сотрудник ${staff.name} успешно добавлен"
                    )
                }
                onSuccess?.invoke(id)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Ошибка добавления сотрудника: ${e.message}"
                    )
                }
            }
        }
    }
    fun updateStaff(staff: StaffEntity, onSuccess: (() -> Unit)? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                repository.updateStaff(staff)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        successMessage = "Сотрудник ${staff.name} обновлен"
                    )
                }
                onSuccess?.invoke()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Ошибка обновления сотрудника: ${e.message}"
                    )
                }
            }
        }
    }
    fun deleteStaff(
        staff: StaffEntity,
        onSuccess: (() -> Unit)? = null,
        showSuccessMessage: Boolean = true
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                repository.deleteStaff(staff)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        successMessage = if (showSuccessMessage) {
                            "Сотрудник ${staff.name} удален"
                        } else {
                            null
                        }
                    )
                }
                onSuccess?.invoke()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Ошибка удаления сотрудника: ${e.message}"
                    )
                }
            }
        }
    }
    fun deleteStaffById(
        id: Long,
        onSuccess: (() -> Unit)? = null,
        showSuccessMessage: Boolean = true
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                repository.deleteStaffById(id)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        successMessage = if (showSuccessMessage) {
                            "Сотрудник удален"
                        } else {
                            null
                        }
                    )
                }
                onSuccess?.invoke()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Ошибка удаления сотрудника: ${e.message}"
                    )
                }
            }
        }
    }
    fun getStaffCountByStatus(status: StaffStatus): StateFlow<Int> {
        return repository.getStaffCountByStatus(status)
            .catch { exception ->
                _uiState.update {
                    it.copy(error = "Ошибка подсчета сотрудников: ${exception.message}")
                }
                emit(0)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = 0
            )
    }
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
    fun clearSuccessMessage() {
        _uiState.update { it.copy(successMessage = null) }
    }
}
data class StaffUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)
