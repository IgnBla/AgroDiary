package com.agrodiary.ui.tasks

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.agrodiary.data.local.entity.TaskEntity
import com.agrodiary.data.local.entity.TaskPriority
import com.agrodiary.data.local.entity.TaskStatus
import com.agrodiary.ui.components.AgroDiaryTextField
import com.agrodiary.ui.components.AgroDiaryTopBar
import com.agrodiary.ui.components.DatePickerField
import com.agrodiary.ui.components.DropdownField

@Composable
fun AddEditTaskScreen(
    taskId: Long?,
    onNavigateBack: () -> Unit,
    onSaveSuccess: () -> Unit,
    viewModel: TasksViewModel = hiltViewModel()
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(TaskPriority.MEDIUM) }
    var status by remember { mutableStateOf(TaskStatus.NEW) }
    var dueDate by remember { mutableLongStateOf(System.currentTimeMillis() + 86400000) } // Tomorrow
    
    var isEditing by remember { mutableStateOf(false) }
    var existingTask by remember { mutableStateOf<TaskEntity?>(null) }

    LaunchedEffect(taskId) {
        if (taskId != null) {
            isEditing = true
            existingTask = viewModel.getTaskById(taskId)
            existingTask?.let {
                title = it.title
                description = it.description ?: ""
                priority = it.priority
                status = it.status
                dueDate = it.dueDate ?: System.currentTimeMillis()
            }
        }
    }

    Scaffold(
        topBar = {
            AgroDiaryTopBar(
                title = if (isEditing) "Редактировать задачу" else "Новая задача",
                onBackClick = onNavigateBack
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            AgroDiaryTextField(
                value = title,
                onValueChange = { title = it },
                label = "Название",
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            AgroDiaryTextField(
                value = description,
                onValueChange = { description = it },
                label = "Описание",
                modifier = Modifier.fillMaxWidth(),
                singleLine = false,
                minLines = 3
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            DropdownField(
                label = "Приоритет",
                items = TaskPriority.entries,
                selectedItem = priority,
                onItemSelected = { priority = it },
                itemLabel = { it.displayName }
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            DropdownField(
                label = "Статус",
                items = TaskStatus.entries,
                selectedItem = status,
                onItemSelected = { status = it },
                itemLabel = { it.displayName }
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            DatePickerField(
                label = "Срок выполнения",
                selectedDate = dueDate,
                onDateSelected = { it?.let { date -> dueDate = date } }
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = {
                    val task = if (isEditing && existingTask != null) {
                        existingTask!!.copy(
                            title = title,
                            description = description,
                            priority = priority,
                            status = status,
                            dueDate = dueDate,
                            updatedAt = System.currentTimeMillis()
                        )
                    } else {
                        TaskEntity(
                            title = title,
                            description = description,
                            priority = priority,
                            status = status,
                            dueDate = dueDate,
                            assignedStaffId = null, // TODO: Implement staff selection
                            animalId = null // TODO: Implement animal selection
                        )
                    }
                    
                    if (isEditing) {
                        viewModel.updateTask(task, onSaveSuccess)
                    } else {
                        viewModel.addTask(task, onSaveSuccess)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = title.isNotBlank()
            ) {
                Text("Сохранить")
            }
        }
    }
}
