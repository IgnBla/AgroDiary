package com.agrodiary.ui.staff

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
import androidx.compose.runtime.mutableDoubleStateOf
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
import com.agrodiary.data.local.entity.StaffEntity
import com.agrodiary.data.local.entity.StaffStatus
import com.agrodiary.ui.components.AgroDiaryButton
import com.agrodiary.ui.components.AgroDiaryMultilineTextField
import com.agrodiary.ui.components.AgroDiaryOutlinedButton
import com.agrodiary.ui.components.AgroDiaryTextField
import com.agrodiary.ui.components.AgroDiaryTopBar
import com.agrodiary.ui.components.DatePickerField
import com.agrodiary.ui.components.DropdownField
import com.agrodiary.ui.theme.AgroDiaryTheme
import kotlinx.coroutines.launch

import com.agrodiary.common.ValidationUtils

/**
 * Экран добавления/редактирования сотрудника.
 *
 * Режимы работы:
 * - Добавление: staffId = null
 * - Редактирование: staffId = ID существующего сотрудника
 *
 * @param staffId ID сотрудника для редактирования (null для добавления)
 * @param onNavigateBack Обработчик возврата назад
 * @param onSaveSuccess Обработчик успешного сохранения
 * @param viewModel ViewModel
 * @param modifier Модификатор
 */
@Composable
fun AddEditStaffScreen(
    staffId: Long?,
    onNavigateBack: () -> Unit,
    onSaveSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: StaffViewModel = hiltViewModel()
) {
    val isEditMode = staffId != null
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()

    // Состояние формы
    var name by remember { mutableStateOf("") }
    var position by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var hireDate by remember { mutableLongStateOf(0L) }
    var salary by remember { mutableDoubleStateOf(0.0) }
    var salaryText by remember { mutableStateOf("") }
    var status by remember { mutableStateOf(StaffStatus.ACTIVE) }
    var notes by remember { mutableStateOf("") }

    // Ошибки валидации
    var nameError by remember { mutableStateOf(false) }
    var positionError by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }
    var phoneError by remember { mutableStateOf(false) }
    var salaryError by remember { mutableStateOf(false) }
    var hireDateError by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    // Загрузка данных для редактирования
    LaunchedEffect(staffId) {
        if (isEditMode && staffId != null) {
            scope.launch {
                val staff = viewModel.getStaffById(staffId)
                staff?.let {
                    name = it.name
                    position = it.position ?: ""
                    phone = it.phone ?: ""
                    email = it.email ?: ""
                    hireDate = it.hireDate ?: 0L
                    salary = it.salary ?: 0.0
                    salaryText = it.salary?.toString() ?: ""
                    status = it.status
                    notes = it.notes ?: ""
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
                title = if (isEditMode) "Редактировать сотрудника" else "Добавить сотрудника",
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
                    placeholder = "Введите имя сотрудника",
                    isError = nameError,
                    errorMessage = if (nameError) "Имя должно быть не менее 2 символов" else null,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                )

                // Должность (обязательное)
                AgroDiaryTextField(
                    value = position,
                    onValueChange = {
                        position = it
                        positionError = it.isBlank()
                    },
                    label = "Должность *",
                    placeholder = "Введите должность",
                    isError = positionError,
                    errorMessage = if (positionError) "Должность обязательна" else null,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                )

                // Статус
                DropdownField(
                    selectedItem = status,
                    items = StaffStatus.entries,
                    onItemSelected = { status = it },
                    itemLabel = { it.displayName },
                    label = "Статус"
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Секция: Контактная информация
                Text(
                    text = "Контактная информация",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                // Телефон
                AgroDiaryTextField(
                    value = phone,
                    onValueChange = {
                        phone = it
                        phoneError = it.isNotEmpty() && !ValidationUtils.isValidPhone(it)
                    },
                    label = "Телефон",
                    placeholder = "+7 (999) 123-45-67",
                    isError = phoneError,
                    errorMessage = if (phoneError) "Некорректный формат телефона" else null,
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next
                )

                // Email
                AgroDiaryTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        emailError = it.isNotEmpty() && !ValidationUtils.isValidEmail(it)
                    },
                    label = "Email",
                    placeholder = "example@example.com",
                    isError = emailError,
                    errorMessage = if (emailError) "Некорректный формат email" else null,
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Секция: Информация о работе
                Text(
                    text = "Информация о работе",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                // Дата приёма на работу
                DatePickerField(
                    selectedDate = if (hireDate > 0) hireDate else null,
                    onDateSelected = {
                        hireDate = it ?: 0L
                        hireDateError = it != null && it > System.currentTimeMillis()
                    },
                    label = "Дата приёма на работу",
                    isError = hireDateError,
                    errorMessage = if (hireDateError) "Дата не может быть в будущем" else null
                )

                // Зарплата
                AgroDiaryTextField(
                    value = salaryText,
                    onValueChange = {
                        salaryText = it
                        val s = it.toDoubleOrNull()
                        if (s != null) {
                            salary = s
                            salaryError = !ValidationUtils.isValidPrice(s)
                        } else {
                            salaryError = it.isNotEmpty()
                        }
                    },
                    label = "Зарплата (руб.)",
                    placeholder = "Введите зарплату",
                    isError = salaryError,
                    errorMessage = if (salaryError) "Введите корректную сумму" else null,
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Next
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
                            positionError = position.isBlank()
                            emailError = email.isNotEmpty() && !ValidationUtils.isValidEmail(email)
                            phoneError = phone.isNotEmpty() && !ValidationUtils.isValidPhone(phone)
                            
                            val s = salaryText.toDoubleOrNull()
                            salaryError = salaryText.isNotEmpty() && (s == null || !ValidationUtils.isValidPrice(s))
                            
                            hireDateError = hireDate > System.currentTimeMillis()

                            if (!nameError && !positionError && !emailError && !phoneError && !salaryError && !hireDateError) {
                                val staff = StaffEntity(
                                    id = staffId ?: 0,
                                    name = name.trim(),
                                    position = position.trim().ifBlank { null },
                                    phone = phone.trim().ifBlank { null },
                                    email = email.trim().ifBlank { null },
                                    hireDate = if (hireDate > 0) hireDate else null,
                                    salary = if (salary > 0) salary else null,
                                    status = status,
                                    notes = notes.trim().ifBlank { null },
                                    createdAt = if (isEditMode) 0 else System.currentTimeMillis(),
                                    updatedAt = System.currentTimeMillis()
                                )

                                if (isEditMode) {
                                    viewModel.updateStaff(staff)
                                } else {
                                    viewModel.addStaff(staff)
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
private fun AddEditStaffScreenAddPreview() {
    AgroDiaryTheme {
        AddEditStaffScreenContent(
            isEditMode = false,
            name = "",
            position = "",
            phone = "",
            email = "",
            hireDate = 0L,
            salaryText = "",
            status = StaffStatus.ACTIVE,
            notes = "",
            nameError = false,
            positionError = false,
            onNavigateBack = {},
            onNameChange = {},
            onPositionChange = {},
            onPhoneChange = {},
            onEmailChange = {},
            onHireDateChange = {},
            onSalaryChange = {},
            onStatusChange = {},
            onNotesChange = {},
            onSave = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AddEditStaffScreenEditPreview() {
    AgroDiaryTheme {
        AddEditStaffScreenContent(
            isEditMode = true,
            name = "Иван Петров",
            position = "Управляющий фермой",
            phone = "+7 (999) 123-45-67",
            email = "ivan@example.com",
            hireDate = System.currentTimeMillis(),
            salaryText = "50000",
            status = StaffStatus.ACTIVE,
            notes = "Опытный управляющий",
            nameError = false,
            positionError = false,
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
private fun AddEditStaffScreenContent(
    isEditMode: Boolean,
    name: String,
    position: String,
    phone: String,
    email: String,
    hireDate: Long,
    salaryText: String,
    status: StaffStatus,
    notes: String,
    nameError: Boolean,
    positionError: Boolean,
    onNavigateBack: () -> Unit,
    onNameChange: (String) -> Unit,
    onPositionChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onHireDateChange: (Long?) -> Unit,
    onSalaryChange: (String) -> Unit,
    onStatusChange: (StaffStatus) -> Unit,
    onNotesChange: (String) -> Unit,
    onSave: () -> Unit
) {
    Scaffold(
        topBar = {
            AgroDiaryTopBar(
                title = if (isEditMode) "Редактировать сотрудника" else "Добавить сотрудника",
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
                placeholder = "Введите имя сотрудника",
                isError = nameError,
                errorMessage = if (nameError) "Имя обязательно" else null
            )

            AgroDiaryTextField(
                value = position,
                onValueChange = onPositionChange,
                label = "Должность *",
                placeholder = "Введите должность",
                isError = positionError,
                errorMessage = if (positionError) "Должность обязательна" else null
            )

            DropdownField(
                selectedItem = status,
                items = StaffStatus.entries,
                onItemSelected = onStatusChange,
                itemLabel = { it.displayName },
                label = "Статус"
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Контактная информация",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            AgroDiaryTextField(
                value = phone,
                onValueChange = onPhoneChange,
                label = "Телефон",
                placeholder = "+7 (999) 123-45-67"
            )

            AgroDiaryTextField(
                value = email,
                onValueChange = onEmailChange,
                label = "Email",
                placeholder = "example@example.com"
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Информация о работе",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            DatePickerField(
                selectedDate = if (hireDate > 0) hireDate else null,
                onDateSelected = onHireDateChange,
                label = "Дата приёма на работу"
            )

            AgroDiaryTextField(
                value = salaryText,
                onValueChange = onSalaryChange,
                label = "Зарплата (руб.)",
                placeholder = "Введите зарплату"
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

// PREVIEWS

@Preview(showBackground = true)
@Composable
private fun AddEditStaffScreenAddPreview() {
    AgroDiaryTheme {
        AddEditStaffScreenContent(
            isEditMode = false,
            name = "",
            position = "",
            phone = "",
            email = "",
            hireDate = 0L,
            salaryText = "",
            status = StaffStatus.ACTIVE,
            notes = "",
            nameError = false,
            positionError = false,
            onNavigateBack = {},
            onNameChange = {},
            onPositionChange = {},
            onPhoneChange = {},
            onEmailChange = {},
            onHireDateChange = {},
            onSalaryChange = {},
            onStatusChange = {},
            onNotesChange = {},
            onSave = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AddEditStaffScreenEditPreview() {
    AgroDiaryTheme {
        AddEditStaffScreenContent(
            isEditMode = true,
            name = "Иван Петров",
            position = "Управляющий фермой",
            phone = "+7 (999) 123-45-67",
            email = "ivan@example.com",
            hireDate = System.currentTimeMillis(),
            salaryText = "50000",
            status = StaffStatus.ACTIVE,
            notes = "Опытный управляющий",
            nameError = false,
            positionError = false,
            onNavigateBack = {},
            onNameChange = {},
            onPositionChange = {},
            onPhoneChange = {},
            onEmailChange = {},
            onHireDateChange = {},
            onSalaryChange = {},
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
private fun AddEditStaffScreenContent(
    isEditMode: Boolean,
    name: String,
    position: String,
    phone: String,
    email: String,
    hireDate: Long,
    salaryText: String,
    status: StaffStatus,
    notes: String,
    nameError: Boolean,
    positionError: Boolean,
    onNavigateBack: () -> Unit,
    onNameChange: (String) -> Unit,
    onPositionChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onHireDateChange: (Long?) -> Unit,
    onSalaryChange: (String) -> Unit,
    onStatusChange: (StaffStatus) -> Unit,
    onNotesChange: (String) -> Unit,
    onSave: () -> Unit
) {
    Scaffold(
        topBar = {
            AgroDiaryTopBar(
                title = if (isEditMode) "Редактировать сотрудника" else "Добавить сотрудника",
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
                placeholder = "Введите имя сотрудника",
                isError = nameError,
                errorMessage = if (nameError) "Имя обязательно" else null
            )

            AgroDiaryTextField(
                value = position,
                onValueChange = onPositionChange,
                label = "Должность *",
                placeholder = "Введите должность",
                isError = positionError,
                errorMessage = if (positionError) "Должность обязательна" else null
            )

            DropdownField(
                selectedItem = status,
                items = StaffStatus.entries,
                onItemSelected = onStatusChange,
                itemLabel = { it.displayName },
                label = "Статус"
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Контактная информация",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            AgroDiaryTextField(
                value = phone,
                onValueChange = onPhoneChange,
                label = "Телефон",
                placeholder = "+7 (999) 123-45-67"
            )

            AgroDiaryTextField(
                value = email,
                onValueChange = onEmailChange,
                label = "Email",
                placeholder = "example@example.com"
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Информация о работе",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            DatePickerField(
                selectedDate = if (hireDate > 0) hireDate else null,
                onDateSelected = onHireDateChange,
                label = "Дата приёма на работу"
            )

            AgroDiaryTextField(
                value = salaryText,
                onValueChange = onSalaryChange,
                label = "Зарплата (руб.)",
                placeholder = "Введите зарплату"
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
