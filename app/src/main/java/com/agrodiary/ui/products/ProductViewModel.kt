package com.agrodiary.ui.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agrodiary.data.local.entity.ProductCategory
import com.agrodiary.data.local.entity.ProductEntity
import com.agrodiary.data.repository.ProductRepository
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
class ProductViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow<ProductCategory?>(null)
    val selectedCategory = _selectedCategory.asStateFlow()

    private val _uiState = MutableStateFlow(ProductUiState())
    val uiState = _uiState.asStateFlow()

    val products = _selectedCategory.flatMapLatest { category ->
        if (category != null) {
            repository.getProductsByCategory(category)
        } else {
            repository.getAllProducts()
        }
    }.catch { e ->
        _uiState.update { it.copy(error = e.message) }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun setSelectedCategory(category: ProductCategory?) {
        _selectedCategory.value = category
    }

    fun addProduct(product: ProductEntity, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                repository.insertProduct(product)
                onSuccess()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun updateProduct(product: ProductEntity, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                repository.updateProduct(product)
                onSuccess()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun deleteProduct(product: ProductEntity) {
        viewModelScope.launch {
            try {
                repository.deleteProduct(product)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
    
    suspend fun getProductById(id: Long): ProductEntity? {
        return repository.getProductById(id)
    }
}

data class ProductUiState(
    val isLoading: Boolean = false,
    val error: String? = null
)
