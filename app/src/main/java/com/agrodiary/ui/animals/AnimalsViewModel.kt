package com.agrodiary.ui.animals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agrodiary.data.local.entity.AnimalEntity
import com.agrodiary.data.local.entity.AnimalStatus
import com.agrodiary.data.local.entity.AnimalType
import com.agrodiary.data.repository.AnimalRepository
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
 * ViewModel для модуля управления животными.
 *
 * Предоставляет функциональность для:
 * - Загрузки и отображения списка животных
 * - Поиска животных по имени
 * - Фильтрации по типу и статусу
 * - CRUD операций (создание, чтение, обновление, удаление)
 * - Получения детальной информации о конкретном животном
 *
 * @property repository Репозиторий для работы с данными животных
 */
@HiltViewModel
class AnimalsViewModel @Inject constructor(
    private val repository: AnimalRepository
) : ViewModel() {

    // Поисковый запрос
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Выбранный фильтр по типу животного (null = все типы)
    private val _selectedType = MutableStateFlow<AnimalType?>(null)
    val selectedType: StateFlow<AnimalType?> = _selectedType.asStateFlow()

    // Выбранный фильтр по статусу (null = все статусы)
    private val _selectedStatus = MutableStateFlow<AnimalStatus?>(null)
    val selectedStatus: StateFlow<AnimalStatus?> = _selectedStatus.asStateFlow()

    // UI состояние экрана
    private val _uiState = MutableStateFlow(AnimalsUiState())
    val uiState: StateFlow<AnimalsUiState> = _uiState.asStateFlow()

    /**
     * Реактивный список животных с применением фильтров и поиска.
     * Автоматически обновляется при изменении поискового запроса, типа или статуса.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val animals: StateFlow<List<AnimalEntity>> = combine(
        _searchQuery,
        _selectedType,
        _selectedStatus
    ) { query, type, status ->
        Triple(query, type, status)
    }.flatMapLatest { (query, type, status) ->
        when {
            // Поиск имеет приоритет
            query.isNotBlank() -> repository.searchAnimals(query)
            // Фильтр по типу и статусу
            type != null && status != null -> repository.getAnimalsByTypeAndStatus(type, status)
            // Только по типу
            type != null -> repository.getAnimalsByType(type)
            // Только по статусу
            status != null -> repository.getAnimalsByStatus(status)
            // Все животные
            else -> repository.getAllAnimals()
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
        loadAnimals()
    }

    /**
     * Загрузка списка животных с обработкой состояния загрузки.
     */
    private fun loadAnimals() {
        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                // Состояние обновится автоматически через Flow
                _uiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Ошибка загрузки животных: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * Установка поискового запроса.
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
     * Установка фильтра по типу животного.
     * @param type Тип животного или null для отображения всех типов
     */
    fun setSelectedType(type: AnimalType?) {
        _selectedType.value = type
    }

    /**
     * Установка фильтра по статусу животного.
     * @param status Статус животного или null для отображения всех статусов
     */
    fun setSelectedStatus(status: AnimalStatus?) {
        _selectedStatus.value = status
    }

    /**
     * Сброс всех фильтров и поиска.
     */
    fun clearFilters() {
        _searchQuery.value = ""
        _selectedType.value = null
        _selectedStatus.value = null
    }

    /**
     * Получение животного по ID.
     * @param id Идентификатор животного
     * @return Animal или null если не найдено
     */
    suspend fun getAnimalById(id: Long): AnimalEntity? {
        return try {
            repository.getAnimalById(id)
        } catch (e: Exception) {
            _uiState.update {
                it.copy(error = "Ошибка получения животного: ${e.message}")
            }
            null
        }
    }

    /**
     * Получение животного по ID как Flow (для реактивного обновления).
     * @param id Идентификатор животного
     * @return StateFlow с данными животного
     */
    fun getAnimalByIdFlow(id: Long): StateFlow<AnimalEntity?> {
        return repository.getAnimalByIdFlow(id)
            .catch { exception ->
                _uiState.update {
                    it.copy(error = "Ошибка получения животного: ${exception.message}")
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
     * Добавление нового животного.
     * @param animal Данные животного для добавления
     * @return ID добавленного животного или null в случае ошибки
     */
    fun addAnimal(animal: AnimalEntity, onSuccess: ((Long) -> Unit)? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val id = repository.insertAnimal(animal)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        successMessage = "Животное \"${animal.name}\" успешно добавлено"
                    )
                }
                onSuccess?.invoke(id)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Ошибка добавления животного: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * Обновление данных существующего животного.
     * @param animal Обновленные данные животного
     */
    fun updateAnimal(animal: AnimalEntity, onSuccess: (() -> Unit)? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                repository.updateAnimal(animal)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        successMessage = "Данные животного \"${animal.name}\" обновлены"
                    )
                }
                onSuccess?.invoke()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Ошибка обновления животного: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * Удаление животного.
     * @param animal Животное для удаления
     */
    fun deleteAnimal(animal: AnimalEntity, onSuccess: (() -> Unit)? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                repository.deleteAnimal(animal)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        successMessage = "Животное \"${animal.name}\" удалено"
                    )
                }
                onSuccess?.invoke()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Ошибка удаления животного: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * Удаление животного по ID.
     * @param id Идентификатор животного
     */
    fun deleteAnimalById(id: Long, onSuccess: (() -> Unit)? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                repository.deleteAnimalById(id)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        successMessage = "Животное удалено"
                    )
                }
                onSuccess?.invoke()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Ошибка удаления животного: ${e.message}"
                    )
                }
            }
        }
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
 * UI состояние экрана управления животными.
 *
 * @property isLoading Флаг загрузки данных
 * @property error Сообщение об ошибке (null если нет ошибки)
 * @property successMessage Сообщение об успешной операции (null если нет)
 */
data class AnimalsUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)
