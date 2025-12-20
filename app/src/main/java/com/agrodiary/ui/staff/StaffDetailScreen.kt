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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
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
 *
 * Отображает:
 * - Полную информацию о сотруднике
 * - Фото (placeholder если нет)
 * - Кнопки редактирования и удаления
 * - Список задач сотрудника (placeholder)
 *
 * @param staffId ID сотрудника
 * @param onNavigateBack Обработчик возврата назад
 * @param onEditClick Обработчик клика по кнопке редактирования
 * @param onDeleteSuccess Обработчик успешного удаления
 * @param viewModel ViewModel
 * @param modifier Модификатор
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

    // Обработка ошибок и успешных сообщений
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
            // Если сотрудник удален, вызываем callback успешного удаления
            if (message.contains("удален", ignoreCase = true)) {
                onDeleteSuccess()
            }
        }
    }

    Scaffold(
        topBar = {
            AgroDiaryTopBar(
                title = staff?.name ?: "Сотрудник",
                onBackClick = onNavigateBack,
                actions = {
                    // Кнопка редактирования
                    IconButton(
                        onClick = { staff?.let { onEditClick(it.id) } },
                        enabled = staff != null && !uiState.isLoading
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Редактировать"
                        )
                    }
                    // Кнопка удаления
                    IconButton(
                        onClick = { showDeleteDialog = true },
                        enabled = staff != null && !uiState.isLoading
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Удалить"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = modifier
    ) { padding ->
        when {
            uiState.isLoading -> {
                // Индикатор загрузки
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
            staff == null -> {
                // Сотрудник не найден
                EmptyStateView(
                    message = "Сотрудник не найден",
                    icon = Icons.Default.Person,
                    modifier = Modifier.padding(padding)
                )
            }
            else -> {
                // Детали сотрудника
                StaffDetailContent(
                    staff = staff!!,
                    modifier = Modifier.padding(padding)
                )
            }
        }
    }

    // Диалог подтверждения удаления
    if (showDeleteDialog && staff != null) {
        DeleteConfirmDialog(
            itemName = staff!!.name,
            onConfirm = {
                viewModel.deleteStaff(staff!!)
                showDeleteDialog = false
            },
            onDismiss = { showDeleteDialog = false }
        )
    }
}

/**
 * Контент с деталями сотрудника.
 */
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
        // Фото (placeholder)
        StaffPhotoPlaceholder()

        // Основная информация
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

        // Контактная информация
        if (!staff.phone.isNullOrBlank() || !staff.email.isNullOrBlank()) {
            AgroDiaryCard {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Контактная информация",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    staff.phone?.let { InfoRow(label = "Телефон", value = it) }
                    staff.email?.let { InfoRow(label = "Email", value = it) }
                }
            }
        }

        // Информация о работе
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

                        // Вычисляем стаж
                        val daysWorked = (System.currentTimeMillis() - it) / (1000 * 60 * 60 * 24)
                        val yearsWorked = daysWorked / 365
                        val monthsWorked = (daysWorked % 365) / 30
                        val experienceText = when {
                            yearsWorked > 0 -> {
                                if (monthsWorked > 0) {
                                    "$yearsWorked ${yearWord(yearsWorked.toInt())} $monthsWorked ${monthWord(monthsWorked.toInt())}"
                                } else {
                                    "$yearsWorked ${yearWord(yearsWorked.toInt())}"
                                }
                            }
                            monthsWorked > 0 -> "$monthsWorked ${monthWord(monthsWorked.toInt())}"
                            else -> "$daysWorked ${dayWord(daysWorked.toInt())}"
                        }
                        InfoRow(label = "Стаж работы", value = experienceText)
                    }

                    staff.salary?.let {
                        val numberFormat = NumberFormat.getNumberInstance(Locale("ru"))
                        InfoRow(
                            label = "Зарплата",
                            value = "${numberFormat.format(it)} руб."
                        )
                    }
                }
            }
        }

        // Заметки
        if (!staff.notes.isNullOrBlank()) {
            AgroDiaryCard {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Заметки",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = staff.notes,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Системная информация
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
                InfoRow(
                    label = "Создано",
                    value = dateFormatter.format(Date(staff.createdAt))
                )
                InfoRow(
                    label = "Обновлено",
                    value = dateFormatter.format(Date(staff.updatedAt))
                )
            }
        }

        // Задачи сотрудника (placeholder)
        AgroDiaryCard {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Задачи сотрудника",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Задачи сотрудника будут доступны после реализации модуля Задачи",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Placeholder для фото сотрудника.
 */
@Composable
private fun StaffPhotoPlaceholder() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
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

/**
 * Строка информации с меткой и значением.
 */
@Composable
private fun InfoRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
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

// Вспомогательные функции для склонения слов
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

// PREVIEWS

@Preview(showBackground = true)
@Composable
private fun StaffDetailScreenPreview() {
    AgroDiaryTheme {
        StaffDetailContent(
            staff = StaffEntity(
                id = 1,
                name = "Иван Петров",
                position = "Управляющий фермой",
                phone = "+7 (999) 123-45-67",
                email = "ivan@example.com",
                hireDate = System.currentTimeMillis() - 365L * 24 * 60 * 60 * 1000, // 1 год назад
                salary = 50000.0,
                status = StaffStatus.ACTIVE,
                notes = "Опытный управляющий с 10-летним стажем работы в сельском хозяйстве.",
                createdAt = System.currentTimeMillis() - 365L * 24 * 60 * 60 * 1000,
                updatedAt = System.currentTimeMillis()
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun StaffDetailScreenMinimalPreview() {
    AgroDiaryTheme {
        StaffDetailContent(
            staff = StaffEntity(
                id = 2,
                name = "Мария Сидорова",
                position = null,
                phone = null,
                email = null,
                status = StaffStatus.ACTIVE
            )
        )
    }
}
