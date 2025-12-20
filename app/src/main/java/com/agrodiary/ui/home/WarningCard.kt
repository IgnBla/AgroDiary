package com.agrodiary.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.agrodiary.data.local.entity.FeedCategory
import com.agrodiary.data.local.entity.FeedStockEntity
import com.agrodiary.data.local.entity.MeasureUnit
import com.agrodiary.ui.theme.AgroDiaryTheme
import kotlin.math.min

/**
 * Компонент карточки предупреждения о низком запасе корма.
 *
 * Отображает информацию о корме с низким запасом, включая
 * название, текущее количество, минимальное количество и
 * визуальный индикатор уровня запаса.
 *
 * @param feedStock Корм с низким запасом
 * @param onClick Обработчик клика на карточку
 * @param modifier Модификатор для настройки внешнего вида
 */
@Composable
fun WarningCard(
    feedStock: FeedStockEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val stockLevel = if (feedStock.minQuantity > 0) {
        min(feedStock.currentQuantity / feedStock.minQuantity, 1.0).toFloat()
    } else {
        1f
    }

    val warningColor = when {
        stockLevel <= 0.25f -> MaterialTheme.colorScheme.error
        stockLevel <= 0.5f -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.primary
    }

    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Иконка предупреждения
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.CenterVertically),
                tint = warningColor
            )

            // Информация о запасе
            Column(
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Название корма
                Text(
                    text = feedStock.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Количество
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Текущий запас:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${feedStock.currentQuantity} ${feedStock.unit.shortName}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = warningColor
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Минимальный:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${feedStock.minQuantity} ${feedStock.unit.shortName}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Индикатор уровня запаса
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    LinearProgressIndicator(
                        progress = { stockLevel },
                        modifier = Modifier.fillMaxWidth(),
                        color = warningColor,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                    Text(
                        text = "Уровень запаса: ${(stockLevel * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Preview(name = "Warning Card - Critical Low", showBackground = true)
@Composable
private fun WarningCardCriticalPreview() {
    AgroDiaryTheme {
        WarningCard(
            feedStock = FeedStockEntity(
                id = 1,
                name = "Сено луговое",
                category = FeedCategory.HAY,
                currentQuantity = 50.0,
                unit = MeasureUnit.KILOGRAM,
                minQuantity = 200.0,
                notes = "Срочно необходимо пополнение"
            ),
            onClick = {}
        )
    }
}

@Preview(name = "Warning Card - Low", showBackground = true)
@Composable
private fun WarningCardLowPreview() {
    AgroDiaryTheme {
        WarningCard(
            feedStock = FeedStockEntity(
                id = 2,
                name = "Комбикорм для кур",
                category = FeedCategory.MIXED_FEED,
                currentQuantity = 80.0,
                unit = MeasureUnit.KILOGRAM,
                minQuantity = 150.0,
                notes = "Скоро закончится"
            ),
            onClick = {}
        )
    }
}

@Preview(name = "Warning Card - Medium", showBackground = true)
@Composable
private fun WarningCardMediumPreview() {
    AgroDiaryTheme {
        WarningCard(
            feedStock = FeedStockEntity(
                id = 3,
                name = "Зерно пшеницы",
                category = FeedCategory.GRAIN,
                currentQuantity = 300.0,
                unit = MeasureUnit.KILOGRAM,
                minQuantity = 500.0
            ),
            onClick = {}
        )
    }
}

@Preview(name = "Warning Card - Ton Unit", showBackground = true)
@Composable
private fun WarningCardTonPreview() {
    AgroDiaryTheme {
        WarningCard(
            feedStock = FeedStockEntity(
                id = 4,
                name = "Силос кукурузный",
                category = FeedCategory.OTHER,
                currentQuantity = 1.2,
                unit = MeasureUnit.TON,
                minQuantity = 5.0,
                notes = "Запас на зиму заканчивается"
            ),
            onClick = {}
        )
    }
}
