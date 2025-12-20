package com.agrodiary.ui.journal

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.agrodiary.data.local.entity.AnimalEntity
import com.agrodiary.data.local.entity.JournalEntryEntity
import com.agrodiary.data.local.entity.JournalEntryType
import com.agrodiary.data.local.entity.StaffEntity
import com.agrodiary.ui.components.AgroDiaryTextField
import com.agrodiary.ui.components.AgroDiaryTopBar
import com.agrodiary.ui.components.DatePickerField
import com.agrodiary.ui.components.DropdownFieldNullable

@Composable
fun AddJournalEntryScreen(
    onNavigateBack: () -> Unit,
    onSaveSuccess: () -> Unit,
    viewModel: JournalViewModel = hiltViewModel()
) {
    var date by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var type by remember { mutableStateOf(JournalEntryType.OTHER) }
    var description by remember { mutableStateOf("") }
    var selectedAnimal by remember { mutableStateOf<AnimalEntity?>(null) }
    var selectedStaff by remember { mutableStateOf<StaffEntity?>(null) }
    var notes by remember { mutableStateOf("") }

    val animals by viewModel.animals.collectAsStateWithLifecycle()
    val staff by viewModel.staff.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            AgroDiaryTopBar(
                title = "Новая запись",
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
            DatePickerField(
                label = "Дата",
                selectedDate = date,
                onDateSelected = { it?.let { d -> date = d } }
            )
            Spacer(modifier = Modifier.height(16.dp))

             com.agrodiary.ui.components.DropdownField(
                label = "Тип события",
                items = JournalEntryType.values().toList(),
                selectedItem = type,
                onItemSelected = { type = it },
                itemLabel = { it.displayName }
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            AgroDiaryTextField(
                value = description,
                onValueChange = { description = it },
                label = "Описание",
                modifier = Modifier.fillMaxWidth(),
                singleLine = false,
                minLines = 2
            )
            Spacer(modifier = Modifier.height(16.dp))

            DropdownFieldNullable(
                label = "Животное (опционально)",
                items = animals,
                selectedItem = selectedAnimal,
                onItemSelected = { selectedAnimal = it },
                itemLabel = { "${it.name} (${it.type.displayName})" }
            )
            Spacer(modifier = Modifier.height(16.dp))

            DropdownFieldNullable(
                label = "Сотрудник (опционально)",
                items = staff,
                selectedItem = selectedStaff,
                onItemSelected = { selectedStaff = it },
                itemLabel = { it.name }
            )
            Spacer(modifier = Modifier.height(16.dp))

            AgroDiaryTextField(
                value = notes,
                onValueChange = { notes = it },
                label = "Дополнительные заметки",
                modifier = Modifier.fillMaxWidth(),
                singleLine = false,
                minLines = 3
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val entry = JournalEntryEntity(
                        date = date,
                        entryType = type,
                        description = description,
                        relatedAnimalId = selectedAnimal?.id,
                        relatedStaffId = selectedStaff?.id,
                        notes = notes,
                        amount = null // Amount field unused in UI for now
                    )
                    viewModel.addEntry(entry, onSaveSuccess)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = description.isNotBlank()
            ) {
                Text("Сохранить")
            }
        }
    }
}