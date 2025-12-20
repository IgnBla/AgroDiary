package com.agrodiary.ui.tasks

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.agrodiary.data.local.entity.TaskEntity
import com.agrodiary.ui.components.AgroDiaryTopBar
import com.agrodiary.ui.components.ConfirmDialog
import com.agrodiary.ui.components.LoadingView
import com.agrodiary.ui.tasks.components.TaskPriorityChip
import com.agrodiary.ui.tasks.components.TaskStatusChip
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TaskDetailScreen(
    taskId: Long,
    onNavigateBack: () -> Unit,
    onEditClick: (Long) -> Unit,
    viewModel: TasksViewModel = hiltViewModel()
) {
    var task by remember { mutableStateOf<TaskEntity?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(taskId) {
        task = viewModel.getTaskById(taskId)
        isLoading = false
    }

    if (showDeleteDialog && task != null) {
        ConfirmDialog(
            title = "Удалить задачу?",
            message = "Вы уверены, что хотите удалить задачу \"${task?.title}\"?",
            onConfirm = {
                viewModel.deleteTask(task!!)
                showDeleteDialog = false
                onNavigateBack()
            },
            onDismiss = { showDeleteDialog = false }
        )
    }

    Scaffold(
        topBar = {
            AgroDiaryTopBar(
                title = "Детали задачи",
                onBackClick = onNavigateBack,
                actions = {
                    androidx.compose.material3.IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Удалить")
                    }
                }
            )
        },
        floatingActionButton = {
            if (task != null) {
                FloatingActionButton(onClick = { onEditClick(taskId) }) {
                    Icon(Icons.Default.Edit, contentDescription = "Редактировать")
                }
            }
        }
    ) { padding ->
        if (isLoading) {
            LoadingView(modifier = Modifier.padding(padding))
        } else {
            task?.let { t ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp)
                ) {
                    Text(
                        text = t.title,
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    TaskStatusChip(status = t.status)
                    Spacer(modifier = Modifier.height(8.dp))
                    TaskPriorityChip(priority = t.priority)
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    if (!t.description.isNullOrBlank()) {
                        Text(
                            text = "Описание",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = t.description,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    
                    if (t.dueDate != null) {
                        Text(
                            text = "Срок выполнения",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = SimpleDateFormat("dd MMMM yyyy", Locale("ru")).format(Date(t.dueDate)),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            } ?: run {
                // Task not found
                Text("Задача не найдена", modifier = Modifier.padding(padding))
            }
        }
    }
}
