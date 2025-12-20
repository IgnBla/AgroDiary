package com.agrodiary.ui.animals

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.agrodiary.data.local.entity.AnimalEntity
import com.agrodiary.data.local.entity.AnimalStatus
import com.agrodiary.data.local.entity.AnimalType
import com.agrodiary.ui.components.AgroDiaryButton
import com.agrodiary.ui.components.AgroDiaryMultilineTextField
import com.agrodiary.ui.components.AgroDiaryOutlinedButton
import com.agrodiary.ui.components.AgroDiaryTextField
import com.agrodiary.ui.components.AgroDiaryTopBar
import com.agrodiary.ui.components.DatePickerField
import com.agrodiary.ui.components.DropdownField
import com.agrodiary.ui.components.ImagePickerField
import com.agrodiary.ui.theme.AgroDiaryTheme
import kotlinx.coroutines.launch
import android.net.Uri

import com.agrodiary.common.ValidationUtils

/**
 * Экран добавления/редактирования животного.
 *
 * Режимы работы:
 * - Добавление: animalId = null
 * - Редактирование: animalId = ID существующего животного
 *
 * @param animalId ID животного для редактирования (null для добавления)
 * @param onNavigateBack Обработчик возврата назад
 * @param onSaveSuccess Обработчик успешного сохранения
 * @param viewModel ViewModel
 * @param modifier Модификатор
 */
@Composable
fun AddEditAnimalScreen(
    animalId: Long?,
    onNavigateBack: () -> Unit,
    onSaveSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AnimalsViewModel = hiltViewModel()
) {
    val isEditMode = animalId != null
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()

    // Состояние формы
    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf<AnimalType?>(null) }
    var breed by remember { mutableStateOf("") }
    var birthDate by remember { mutableLongStateOf(0L) }
    var gender by remember { mutableStateOf<String?>(null) }
    var weight by remember { mutableFloatStateOf(0f) }
    var weightText by remember { mutableStateOf("") }
    var status by remember { mutableStateOf(AnimalStatus.ACTIVE) }
    var notes by remember { mutableStateOf("") }
    var photoUri by remember { mutableStateOf<Uri?>(null) }

    // Ошибки валидации
    var nameError by remember { mutableStateOf(false) }
    var typeError by remember { mutableStateOf(false) }
    var weightError by remember { mutableStateOf(false) }
    var birthDateError by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    // Загрузка данных для редактирования
    LaunchedEffect(animalId) {
        if (isEditMode && animalId != null) {
            scope.launch {
                val animal = viewModel.getAnimalById(animalId)
                animal?.let {
                    name = it.name
                    type = it.type
                    breed = it.breed ?: ""
                    birthDate = it.birthDate ?: 0L
                    gender = it.gender
                    weight = it.weight ?: 0f
                    weightText = it.weight?.toString() ?: ""
                    status = it.status
                    notes = it.notes ?: ""
                    photoUri = it.photoUri?.let { uri -> Uri.parse(uri) }
                }
            }
        }
    }

    // Обработка ошибок и успешных сообщений
    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.clearError()
        }
    }

    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearSuccessMessage()
            onSaveSuccess()
        }
    }

    Scaffold(
        topBar = {
            AgroDiaryTopBar(
                title = if (isEditMode) "Редактировать животное" else "Добавить животное",
                onBackClick = onNavigateBack
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = modifier
    ) { padding ->
        if (uiState.isLoading) {
            // Индикатор загрузки
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            // Форма
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Секция: Основная информация
                Text(
                    text = "Основная информация",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                // Имя (обязательное)
                AgroDiaryTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        nameError = !ValidationUtils.isValidName(it)
                    },
                    label = "Имя *",
                    placeholder = "Введите имя животного",
                    isError = nameError,
                    errorMessage = if (nameError) "Имя должно быть не менее 2 символов" else null,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                )

                // Фото животного
                ImagePickerField(
                    selectedImageUri = photoUri,
                    onImageSelected = { photoUri = it },
                    label = "Фото животного"
                )

                // Тип (обязательное)
                DropdownField(
                    selectedItem = type,
                    items = AnimalType.entries,
                    onItemSelected = {
                        type = it
                        typeError = false
                    },
                    itemLabel = { it.displayName },
                    label = "Тип *",
                    placeholder = "Выберите тип животного",
                    isError = typeError,
                    errorMessage = if (typeError) "Тип обязателен" else null
                )

                // Порода
                AgroDiaryTextField(
                    value = breed,
                    onValueChange = { breed = it },
                    label = "Порода",
                    placeholder = "Введите породу",
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Секция: Физические параметры
                Text(
                    text = "Физические параметры",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                // Дата рождения
                DatePickerField(
                    selectedDate = if (birthDate > 0) birthDate else null,
                    onDateSelected = {
                        birthDate = it ?: 0L
                        birthDateError = it != null && it > System.currentTimeMillis()
                    },
                    label = "Дата рождения",
                    isError = birthDateError,
                    errorMessage = if (birthDateError) "Дата не может быть в будущем" else null
                )

                // Пол
                DropdownField(
                    selectedItem = gender,
                    items = listOf("М", "Ж"),
                    onItemSelected = { gender = it },
                    itemLabel = { if (it == "М") "Мужской" else "Женский" },
                    label = "Пол",
                    placeholder = "Выберите пол"
                )

                // Вес
                AgroDiaryTextField(
                    value = weightText,
                    onValueChange = {
                        weightText = it
                        val w = it.toFloatOrNull()
                        if (w != null) {
                            weight = w
                            weightError = !ValidationUtils.isValidWeight(w)
                        } else {
                            weightError = it.isNotEmpty()
                        }
                    },
                    label = "Вес (кг)",
                    placeholder = "Введите вес",
                    isError = weightError,
                    errorMessage = if (weightError) "Введите корректный вес" else null,
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Next
                )

                // Статус
                DropdownField(
                    selectedItem = status,
                    items = AnimalStatus.entries,
                    onItemSelected = { status = it },
                    itemLabel = { it.displayName },
                    label = "Статус"
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Секция: Дополнительно
                Text(
                    text = "Дополнительно",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                // Заметки
                AgroDiaryMultilineTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = "Заметки",
                    placeholder = "Дополнительная информация",
                    minLines = 3,
                    maxLines = 5
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Кнопки действий
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Отмена
                    AgroDiaryOutlinedButton(
                        text = "Отмена",
                        onClick = onNavigateBack,
                        modifier = Modifier.weight(1f)
                    )

                    // Сохранить
                    AgroDiaryButton(
                        text = if (isEditMode) "Сохранить" else "Добавить",
                        onClick = {
                            // Валидация
                            nameError = !ValidationUtils.isValidName(name)
                            typeError = type == null
                            
                            val w = weightText.toFloatOrNull()
                            weightError = weightText.isNotEmpty() && (w == null || !ValidationUtils.isValidWeight(w))
                            
                            birthDateError = birthDate > System.currentTimeMillis()

                            if (!nameError && !typeError && !weightError && !birthDateError) {
                                val animal = AnimalEntity(
                                    id = animalId ?: 0,
                                    name = name.trim(),
                                    type = type!!,
                                    breed = breed.trim().ifBlank { null },
                                    birthDate = if (birthDate > 0) birthDate else null,
                                    gender = gender,
                                    weight = if (weight > 0) weight else null,
                                    status = status,
                                    notes = notes.trim().ifBlank { null },
                                    photoUri = photoUri?.toString(),
                                    createdAt = if (isEditMode) 0 else System.currentTimeMillis(),
                                    updatedAt = System.currentTimeMillis()
                                )

                                if (isEditMode) {
                                    viewModel.updateAnimal(animal)
                                } else {
                                    viewModel.addAnimal(animal)
                                }
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = !uiState.isLoading
                    )
                }
            }
        }
    }
}

// PREVIEWS

@Preview(showBackground = true)
@Composable
private fun AddEditAnimalScreenAddPreview() {
    AgroDiaryTheme {
        AddEditAnimalScreenContent(
            isEditMode = false,
            name = "",
            type = null,
            breed = "",
            birthDate = 0L,
            gender = null,
            weightText = "",
            status = AnimalStatus.ACTIVE,
            notes = "",
            nameError = false,
            typeError = false,
            onNavigateBack = {},
            onNameChange = {},
            onTypeChange = {},
            onBreedChange = {},
            onBirthDateChange = {},
            onGenderChange = {},
            onWeightChange = {},
            onStatusChange = {},
            onNotesChange = {},
            onSave = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AddEditAnimalScreenEditPreview() {
    AgroDiaryTheme {
        AddEditAnimalScreenContent(
            isEditMode = true,
            name = "Буренка",
            type = AnimalType.COW,
            breed = "Голштинская",
            birthDate = System.currentTimeMillis(),
            gender = "Ж",
            weightText = "450",
            status = AnimalStatus.ACTIVE,
            notes = "Здоровая корова",
            nameError = false,
            typeError = false,
            onNavigateBack = {},
            onNameChange = {},
            onTypeChange = {},
            onBreedChange = {},
            onBirthDateChange = {},
            onGenderChange = {},
            onWeightChange = {},
            onStatusChange = {},
            onNotesChange = {},
            onSave = {}
        )
    }
}

/**
 * Вспомогательный Composable для Preview.
 */
@Composable
private fun AddEditAnimalScreenContent(
    isEditMode: Boolean,
    name: String,
    type: AnimalType?,
    breed: String,
    birthDate: Long,
    gender: String?,
    weightText: String,
    status: AnimalStatus,
    notes: String,
    nameError: Boolean,
    typeError: Boolean,
    onNavigateBack: () -> Unit,
    onNameChange: (String) -> Unit,
    onTypeChange: (AnimalType?) -> Unit,
    onBreedChange: (String) -> Unit,
    onBirthDateChange: (Long?) -> Unit,
    onGenderChange: (String?) -> Unit,
    onWeightChange: (String) -> Unit,
    onStatusChange: (AnimalStatus) -> Unit,
    onNotesChange: (String) -> Unit,
    onSave: () -> Unit
) {
    Scaffold(
        topBar = {
            AgroDiaryTopBar(
                title = if (isEditMode) "Редактировать животное" else "Добавить животное",
                onBackClick = onNavigateBack
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Основная информация",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            AgroDiaryTextField(
                value = name,
                onValueChange = onNameChange,
                label = "Имя *",
                placeholder = "Введите имя животного",
                isError = nameError,
                errorMessage = if (nameError) "Имя обязательно" else null
            )

            DropdownField(
                selectedItem = type,
                items = AnimalType.entries,
                onItemSelected = onTypeChange,
                itemLabel = { it.displayName },
                label = "Тип *",
                placeholder = "Выберите тип животного",
                isError = typeError,
                errorMessage = if (typeError) "Тип обязателен" else null
            )

            AgroDiaryTextField(
                value = breed,
                onValueChange = onBreedChange,
                label = "Порода",
                placeholder = "Введите породу"
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Физические параметры",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            DatePickerField(
                selectedDate = if (birthDate > 0) birthDate else null,
                onDateSelected = onBirthDateChange,
                label = "Дата рождения"
            )

            DropdownField(
                selectedItem = gender,
                items = listOf("М", "Ж"),
                onItemSelected = onGenderChange,
                itemLabel = { if (it == "М") "Мужской" else "Женский" },
                label = "Пол",
                placeholder = "Выберите пол"
            )

            AgroDiaryTextField(
                value = weightText,
                onValueChange = onWeightChange,
                label = "Вес (кг)",
                placeholder = "Введите вес"
            )

            DropdownField(
                selectedItem = status,
                items = AnimalStatus.entries,
                onItemSelected = onStatusChange,
                itemLabel = { it.displayName },
                label = "Статус"
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Дополнительно",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            AgroDiaryMultilineTextField(
                value = notes,
                onValueChange = onNotesChange,
                label = "Заметки",
                placeholder = "Дополнительная информация"
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AgroDiaryOutlinedButton(
                    text = "Отмена",
                    onClick = onNavigateBack,
                    modifier = Modifier.weight(1f)
                )

                AgroDiaryButton(
                    text = if (isEditMode) "Сохранить" else "Добавить",
                    onClick = onSave,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
