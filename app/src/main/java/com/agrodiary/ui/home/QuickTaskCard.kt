package com.agrodiary.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.agrodiary.data.local.entity.TaskEntity
import com.agrodiary.data.local.entity.TaskPriority
import com.agrodiary.data.local.entity.TaskStatus
import com.agrodiary.ui.theme.AgroDiaryTheme
import com.agrodiary.ui.theme.PriorityHigh
import com.agrodiary.ui.theme.PriorityLow
import com.agrodiary.ui.theme.PriorityMedium
import com.agrodiary.ui.theme.PriorityUrgent
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Компонент карточки срочной задачи для главного экрана.
 *
 * Отображает задачу с цветовым индикатором приоритета, названием,
 * описанием и сроком выполнения.
 *
 * @param task Задача для отображения
 * @param onClick Обработчик клика на карточку
 * @param modifier Модификатор для настройки внешнего вида
 */
@Composable
fun QuickTaskCard(
    task: TaskEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val priorityColor = when (task.priority) {
        TaskPriority.LOW -> PriorityLow
        TaskPriority.MEDIUM -> PriorityMedium
        TaskPriority.HIGH -> PriorityHigh
        TaskPriority.URGENT -> PriorityUrgent
    }

    val dateFormatter = SimpleDateFormat("dd MMM", Locale("ru"))
    val dueDateText = task.dueDate?.let { dateFormatter.format(Date(it)) } ?: "Без срока"

    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Цветовой индикатор приоритета
            Card(
                modifier = Modifier
                    .width(4.dp)
                    .height(56.dp),
                colors = CardDefaults.cardColors(
                    containerColor = priorityColor
                )
            ) {}

            // Иконка задачи
            Icon(
                imageVector = Icons.Default.Assignment,
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.CenterVertically),
                tint = priorityColor
            )

            // Информация о задаче
            Column(
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (!task.description.isNullOrBlank()) {
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }

                // Приоритет и срок выполнения
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = task.priority.displayName,
                        style = MaterialTheme.typography.labelSmall,
                        color = priorityColor,
                        fontWeight = FontWeight.Medium
                    )

                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )

                    Text(
                        text = dueDateText,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

@Preview(name = "Quick Task Card - Urgent", showBackground = true)
@Composable
private fun QuickTaskCardUrgentPreview() {
    AgroDiaryTheme {
        QuickTaskCard(
            task = TaskEntity(
                id = 1,
                title = "Срочная вакцинация коров",
                description = "Необходимо провести вакцинацию стада от ящура",
                priority = TaskPriority.URGENT,
                status = TaskStatus.NEW,
                dueDate = System.currentTimeMillis() + (24 * 60 * 60 * 1000) // Завтра
            ),
            onClick = {}
        )
    }
}

@Preview(name = "Quick Task Card - High", showBackground = true)
@Composable
private fun QuickTaskCardHighPreview() {
    AgroDiaryTheme {
        QuickTaskCard(
            task = TaskEntity(
                id = 2,
                title = "Проверить запасы корма",
                description = "Осталось мало сена в первом хранилище",
                priority = TaskPriority.HIGH,
                status = TaskStatus.IN_PROGRESS,
                dueDate = System.currentTimeMillis() + (2 * 24 * 60 * 60 * 1000) // Через 2 дня
            ),
            onClick = {}
        )
    }
}

@Preview(name = "Quick Task Card - Medium", showBackground = true)
@Composable
private fun QuickTaskCardMediumPreview() {
    AgroDiaryTheme {
        QuickTaskCard(
            task = TaskEntity(
                id = 3,
                title = "Осмотр молодняка",
                priority = TaskPriority.MEDIUM,
                status = TaskStatus.NEW,
                dueDate = System.currentTimeMillis() + (3 * 24 * 60 * 60 * 1000) // Через 3 дня
            ),
            onClick = {}
        )
    }
}

@Preview(name = "Quick Task Card - No Due Date", showBackground = true)
@Composable
private fun QuickTaskCardNoDueDatePreview() {
    AgroDiaryTheme {
        QuickTaskCard(
            task = TaskEntity(
                id = 4,
                title = "Подготовить отчет за месяц",
                description = "Собрать данные по всем животным и кормам",
                priority = TaskPriority.LOW,
                status = TaskStatus.NEW,
                dueDate = null
            ),
            onClick = {}
        )
    }
}
