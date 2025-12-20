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
import androidx.compose.runtime.LaunchedEffect
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
fun AddEditJournalEntryScreen(
    entryId: Long?,
    onNavigateBack: () -> Unit,
    onSaveSuccess: () -> Unit,
    viewModel: JournalViewModel = hiltViewModel()
) {
    var date by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var type by remember { mutableStateOf(JournalEntryType.OTHER) }
    var description by remember { mutableStateOf("") }
    var selectedAnimalId by remember { mutableStateOf<Long?>(null) }
    var selectedStaffId by remember { mutableStateOf<Long?>(null) }
    var notes by remember { mutableStateOf("") }
    
    val animals by viewModel.animals.collectAsStateWithLifecycle()
    val staff by viewModel.staff.collectAsStateWithLifecycle()
    
    val isEditMode = entryId != null

    LaunchedEffect(entryId) {
        if (isEditMode) {
            val entry = viewModel.getEntryById(entryId!!)
            entry?.let {
                date = it.date
                type = it.entryType
                description = it.description
                selectedAnimalId = it.relatedAnimalId
                selectedStaffId = it.relatedStaffId
                notes = it.notes ?: ""
            }
        }
    }

    Scaffold(
        topBar = {
            AgroDiaryTopBar(
                title = if (isEditMode) "Редактировать запись" else "Новая запись",
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

            val selectedAnimal = animals.find { it.id == selectedAnimalId }
            DropdownFieldNullable(
                label = "Животное (опционально)",
                items = animals,
                selectedItem = selectedAnimal,
                onItemSelected = { selectedAnimalId = it?.id },
                itemLabel = { "${it.name} (${it.type.displayName})" }
            )
            Spacer(modifier = Modifier.height(16.dp))

            val selectedStaff = staff.find { it.id == selectedStaffId }
            DropdownFieldNullable(
                label = "Сотрудник (опционально)",
                items = staff,
                selectedItem = selectedStaff,
                onItemSelected = { selectedStaffId = it?.id },
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
                        id = entryId ?: 0,
                        date = date,
                        entryType = type,
                        description = description,
                        relatedAnimalId = selectedAnimalId,
                        relatedStaffId = selectedStaffId,
                        notes = notes,
                        amount = null
                    )
                    if (isEditMode) {
                        viewModel.updateEntry(entry, onSaveSuccess)
                    } else {
                        viewModel.addEntry(entry, onSaveSuccess)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = description.isNotBlank()
            ) {
                Text("Сохранить")
            }
        }
    }
}