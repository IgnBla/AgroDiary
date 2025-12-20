package com.agrodiary.ui.feed

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.agrodiary.data.local.entity.FeedCategory
import com.agrodiary.data.local.entity.FeedStockEntity
import com.agrodiary.data.local.entity.MeasureUnit
import com.agrodiary.ui.components.AgroDiaryTextField
import com.agrodiary.ui.components.AgroDiaryTopBar
import com.agrodiary.ui.components.DropdownField

@Composable
fun AddEditFeedScreen(
    feedId: Long?,
    onNavigateBack: () -> Unit,
    onSaveSuccess: () -> Unit,
    viewModel: FeedStockViewModel = hiltViewModel()
) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(FeedCategory.HAY) }
    var currentQuantity by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf(MeasureUnit.KILOGRAM) }
    var minQuantity by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    
    var isEditing by remember { mutableStateOf(false) }
    var existingFeed by remember { mutableStateOf<FeedStockEntity?>(null) }

    LaunchedEffect(feedId) {
        if (feedId != null) {
            isEditing = true
            existingFeed = viewModel.getFeedStockById(feedId)
            existingFeed?.let {
                name = it.name
                category = it.category
                currentQuantity = it.currentQuantity.toString()
                unit = it.unit
                minQuantity = it.minQuantity.toString()
                notes = it.notes ?: ""
            }
        }
    }

    Scaffold(
        topBar = {
            AgroDiaryTopBar(
                title = if (isEditing) "Редактировать корм" else "Добавить корм",
                onBackClick = onNavigateBack
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            AgroDiaryTextField(
                value = name,
                onValueChange = { name = it },
                label = "Название",
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            DropdownField(
                label = "Категория",
                items = FeedCategory.values().toList(),
                selectedItem = category,
                onItemSelected = { category = it },
                itemLabel = { it.displayName }
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            AgroDiaryTextField(
                value = currentQuantity,
                onValueChange = { if (it.all { char -> char.isDigit() || char == '.' }) currentQuantity = it },
                label = "Текущий запас",
                keyboardType = KeyboardType.Number,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            DropdownField(
                label = "Единица измерения",
                items = MeasureUnit.values().toList(),
                selectedItem = unit,
                onItemSelected = { unit = it },
                itemLabel = { "${it.displayName} (${it.shortName})" }
            )
            Spacer(modifier = Modifier.height(16.dp))

            AgroDiaryTextField(
                value = minQuantity,
                onValueChange = { if (it.all { char -> char.isDigit() || char == '.' }) minQuantity = it },
                label = "Минимальный запас (для предупреждений)",
                keyboardType = KeyboardType.Number,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            AgroDiaryTextField(
                value = notes,
                onValueChange = { notes = it },
                label = "Примечание",
                modifier = Modifier.fillMaxWidth(),
                singleLine = false,
                minLines = 3
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = {
                    val feed = if (isEditing && existingFeed != null) {
                        existingFeed!!.copy(
                            name = name,
                            category = category,
                            currentQuantity = currentQuantity.toDoubleOrNull() ?: 0.0,
                            unit = unit,
                            minQuantity = minQuantity.toDoubleOrNull() ?: 0.0,
                            notes = notes,
                            lastUpdated = System.currentTimeMillis()
                        )
                    } else {
                        FeedStockEntity(
                            name = name,
                            category = category,
                            currentQuantity = currentQuantity.toDoubleOrNull() ?: 0.0,
                            unit = unit,
                            minQuantity = minQuantity.toDoubleOrNull() ?: 0.0,
                            notes = notes
                        )
                    }
                    
                    if (isEditing) {
                        viewModel.updateFeedStock(feed, onSaveSuccess)
                    } else {
                        viewModel.addFeedStock(feed, onSaveSuccess)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = name.isNotBlank() && currentQuantity.isNotBlank()
            ) {
                Text("Сохранить")
            }
        }
    }
}
