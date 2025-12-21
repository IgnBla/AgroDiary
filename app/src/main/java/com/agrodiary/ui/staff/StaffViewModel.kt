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

/**
 * ViewModel для модуля управления персоналом.
 *
 * Предоставляет функциональность для:
 * - Загрузки и отображения списка сотрудников
 * - Поиска сотрудников по имени и должности
 * - Фильтрации по статусу (АКТИВЕН/В_ОТПУСКЕ/УВОЛЕН)
 * - CRUD операций (создание, чтение, обновление, удаление)
 * - Получения детальной информации о конкретном сотруднике
 *
 * @property repository Репозиторий для работы с данными сотрудников
 */
@HiltViewModel
class StaffViewModel @Inject constructor(
    private val repository: StaffRepository
) : ViewModel() {

    // Поисковый запрос
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Выбранный фильтр по статусу сотрудника (null = все статусы)
    private val _selectedStatus = MutableStateFlow<StaffStatus?>(null)
    val selectedStatus: StateFlow<StaffStatus?> = _selectedStatus.asStateFlow()

    // UI состояние экрана
    private val _uiState = MutableStateFlow(StaffUiState())
    val uiState: StateFlow<StaffUiState> = _uiState.asStateFlow()

    /**
     * Реактивный список сотрудников с применением фильтров и поиска.
     * Автоматически обновляется при изменении поискового запроса или статуса.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val staff: StateFlow<List<StaffEntity>> = combine(
        _searchQuery,
        _selectedStatus
    ) { query, status ->
        Pair(query, status)
    }.flatMapLatest { (query, status) ->
        when {
            // Поиск имеет приоритет
            query.isNotBlank() -> repository.searchStaff(query)
            // Фильтр по статусу
            status != null -> repository.getStaffByStatus(status)
            // Все сотрудники
            else -> repository.getAllStaff()
        }
    }.catch { exception ->
        // Обработка ошибок загрузки
        _uiState.update { it.copy(error = exception.message) }
        emit(emptyList())
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        // Инициализация: загрузка данных
        loadStaff()
    }

    /**
     * Загрузка списка сотрудников с обработкой состояния загрузки.
     */
    private fun loadStaff() {
        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                // Состояние обновится автоматически через Flow
                _uiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Ошибка загрузки сотрудников: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * Установка поискового запроса.
     * Поиск выполняется по имени и должности сотрудника.
     * @param query Текст для поиска
     */
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    /**
     * Очистка поискового запроса.
     */
    fun clearSearch() {
        _searchQuery.value = ""
    }

    /**
     * Установка фильтра по статусу сотрудника.
     * @param status Статус сотрудника или null для отображения всех статусов
     */
    fun setSelectedStatus(status: StaffStatus?) {
        _selectedStatus.value = status
    }

    /**
     * Сброс всех фильтров и поиска.
     */
    fun clearFilters() {
        _searchQuery.value = ""
        _selectedStatus.value = null
    }

    /**
     * Получение сотрудника по ID.
     * @param id Идентификатор сотрудника
     * @return Staff или null если не найдено
     */
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

    /**
     * Получение сотрудника по ID как Flow (для реактивного обновления).
     * @param id Идентификатор сотрудника
     * @return StateFlow с данными сотрудника
     */
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

    /**
     * Получение списка только активных сотрудников.
     * Используется для выбора исполнителя задачи.
     * @return StateFlow со списком активных сотрудников
     */
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

    /**
     * Добавление нового сотрудника.
     * @param staff Данные сотрудника для добавления
     * @param onSuccess Callback с ID добавленного сотрудника
     */
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

    /**
     * Обновление данных существующего сотрудника.
     * @param staff Обновленные данные сотрудника
     * @param onSuccess Callback при успешном обновлении
     */
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

    /**
     * Удаление сотрудника.
     * @param staff Сотрудник для удаления
     * @param onSuccess Callback при успешном удалении
     */
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
                            "????????? ${staff.name} ??????"
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

    /**
     * Удаление сотрудника по ID.
     * @param id Идентификатор сотрудника
     * @param onSuccess Callback при успешном удалении
     */
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

    /**
     * Получение количества сотрудников по статусу.
     * Используется для статистики на главном экране.
     * @param status Статус сотрудника
     * @return StateFlow с количеством
     */
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

    /**
     * Очистка сообщения об ошибке.
     */
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    /**
     * Очистка сообщения об успехе.
     */
    fun clearSuccessMessage() {
        _uiState.update { it.copy(successMessage = null) }
    }
}

/**
 * UI состояние экрана управления персоналом.
 *
 * @property isLoading Флаг загрузки данных
 * @property error Сообщение об ошибке (null если нет ошибки)
 * @property successMessage Сообщение об успешной операции (null если нет)
 */
data class StaffUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)
