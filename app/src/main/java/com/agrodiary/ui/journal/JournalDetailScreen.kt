package com.agrodiary.ui.journal

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.agrodiary.data.local.entity.JournalEntryEntity
import com.agrodiary.ui.components.AgroDiaryTopBar
import com.agrodiary.ui.components.ConfirmDialog
import com.agrodiary.ui.components.LoadingView
import com.agrodiary.ui.journal.components.EntryTypeChip
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun JournalDetailScreen(
    entryId: Long,
    onNavigateBack: () -> Unit,
    onEditClick: (Long) -> Unit,
    viewModel: JournalViewModel = hiltViewModel()
) {
    var entry by remember { mutableStateOf<JournalEntryEntity?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(entryId) {
        entry = viewModel.getEntryById(entryId)
        isLoading = false
    }

    if (showDeleteDialog && entry != null) {
        ConfirmDialog(
            title = "Удалить запись?",
            message = "Вы уверены, что хотите удалить эту запись?",
            onConfirm = {
                viewModel.deleteEntry(entry!!)
                showDeleteDialog = false
                onNavigateBack()
            },
            onDismiss = { showDeleteDialog = false }
        )
    }

    Scaffold(
        topBar = {
            AgroDiaryTopBar(
                title = "Детали записи",
                onBackClick = onNavigateBack,
                actions = {
                    IconButton(onClick = { onEditClick(entryId) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Редактировать")
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Удалить")
                    }
                }
            )
        }
    ) { padding ->
        if (isLoading) {
            LoadingView(modifier = Modifier.padding(padding))
        } else {
            entry?.let { e ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp)
                ) {
                    Text(
                        text = SimpleDateFormat("dd MMMM yyyy", Locale("ru")).format(Date(e.date)),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    EntryTypeChip(type = e.entryType)
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    if (e.description.isNotBlank()) {
                        Text(
                            text = "Описание",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = e.description,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    
                    if (!e.notes.isNullOrBlank()) {
                        Text(
                            text = "Заметки",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = e.notes,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            } ?: run {
                Text("Запись не найдена", modifier = Modifier.padding(padding))
            }
        }
    }
}