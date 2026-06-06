package com.agrodiary.ui.home
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agrodiary.data.local.entity.FeedStockEntity
import com.agrodiary.data.local.entity.JournalEntryEntity
import com.agrodiary.data.local.entity.TaskEntity
import com.agrodiary.data.local.entity.TaskStatus
import com.agrodiary.data.repository.AnimalRepository
import com.agrodiary.data.repository.FeedStockRepository
import com.agrodiary.data.repository.JournalRepository
import com.agrodiary.data.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val animalRepository: AnimalRepository,
    private val taskRepository: TaskRepository,
    private val journalRepository: JournalRepository,
    private val feedStockRepository: FeedStockRepository
) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    val statistics: StateFlow<HomeStatistics> = combine(
        animalRepository.getTotalAnimalCount(),
        taskRepository.getActiveTaskCount(),
        taskRepository.getTaskCountByStatus(TaskStatus.COMPLETED),
        feedStockRepository.getLowStockCount()
    ) { totalAnimals, activeTasks, completedTasks, lowStockCount ->
        HomeStatistics(
            totalAnimals = totalAnimals,
            activeTasks = activeTasks,
            completedTasks = completedTasks,
            lowStockCount = lowStockCount
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeStatistics()
    )
    val recentJournalEntries: StateFlow<List<JournalEntryEntity>> =
        journalRepository.getRecentEntries(limit = 5)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    val urgentTasks: StateFlow<List<TaskEntity>> =
        taskRepository.getUpcomingTasks(daysAhead = 3)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    val lowStockWarnings: StateFlow<List<FeedStockEntity>> =
        feedStockRepository.getLowStockFeeds()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    val uiState: StateFlow<HomeUiState> = combine(
        statistics,
        recentJournalEntries,
        urgentTasks,
        lowStockWarnings,
        isLoading
    ) { stats, journalEntries, tasks, lowStock, loading ->
        HomeUiState(
            statistics = stats,
            recentJournalEntries = journalEntries,
            urgentTasks = tasks,
            lowStockWarnings = lowStock,
            isLoading = loading
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState()
    )
}
data class HomeStatistics(
    val totalAnimals: Int = 0,
    val activeTasks: Int = 0,
    val completedTasks: Int = 0,
    val lowStockCount: Int = 0
)
data class HomeUiState(
    val statistics: HomeStatistics = HomeStatistics(),
    val recentJournalEntries: List<JournalEntryEntity> = emptyList(),
    val urgentTasks: List<TaskEntity> = emptyList(),
    val lowStockWarnings: List<FeedStockEntity> = emptyList(),
    val isLoading: Boolean = false
)
