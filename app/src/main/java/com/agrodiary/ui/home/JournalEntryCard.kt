package com.agrodiary.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Event
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
import com.agrodiary.data.local.entity.JournalEntryEntity
import com.agrodiary.data.local.entity.JournalEntryType
import com.agrodiary.ui.theme.AgroDiaryTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Компонент карточки записи журнала для главного экрана.
 *
 * Отображает краткую информацию о записи журнала, включая
 * тип события, описание и дату.
 *
 * @param entry Запись журнала
 * @param onClick Обработчик клика на карточку
 * @param modifier Модификатор для настройки внешнего вида
 */
@Composable
fun JournalEntryCard(
    entry: JournalEntryEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormatter = SimpleDateFormat("dd MMM, HH:mm", Locale("ru"))
    val dateText = dateFormatter.format(Date(entry.date))

    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Иконка записи
            Icon(
                imageVector = Icons.Default.Book,
                contentDescription = null,
                modifier = Modifier
                    .size(36.dp)
                    .align(Alignment.CenterVertically),
                tint = MaterialTheme.colorScheme.primary
            )

            // Информация о записи
            Column(
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Тип записи
                Text(
                    text = entry.entryType.displayName,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )

                // Описание
                Text(
                    text = entry.description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Дата и время
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Event,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                    Text(
                        text = dateText,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )

                    // Сумма, если есть
                    if (entry.amount != null) {
                        Text(
                            text = "•",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                        Text(
                            text = "${entry.amount} руб.",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.tertiary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Preview(name = "Journal Entry Card - Feeding", showBackground = true)
@Composable
private fun JournalEntryCardFeedingPreview() {
    AgroDiaryTheme {
        JournalEntryCard(
            entry = JournalEntryEntity(
                id = 1,
                date = System.currentTimeMillis(),
                entryType = JournalEntryType.FEEDING,
                description = "Утреннее кормление коров, выдано 50 кг сена",
                relatedAnimalId = 1
            ),
            onClick = {}
        )
    }
}

@Preview(name = "Journal Entry Card - Health Check", showBackground = true)
@Composable
private fun JournalEntryCardHealthPreview() {
    AgroDiaryTheme {
        JournalEntryCard(
            entry = JournalEntryEntity(
                id = 2,
                date = System.currentTimeMillis() - (2 * 60 * 60 * 1000), // 2 часа назад
                entryType = JournalEntryType.HEALTH_CHECK,
                description = "Ветеринарный осмотр всего стада. Все животные здоровы",
                relatedStaffId = 1
            ),
            onClick = {}
        )
    }
}

@Preview(name = "Journal Entry Card - Sale", showBackground = true)
@Composable
private fun JournalEntryCardSalePreview() {
    AgroDiaryTheme {
        JournalEntryCard(
            entry = JournalEntryEntity(
                id = 3,
                date = System.currentTimeMillis() - (24 * 60 * 60 * 1000), // Вчера
                entryType = JournalEntryType.SALE,
                description = "Продано 100 литров молока местному магазину",
                amount = 5000.0,
                notes = "Оплата наличными"
            ),
            onClick = {}
        )
    }
}

@Preview(name = "Journal Entry Card - Vaccination", showBackground = true)
@Composable
private fun JournalEntryCardVaccinationPreview() {
    AgroDiaryTheme {
        JournalEntryCard(
            entry = JournalEntryEntity(
                id = 4,
                date = System.currentTimeMillis() - (3 * 24 * 60 * 60 * 1000), // 3 дня назад
                entryType = JournalEntryType.VACCINATION,
                description = "Вакцинация молодняка от ящура",
                relatedAnimalId = 5,
                relatedStaffId = 2,
                amount = 1500.0
            ),
            onClick = {}
        )
    }
}

@Preview(name = "Journal Entry Card - Birth", showBackground = true)
@Composable
private fun JournalEntryCardBirthPreview() {
    AgroDiaryTheme {
        JournalEntryCard(
            entry = JournalEntryEntity(
                id = 5,
                date = System.currentTimeMillis() - (5 * 24 * 60 * 60 * 1000), // 5 дней назад
                entryType = JournalEntryType.BIRTH,
                description = "Родился телёнок у коровы Зорька. Пол: женский, вес: 35 кг",
                relatedAnimalId = 3,
                notes = "Мать и телёнок чувствуют себя хорошо"
            ),
            onClick = {}
        )
    }
}
