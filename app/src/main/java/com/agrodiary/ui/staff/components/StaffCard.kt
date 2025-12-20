package com.agrodiary.ui.staff.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.agrodiary.data.local.entity.StaffEntity
import com.agrodiary.data.local.entity.StaffStatus
import com.agrodiary.ui.components.AgroDiaryCard
import com.agrodiary.ui.theme.AgroDiaryTheme

/**
 * Карточка сотрудника для отображения в списке.
 *
 * Отображает основную информацию о сотруднике:
 * - Фото сотрудника (или иконка)
 * - Имя
 * - Должность
 * - Телефон и email (если есть)
 * - Статус (цветной badge)
 *
 * @param staff Данные сотрудника
 * @param onClick Обработчик клика по карточке
 * @param modifier Модификатор
 */
@Composable
fun StaffCard(
    staff: StaffEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AgroDiaryCard(
        onClick = onClick,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Фото сотрудника
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier.size(48.dp)
            ) {
                if (!staff.photoUri.isNullOrBlank()) {
                    AsyncImage(
                        model = staff.photoUri,
                        contentDescription = "Фото ${staff.name}",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Сотрудник",
                        modifier = Modifier.padding(8.dp),
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            // Информация о сотруднике
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Имя
                Text(
                    text = staff.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Должность
                if (!staff.position.isNullOrBlank()) {
                    Text(
                        text = staff.position,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Контактная информация
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Телефон
                    if (!staff.phone.isNullOrBlank()) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = "Телефон",
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = staff.phone,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Email
                    if (!staff.email.isNullOrBlank()) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = "Email",
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = staff.email,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            // Статус
            StaffStatusBadge(status = staff.status)
        }
    }
}

/**
 * Badge со статусом сотрудника.
 */
@Composable
private fun StaffStatusBadge(
    status: StaffStatus,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor) = when (status) {
        StaffStatus.ACTIVE -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
        StaffStatus.ON_VACATION -> MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
        StaffStatus.FIRED -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
    }

    Surface(
        shape = MaterialTheme.shapes.small,
        color = backgroundColor,
        modifier = modifier
    ) {
        Text(
            text = status.displayName,
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

// PREVIEWS

@Preview(showBackground = true)
@Composable
private fun StaffCardPreview() {
    AgroDiaryTheme {
        StaffCard(
            staff = StaffEntity(
                id = 1,
                name = "Иван Петров",
                position = "Управляющий фермой",
                phone = "+7 (999) 123-45-67",
                email = "ivan@example.com",
                hireDate = System.currentTimeMillis(),
                salary = 50000.0,
                status = StaffStatus.ACTIVE
            ),
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun StaffCardOnVacationPreview() {
    AgroDiaryTheme {
        StaffCard(
            staff = StaffEntity(
                id = 2,
                name = "Мария Сидорова",
                position = "Ветеринар",
                phone = "+7 (999) 987-65-43",
                email = null,
                status = StaffStatus.ON_VACATION
            ),
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun StaffCardMinimalPreview() {
    AgroDiaryTheme {
        StaffCard(
            staff = StaffEntity(
                id = 3,
                name = "Петр Иванов",
                position = null,
                phone = null,
                email = null,
                status = StaffStatus.ACTIVE
            ),
            onClick = {}
        )
    }
}

/**
 * Badge со статусом сотрудника.
 */
@Composable
private fun StaffStatusBadge(
    status: StaffStatus,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor) = when (status) {
        StaffStatus.ACTIVE -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
        StaffStatus.ON_VACATION -> MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
        StaffStatus.FIRED -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
    }

    Surface(
        shape = MaterialTheme.shapes.small,
        color = backgroundColor,
        modifier = modifier
    ) {
        Text(
            text = status.displayName,
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

// PREVIEWS

@Preview(showBackground = true)
@Composable
private fun StaffCardPreview() {
    AgroDiaryTheme {
        StaffCard(
            staff = StaffEntity(
                id = 1,
                name = "Иван Петров",
                position = "Управляющий фермой",
                phone = "+7 (999) 123-45-67",
                email = "ivan@example.com",
                hireDate = System.currentTimeMillis(),
                salary = 50000.0,
                status = StaffStatus.ACTIVE
            ),
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun StaffCardOnVacationPreview() {
    AgroDiaryTheme {
        StaffCard(
            staff = StaffEntity(
                id = 2,
                name = "Мария Сидорова",
                position = "Ветеринар",
                phone = "+7 (999) 987-65-43",
                email = null,
                status = StaffStatus.ON_VACATION
            ),
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun StaffCardMinimalPreview() {
    AgroDiaryTheme {
        StaffCard(
            staff = StaffEntity(
                id = 3,
                name = "Петр Иванов",
                position = null,
                phone = null,
                email = null,
                status = StaffStatus.ACTIVE
            ),
            onClick = {}
        )
    }
}
