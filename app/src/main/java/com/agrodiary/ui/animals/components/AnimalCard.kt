package com.agrodiary.ui.animals.components
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.agrodiary.data.local.entity.AnimalEntity
import com.agrodiary.data.local.entity.AnimalStatus
import com.agrodiary.data.local.entity.AnimalType
import com.agrodiary.ui.components.AgroDiaryCard
import com.agrodiary.ui.theme.AgroDiaryTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
@Composable
fun AnimalCard(
    animal: AnimalEntity,
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
            Surface(
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(48.dp)
            ) {
                if (!animal.photoUri.isNullOrBlank()) {
                    AsyncImage(
                        model = animal.photoUri,
                        contentDescription = "Фото ${animal.name}",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = getAnimalIcon(animal.type),
                        contentDescription = animal.type.displayName,
                        modifier = Modifier.padding(8.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = animal.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = animal.type.displayName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (!animal.breed.isNullOrBlank()) {
                        Text(
                            text = "•",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = animal.breed,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    animal.weight?.let { weight ->
                        Text(
                            text = "${weight.toInt()} кг",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (animal.weight != null && animal.birthDate != null) {
                        Text(
                            text = "•",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    animal.birthDate?.let { birthDate ->
                        val dateFormatter = SimpleDateFormat("dd.MM.yyyy", Locale("ru"))
                        Text(
                            text = dateFormatter.format(Date(birthDate)),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            AnimalStatusBadge(status = animal.status)
        }
    }
}
@Composable
private fun AnimalStatusBadge(
    status: AnimalStatus,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor) = when (status) {
        AnimalStatus.ACTIVE -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
        AnimalStatus.SICK -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
        AnimalStatus.SOLD -> MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
        AnimalStatus.DEAD -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
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
private fun getAnimalIcon(type: AnimalType) = when (type) {
    else -> Icons.Default.Pets
}
@Preview(showBackground = true)
@Composable
private fun AnimalCardPreview() {
    AgroDiaryTheme {
        AnimalCard(
            animal = AnimalEntity(
                id = 1,
                name = "Буренка",
                type = AnimalType.COW,
                breed = "Голштинская",
                birthDate = System.currentTimeMillis() - 365L * 24 * 60 * 60 * 1000,
                gender = "Ж",
                weight = 450f,
                status = AnimalStatus.ACTIVE,
                notes = "Здоровая корова"
            ),
            onClick = {}
        )
    }
}
@Preview(showBackground = true)
@Composable
private fun AnimalCardSickPreview() {
    AgroDiaryTheme {
        AnimalCard(
            animal = AnimalEntity(
                id = 2,
                name = "Петух",
                type = AnimalType.CHICKEN,
                breed = "Леггорн",
                birthDate = null,
                gender = "М",
                weight = 2.5f,
                status = AnimalStatus.SICK,
                notes = null
            ),
            onClick = {}
        )
    }
}
@Preview(showBackground = true)
@Composable
private fun AnimalCardMinimalPreview() {
    AgroDiaryTheme {
        AnimalCard(
            animal = AnimalEntity(
                id = 3,
                name = "Козочка",
                type = AnimalType.GOAT,
                breed = null,
                birthDate = null,
                gender = null,
                weight = null,
                status = AnimalStatus.ACTIVE,
                notes = null
            ),
            onClick = {}
        )
    }
}
