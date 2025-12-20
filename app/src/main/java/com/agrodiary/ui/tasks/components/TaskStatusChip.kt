package com.agrodiary.ui.tasks.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.agrodiary.data.local.entity.TaskStatus
import com.agrodiary.ui.theme.StatusCancelled
import com.agrodiary.ui.theme.StatusCompleted
import com.agrodiary.ui.theme.StatusInProgress
import com.agrodiary.ui.theme.StatusNew

@Composable
fun TaskStatusChip(
    status: TaskStatus,
    modifier: Modifier = Modifier
) {
    val (color, text) = when (status) {
        TaskStatus.NEW -> StatusNew to "Новая"
        TaskStatus.IN_PROGRESS -> StatusInProgress to "В работе"
        TaskStatus.COMPLETED -> StatusCompleted to "Готово"
        TaskStatus.CANCELLED -> StatusCancelled to "Отмена"
    }

    AssistChip(
        onClick = {},
        label = { Text(text = text, color = color) },
        border = BorderStroke(1.dp, color),
        colors = AssistChipDefaults.assistChipColors(
            containerColor = color.copy(alpha = 0.1f),
            labelColor = color
        ),
        modifier = modifier
    )
}
