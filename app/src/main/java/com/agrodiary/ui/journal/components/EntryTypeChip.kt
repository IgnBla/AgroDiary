package com.agrodiary.ui.journal.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.agrodiary.data.local.entity.JournalEntryType

@Composable
fun EntryTypeChip(
    type: JournalEntryType,
    modifier: Modifier = Modifier
) {
    AssistChip(
        onClick = {},
        label = { Text(text = type.displayName) }, // Assuming displayName exists or similar
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = modifier
    )
}
