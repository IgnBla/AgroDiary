package com.agrodiary.ui.animals

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
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
import com.agrodiary.data.local.entity.AnimalEntity
import com.agrodiary.data.local.entity.AnimalStatus
import com.agrodiary.data.local.entity.AnimalType
import com.agrodiary.ui.components.AgroDiaryCard
import com.agrodiary.ui.components.AgroDiaryTopBar
import com.agrodiary.ui.components.DeleteConfirmDialog
import com.agrodiary.ui.components.EmptyStateView
import com.agrodiary.ui.theme.AgroDiaryTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Экран детальной информации о животном.
 *
 * Отображает:
 * - Полную информацию о животном
 * - Фото (placeholder если нет)
 * - Кнопки редактирования и удаления
 * - История записей журнала (placeholder)
 *
 * @param animalId ID животного
 * @param onNavigateBack Обработчик возврата назад
 * @param onEditClick Обработчик клика по кнопке редактирования
 * @param onDeleteSuccess Обработчик успешного удаления
 * @param viewModel ViewModel
 * @param modifier Модификатор
 */
@Composable
fun AnimalDetailScreen(
    animalId: Long,
    onNavigateBack: () -> Unit,
    onEditClick: (Long) -> Unit,
    onDeleteSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AnimalsViewModel = hiltViewModel()
) {
    val animal by viewModel.getAnimalByIdFlow(animalId).collectAsState()
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
            // Если животное удалено, вызываем callback успешного удаления
            if (message.contains("удалено", ignoreCase = true)) {
                onDeleteSuccess()
            }
        }
    }

    Scaffold(
        topBar = {
            AgroDiaryTopBar(
                title = animal?.name ?: "Животное",
                onBackClick = onNavigateBack,
                actions = {
                    // Кнопка редактирования
                    IconButton(
                        onClick = { animal?.let { onEditClick(it.id) } },
                        enabled = animal != null && !uiState.isLoading
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Редактировать"
                        )
                    }
                    // Кнопка удаления
                    IconButton(
                        onClick = { showDeleteDialog = true },
                        enabled = animal != null && !uiState.isLoading
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
            animal == null -> {
                // Животное не найдено
                EmptyStateView(
                    message = "Животное не найдено",
                    icon = Icons.Default.Pets,
                    modifier = Modifier.padding(padding)
                )
            }
            else -> {
                // Детали животного
                AnimalDetailContent(
                    animal = animal!!,
                    modifier = Modifier.padding(padding)
                )
            }
        }
    }

    // Диалог подтверждения удаления
    if (showDeleteDialog && animal != null) {
        DeleteConfirmDialog(
            itemName = animal!!.name,
            onConfirm = {
                viewModel.deleteAnimal(animal!!)
                showDeleteDialog = false
            },
            onDismiss = { showDeleteDialog = false }
        )
    }
}

/**
 * Контент с деталями животного.
 */
@Composable
private fun AnimalDetailContent(
    animal: AnimalEntity,
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
        AnimalPhotoPlaceholder()

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

                InfoRow(label = "Имя", value = animal.name)
                InfoRow(label = "Тип", value = animal.type.displayName)
                animal.breed?.let { InfoRow(label = "Порода", value = it) }
                animal.gender?.let { InfoRow(label = "Пол", value = it) }
                InfoRow(label = "Статус", value = animal.status.displayName)
            }
        }

        // Физические параметры
        AgroDiaryCard {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Физические параметры",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                animal.weight?.let { InfoRow(label = "Вес", value = "${it.toInt()} кг") }
                animal.birthDate?.let {
                    val dateFormatter = SimpleDateFormat("dd.MM.yyyy", Locale("ru"))
                    InfoRow(label = "Дата рождения", value = dateFormatter.format(Date(it)))

                    // Вычисляем возраст
                    val ageInDays = (System.currentTimeMillis() - it) / (1000 * 60 * 60 * 24)
                    val ageInYears = ageInDays / 365
                    val ageInMonths = (ageInDays % 365) / 30
                    val ageText = when {
                        ageInYears > 0 -> {
                            if (ageInMonths > 0) {
                                "$ageInYears ${yearWord(ageInYears.toInt())} $ageInMonths ${monthWord(ageInMonths.toInt())}"
                            } else {
                                "$ageInYears ${yearWord(ageInYears.toInt())}"
                            }
                        }
                        ageInMonths > 0 -> "$ageInMonths ${monthWord(ageInMonths.toInt())}"
                        else -> "$ageInDays ${dayWord(ageInDays.toInt())}"
                    }
                    InfoRow(label = "Возраст", value = ageText)
                }
            }
        }

        // Заметки
        if (!animal.notes.isNullOrBlank()) {
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
                        text = animal.notes,
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
                    value = dateFormatter.format(Date(animal.createdAt))
                )
                InfoRow(
                    label = "Обновлено",
                    value = dateFormatter.format(Date(animal.updatedAt))
                )
            }
        }

        // История записей журнала (placeholder)
        AgroDiaryCard {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "История записей",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Записей журнала для этого животного пока нет",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Placeholder для фото животного.
 */
@Composable
private fun AnimalPhotoPlaceholder() {
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
                imageVector = Icons.Default.Pets,
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
private fun AnimalDetailScreenPreview() {
    AgroDiaryTheme {
        AnimalDetailContent(
            animal = AnimalEntity(
                id = 1,
                name = "Буренка",
                type = AnimalType.COW,
                breed = "Голштинская",
                birthDate = System.currentTimeMillis() - 365L * 24 * 60 * 60 * 1000, // 1 год назад
                gender = "Ж",
                weight = 450f,
                status = AnimalStatus.ACTIVE,
                notes = "Здоровая корова, регулярно даёт молоко.",
                createdAt = System.currentTimeMillis() - 365L * 24 * 60 * 60 * 1000,
                updatedAt = System.currentTimeMillis()
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AnimalDetailScreenMinimalPreview() {
    AgroDiaryTheme {
        AnimalDetailContent(
            animal = AnimalEntity(
                id = 2,
                name = "Козочка",
                type = AnimalType.GOAT,
                status = AnimalStatus.ACTIVE
            )
        )
    }
}
