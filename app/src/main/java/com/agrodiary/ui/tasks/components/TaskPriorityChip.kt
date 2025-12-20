package com.agrodiary.ui.tasks.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.agrodiary.data.local.entity.TaskPriority
import com.agrodiary.ui.theme.PriorityHigh
import com.agrodiary.ui.theme.PriorityLow
import com.agrodiary.ui.theme.PriorityMedium
import com.agrodiary.ui.theme.PriorityUrgent

@Composable
fun TaskPriorityChip(
    priority: TaskPriority,
    modifier: Modifier = Modifier
) {
    val (color, text) = when (priority) {
        TaskPriority.LOW -> PriorityLow to "Низкий"
        TaskPriority.MEDIUM -> PriorityMedium to "Средний"
        TaskPriority.HIGH -> PriorityHigh to "Высокий"
        TaskPriority.URGENT -> PriorityUrgent to "Срочно"
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
