package com.agrodiary.ui.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agrodiary.data.repository.AnimalRepository
import com.agrodiary.data.repository.FeedStockRepository
import com.agrodiary.data.repository.ProductRepository
import com.agrodiary.data.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ReportsViewModel @Inject constructor(
    private val animalRepository: AnimalRepository,
    private val taskRepository: TaskRepository,
    private val feedStockRepository: FeedStockRepository,
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReportsUiState())
    val uiState = _uiState.asStateFlow()

    // Combine flows from multiple repositories to generate report data
    val reportData = combine(
        animalRepository.getAllAnimals(),
        taskRepository.getAllTasks(),
        feedStockRepository.getAllFeedStocks(),
        productRepository.getAllProducts()
    ) { animals, tasks, feeds, products ->
        
        val totalAnimals = animals.size
        val totalTasks = tasks.size
        val completedTasks = tasks.count { it.status == com.agrodiary.data.local.entity.TaskStatus.COMPLETED }
        val lowStockCount = feeds.count { it.currentQuantity <= it.minQuantity }
        val totalProductsValue = products.sumOf { (it.pricePerUnit ?: 0.0) * it.currentQuantity }

        ReportData(
            totalAnimals = totalAnimals,
            totalTasks = totalTasks,
            completedTasks = completedTasks,
            lowStockCount = lowStockCount,
            totalProductsValue = totalProductsValue
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ReportData()
    )
}

data class ReportsUiState(
    val isLoading: Boolean = false,
    val error: String? = null
)

data class ReportData(
    val totalAnimals: Int = 0,
    val totalTasks: Int = 0,
    val completedTasks: Int = 0,
    val lowStockCount: Int = 0,
    val totalProductsValue: Double = 0.0
)
