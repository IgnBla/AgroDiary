package com.agrodiary.ui.journal

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.agrodiary.data.local.entity.AnimalEntity
import com.agrodiary.data.local.entity.JournalEntryEntity
import com.agrodiary.data.local.entity.StaffEntity
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
    var relatedAnimal by remember { mutableStateOf<AnimalEntity?>(null) }
    var relatedStaff by remember { mutableStateOf<StaffEntity?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(entryId) {
        entry = viewModel.getEntryById(entryId)
        entry?.let { e ->
            e.relatedAnimalId?.let { animalId ->
                relatedAnimal = viewModel.getAnimalById(animalId)
            }
            e.relatedStaffId?.let { staffId ->
                relatedStaff = viewModel.getStaffById(staffId)
            }
        }
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
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    // Date
                    Text(
                        text = SimpleDateFormat("dd MMMM yyyy", Locale("ru")).format(Date(e.date)),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Entry Type
                    EntryTypeChip(type = e.entryType)

                    Spacer(modifier = Modifier.height(16.dp))

                    // Description
                    if (e.description.isNotBlank()) {
                        DetailSection(title = "Описание") {
                            Text(
                                text = e.description,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Amount
                    e.amount?.let { amount ->
                        DetailCard(
                            icon = { Icon(Icons.Default.Payments, contentDescription = null) },
                            title = "Сумма",
                            value = String.format(Locale("ru"), "%.2f ₽", amount)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Related Animal
                    relatedAnimal?.let { animal ->
                        DetailCard(
                            icon = { Icon(Icons.Default.Pets, contentDescription = null) },
                            title = "Животное",
                            value = animal.name
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Related Staff
                    relatedStaff?.let { staff ->
                        DetailCard(
                            icon = { Icon(Icons.Default.Person, contentDescription = null) },
                            title = "Сотрудник",
                            value = staff.name
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Notes
                    if (!e.notes.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        DetailSection(title = "Заметки") {
                            Text(
                                text = e.notes,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Created At
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Создано: ${SimpleDateFormat("dd.MM.yyyy HH:mm", Locale("ru")).format(Date(e.createdAt))}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } ?: run {
                Text("Запись не найдена", modifier = Modifier.padding(padding))
            }
        }
    }
}

@Composable
private fun DetailSection(
    title: String,
    content: @Composable () -> Unit
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.SemiBold
    )
    Spacer(modifier = Modifier.height(4.dp))
    content()
}

@Composable
private fun DetailCard(
    icon: @Composable () -> Unit,
    title: String,
    value: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon()
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
