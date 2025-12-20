package com.agrodiary.ui.tasks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Task
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.agrodiary.data.local.entity.TaskStatus
import com.agrodiary.ui.components.AgroDiaryFilterChips
import com.agrodiary.ui.components.AgroDiaryTopBar
import com.agrodiary.ui.components.EmptyStateView
import com.agrodiary.ui.components.AgroDiarySearchBar
import com.agrodiary.ui.tasks.components.TaskCard

@Composable
fun TasksListScreen(
    onTaskClick: (Long) -> Unit,
    onAddClick: () -> Unit,
    viewModel: TasksViewModel = hiltViewModel()
) {
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedStatus by viewModel.selectedStatus.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            AgroDiaryTopBar(
                title = "Задачи",
                onBackClick = null
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(Icons.Default.Add, contentDescription = "Добавить задачу")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            AgroDiarySearchBar(
                query = searchQuery,
                onQueryChange = viewModel::setSearchQuery,
                placeholder = "Поиск задач..."
            )

            AgroDiaryFilterChips(
                items = TaskStatus.values().toList(),
                selectedItem = selectedStatus,
                onItemSelected = viewModel::setSelectedStatus,
                itemLabel = { 
                    when(it) {
                        TaskStatus.NEW -> "Новые"
                        TaskStatus.IN_PROGRESS -> "В работе"
                        TaskStatus.COMPLETED -> "Готовые"
                        TaskStatus.CANCELLED -> "Отмена"
                    }
                }
            )

            if (tasks.isEmpty()) {
                EmptyStateView(
                    message = if (searchQuery.isNotEmpty() || selectedStatus != null) "Ничего не найдено" else "Нет задач",
                    icon = Icons.Default.Task
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(tasks) { task ->
                        TaskCard(
                            task = task,
                            onClick = { onTaskClick(task.id) }
                        )
                    }
                }
            }
        }
    }
}
