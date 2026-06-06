package com.agrodiary.ui.staff.components
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.PersonOff
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.agrodiary.data.local.entity.StaffStatus
import com.agrodiary.ui.theme.AgroDiaryTheme
@Composable
fun StaffStatusChip(
    status: StaffStatus?,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (icon, label) = getStatusIconAndLabel(status)
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(text = label) },
        leadingIcon = if (selected) {
            {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null
                )
            }
        } else {
            {
                Icon(
                    imageVector = icon,
                    contentDescription = null
                )
            }
        },
        modifier = modifier
    )
}
@Composable
fun StaffStatusFilterRow(
    selectedStatus: StaffStatus?,
    onStatusSelected: (StaffStatus?) -> Unit,
    modifier: Modifier = Modifier
) {
    val statuses: List<StaffStatus?> = listOf(null) + StaffStatus.entries
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = statuses,
            key = { it?.name ?: "ALL" }
        ) { status ->
            StaffStatusChip(
                status = status,
                selected = selectedStatus == status,
                onClick = { onStatusSelected(status) }
            )
        }
    }
}
private fun getStatusIconAndLabel(status: StaffStatus?): Pair<ImageVector, String> {
    return when (status) {
        null -> Icons.Default.Groups to "Все"
        StaffStatus.ACTIVE -> Icons.Default.Work to status.displayName
        StaffStatus.ON_VACATION -> Icons.Default.Groups to status.displayName
        StaffStatus.FIRED -> Icons.Default.PersonOff to status.displayName
    }
}
@Preview(showBackground = true)
@Composable
private fun StaffStatusChipSelectedPreview() {
    AgroDiaryTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StaffStatusChip(
                status = null,
                selected = true,
                onClick = {}
            )
            StaffStatusChip(
                status = StaffStatus.ACTIVE,
                selected = false,
                onClick = {}
            )
        }
    }
}
@Preview(showBackground = true)
@Composable
private fun StaffStatusFilterRowPreview() {
    AgroDiaryTheme {
        StaffStatusFilterRow(
            selectedStatus = StaffStatus.ACTIVE,
            onStatusSelected = {}
        )
    }
}
@Preview(showBackground = true)
@Composable
private fun StaffStatusFilterRowAllPreview() {
    AgroDiaryTheme {
        StaffStatusFilterRow(
            selectedStatus = null,
            onStatusSelected = {}
        )
    }
}
