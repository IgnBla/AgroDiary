package com.agrodiary.ui.animals.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.agrodiary.data.local.entity.AnimalType
import com.agrodiary.ui.theme.AgroDiaryTheme

/**
 * FilterChip для фильтрации по типу животного.
 *
 * @param type Тип животного
 * @param selected Выбран ли этот тип
 * @param onClick Обработчик клика
 * @param modifier Модификатор
 */
@Composable
fun AnimalTypeChip(
    type: AnimalType,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(
                text = type.displayName,
                style = MaterialTheme.typography.labelLarge
            )
        },
        leadingIcon = if (selected) {
            {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Выбрано",
                    modifier = Modifier.padding(end = 4.dp)
                )
            }
        } else null,
        modifier = modifier,
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
            selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}

/**
 * Горизонтальный список фильтров по типам животных.
 *
 * @param selectedType Выбранный тип (null = все типы)
 * @param onTypeSelected Обработчик выбора типа (null для сброса фильтра)
 * @param modifier Модификатор
 */
@Composable
fun AnimalTypeFilterRow(
    selectedType: AnimalType?,
    onTypeSelected: (AnimalType?) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Кнопка "Все"
        item {
            FilterChip(
                selected = selectedType == null,
                onClick = { onTypeSelected(null) },
                label = {
                    Text(
                        text = "Все",
                        style = MaterialTheme.typography.labelLarge
                    )
                },
                leadingIcon = if (selectedType == null) {
                    {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Выбрано",
                            modifier = Modifier.padding(end = 4.dp)
                        )
                    }
                } else null,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }

        // Кнопки для каждого типа животного
        items(AnimalType.entries) { type ->
            AnimalTypeChip(
                type = type,
                selected = selectedType == type,
                onClick = {
                    onTypeSelected(if (selectedType == type) null else type)
                }
            )
        }
    }
}

// PREVIEWS

@Preview(showBackground = true)
@Composable
private fun AnimalTypeChipSelectedPreview() {
    AgroDiaryTheme {
        AnimalTypeChip(
            type = AnimalType.COW,
            selected = true,
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AnimalTypeChipUnselectedPreview() {
    AgroDiaryTheme {
        AnimalTypeChip(
            type = AnimalType.CHICKEN,
            selected = false,
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AnimalTypeFilterRowPreview() {
    AgroDiaryTheme {
        AnimalTypeFilterRow(
            selectedType = AnimalType.COW,
            onTypeSelected = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AnimalTypeFilterRowAllPreview() {
    AgroDiaryTheme {
        AnimalTypeFilterRow(
            selectedType = null,
            onTypeSelected = {}
        )
    }
}
