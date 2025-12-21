package com.agrodiary.ui.staff

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.agrodiary.data.local.entity.StaffEntity
import com.agrodiary.data.local.entity.StaffStatus
import com.agrodiary.ui.components.AgroDiaryCard
import com.agrodiary.ui.components.AgroDiaryTopBar
import com.agrodiary.ui.components.DeleteConfirmDialog
import com.agrodiary.ui.components.EmptyStateView
import com.agrodiary.ui.theme.AgroDiaryTheme
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Экран детальной информации о сотруднике.
 */
@Composable
fun StaffDetailScreen(
    staffId: Long,
    onNavigateBack: () -> Unit,
    onEditClick: (Long) -> Unit,
    onDeleteSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: StaffViewModel = hiltViewModel()
) {
    val staff by viewModel.getStaffByIdFlow(staffId).collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    var showDeleteDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.clearError()
        }
    }

    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearSuccessMessage()
        }
    }

    Scaffold(
        topBar = {
            AgroDiaryTopBar(
                title = staff?.name ?: "Сотрудник",
                onBackClick = onNavigateBack,
                actions = {
                    IconButton(
                        onClick = { staff?.let { onEditClick(it.id) } },
                        enabled = staff != null && !uiState.isLoading
                    ) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Редактировать")
                    }
                    IconButton(
                        onClick = { showDeleteDialog = true },
                        enabled = staff != null && !uiState.isLoading
                    ) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Удалить")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = modifier
    ) { padding ->
        when {
            uiState.isLoading -> {
                Column(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
            staff == null -> {
                EmptyStateView(
                    message = "Сотрудник не найден",
                    icon = Icons.Default.Person,
                    modifier = Modifier.padding(padding)
                )
            }
            else -> {
                StaffDetailContent(
                    staff = staff!!,
                    modifier = Modifier.padding(padding)
                )
            }
        }
    }

    if (showDeleteDialog && staff != null) {
        DeleteConfirmDialog(
            itemName = staff!!.name,
            onConfirm = {
                viewModel.deleteStaff(
                    staff = staff!!,
                    onSuccess = onDeleteSuccess,
                    showSuccessMessage = false
                )
                showDeleteDialog = false
            },
            onDismiss = { showDeleteDialog = false }
        )
    }
}

@Composable
private fun StaffDetailContent(
    staff: StaffEntity,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StaffPhotoHeader(photoUri = staff.photoUri)

        AgroDiaryCard {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Основная информация",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                InfoRow(label = "Имя", value = staff.name)
                staff.position?.let { InfoRow(label = "Должность", value = it) }
                InfoRow(label = "Статус", value = staff.status.displayName)
            }
        }

        if (staff.hireDate != null || staff.salary != null) {
            AgroDiaryCard {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Информация о работе",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    staff.hireDate?.let {
                        val dateFormatter = SimpleDateFormat("dd.MM.yyyy", Locale("ru"))
                        InfoRow(label = "Дата приёма", value = dateFormatter.format(Date(it)))
                    }
                    staff.salary?.let {
                        val numberFormat = NumberFormat.getNumberInstance(Locale("ru"))
                        InfoRow(label = "Зарплата", value = "${numberFormat.format(it)} руб.")
                    }
                }
            }
        }

        AgroDiaryCard {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Системная информация",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                val dateFormatter = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale("ru"))
                InfoRow(label = "Создано", value = dateFormatter.format(Date(staff.createdAt)))
                InfoRow(label = "Обновлено", value = dateFormatter.format(Date(staff.updatedAt)))
            }
        }
    }
}

@Composable
private fun StaffPhotoHeader(photoUri: String?) {
    Surface(
        modifier = Modifier.fillMaxWidth().height(240.dp),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        if (!photoUri.isNullOrBlank()) {
            AsyncImage(
                model = photoUri,
                contentDescription = "Фото сотрудника",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Фото не добавлено",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

private fun yearWord(years: Int): String = when {
    years % 10 == 1 && years % 100 != 11 -> "год"
    years % 10 in 2..4 && years % 100 !in 12..14 -> "года"
    else -> "лет"
}

private fun monthWord(months: Int): String = when {
    months % 10 == 1 && months % 100 != 11 -> "месяц"
    months % 10 in 2..4 && months % 100 !in 12..14 -> "месяца"
    else -> "месяцев"
}

private fun dayWord(days: Int): String = when {
    days % 10 == 1 && days % 100 != 11 -> "день"
    days % 10 in 2..4 && days % 100 !in 12..14 -> "дня"
    else -> "дней"
}

@Preview(showBackground = true)
@Composable
private fun StaffDetailScreenPreview() {
    AgroDiaryTheme {
        StaffDetailContent(
            staff = StaffEntity(
                id = 1,
                name = "Иван Петров",
                status = StaffStatus.ACTIVE
            )
        )
    }
}
