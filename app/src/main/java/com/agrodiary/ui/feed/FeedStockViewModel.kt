package com.agrodiary.ui.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agrodiary.data.local.entity.FeedCategory
import com.agrodiary.data.local.entity.FeedStockEntity
import com.agrodiary.data.repository.FeedStockRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedStockViewModel @Inject constructor(
    private val repository: FeedStockRepository
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow<FeedCategory?>(null)
    val selectedCategory = _selectedCategory.asStateFlow()

    private val _uiState = MutableStateFlow(FeedStockUiState())
    val uiState = _uiState.asStateFlow()

    val feedStocks = _selectedCategory.flatMapLatest { category ->
        if (category != null) {
            repository.getFeedStocksByCategory(category)
        } else {
            repository.getAllFeedStocks()
        }
    }.catch { e ->
        _uiState.update { it.copy(error = e.message) }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Separate flow for low stock warnings
    val lowStockWarnings = repository.getLowStockFeed()
        .catch { e -> _uiState.update { it.copy(error = e.message) } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun setSelectedCategory(category: FeedCategory?) {
        _selectedCategory.value = category
    }

    fun addFeedStock(feedStock: FeedStockEntity, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                repository.insertFeedStock(feedStock)
                onSuccess()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun updateFeedStock(feedStock: FeedStockEntity, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                repository.updateFeedStock(feedStock)
                onSuccess()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun deleteFeedStock(feedStock: FeedStockEntity) {
        viewModelScope.launch {
            try {
                repository.deleteFeedStock(feedStock)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
    
    suspend fun getFeedStockById(id: Long): FeedStockEntity? {
        return repository.getFeedStockById(id)
    }
}

data class FeedStockUiState(
    val isLoading: Boolean = false,
    val error: String? = null
)
