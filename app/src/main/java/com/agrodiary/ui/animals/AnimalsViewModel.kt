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
@HiltViewModel
class AnimalsViewModel @Inject constructor(
    private val repository: AnimalRepository
) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    private val _selectedType = MutableStateFlow<AnimalType?>(null)
    val selectedType: StateFlow<AnimalType?> = _selectedType.asStateFlow()
    private val _selectedStatus = MutableStateFlow<AnimalStatus?>(null)
    val selectedStatus: StateFlow<AnimalStatus?> = _selectedStatus.asStateFlow()
    private val _uiState = MutableStateFlow(AnimalsUiState())
    val uiState: StateFlow<AnimalsUiState> = _uiState.asStateFlow()
    @OptIn(ExperimentalCoroutinesApi::class)
    val animals: StateFlow<List<AnimalEntity>> = combine(
        _searchQuery,
        _selectedType,
        _selectedStatus
    ) { query, type, status ->
        Triple(query, type, status)
    }.flatMapLatest { (query, type, status) ->
        when {
            query.isNotBlank() -> repository.searchAnimals(query)
            type != null && status != null -> repository.getAnimalsByTypeAndStatus(type, status)
            type != null -> repository.getAnimalsByType(type)
            status != null -> repository.getAnimalsByStatus(status)
            else -> repository.getAllAnimals()
        }
    }.catch { exception ->
        _uiState.update { it.copy(error = exception.message) }
        emit(emptyList())
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = emptyList()
    )
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }
    fun setSelectedType(type: AnimalType?) {
        _selectedType.value = type
    }
    fun setSelectedStatus(status: AnimalStatus?) {
        _selectedStatus.value = status
    }
    fun clearFilters() {
        _searchQuery.value = ""
        _selectedType.value = null
        _selectedStatus.value = null
    }
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
    fun deleteAnimal(animal: AnimalEntity, onSuccess: (() -> Unit)? = null) {
        viewModelScope.launch {
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
    fun deleteAnimalById(id: Long, onSuccess: (() -> Unit)? = null) {
        viewModelScope.launch {
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
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
    fun clearSuccessMessage() {
        _uiState.update { it.copy(successMessage = null) }
    }
}
data class AnimalsUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)
