package com.agrodiary.ui.staff

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.agrodiary.data.local.entity.StaffEntity
import com.agrodiary.data.local.entity.StaffStatus
import com.agrodiary.ui.components.AgroDiarySearchBar
import com.agrodiary.ui.components.AgroDiaryTopBar
import com.agrodiary.ui.components.EmptyStateView
import com.agrodiary.ui.staff.components.StaffCard
import com.agrodiary.ui.staff.components.StaffStatusFilterRow
import com.agrodiary.ui.theme.AgroDiaryTheme

/**
 * Экран списка сотрудников.
 *
 * Отображает:
 * - Поиск по имени и должности
 * - Фильтры по статусу сотрудника
 * - Список сотрудников в LazyColumn
 * - FAB для добавления нового сотрудника
 * - Пустое состояние, если сотрудников нет
 * - Индикатор загрузки
 * - Сообщения об ошибках
 *
 * @param onStaffClick Обработчик клика по сотруднику
 * @param onAddClick Обработчик клика по кнопке добавления
 * @param viewModel ViewModel для управления состоянием
 * @param modifier Модификатор
 */
@Composable
fun StaffListScreen(
    onStaffClick: (Long) -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: StaffViewModel = hiltViewModel()
) {
    val staff by viewModel.staff.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedStatus by viewModel.selectedStatus.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

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
        }
    }

    Scaffold(
        topBar = {
            AgroDiaryTopBar(
                title = "Персонал",
                onBackClick = null
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Добавить сотрудника"
                )
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = modifier
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Поиск
            AgroDiarySearchBar(
                query = searchQuery,
                onQueryChange = { viewModel.setSearchQuery(it) },
                placeholder = "Поиск по имени и должности...",
                enabled = !uiState.isLoading
            )

            // Фильтры по статусу
            StaffStatusFilterRow(
                selectedStatus = selectedStatus,
                onStatusSelected = { viewModel.setSelectedStatus(it) }
            )

            // Контент
            when {
                uiState.isLoading -> {
                    // Индикатор загрузки
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                staff.isEmpty() -> {
                    // Пустое состояние
                    EmptyStateView(
                        message = if (searchQuery.isNotBlank() || selectedStatus != null) {
                            "Сотрудники не найдены"
                        } else {
                            "Нет сотрудников.\nДобавьте первого сотрудника!"
                        },
                        icon = Icons.Default.Groups
                    )
                }
                else -> {
                    // Список сотрудников
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            items = staff,
                            key = { it.id }
                        ) { staffMember ->
                            StaffCard(
                                staff = staffMember,
                                onClick = { onStaffClick(staffMember.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

// PREVIEWS

@Preview(showBackground = true)
@Composable
private fun StaffListScreenPreview() {
    AgroDiaryTheme {
        StaffListScreenContent(
            staff = listOf(
                StaffEntity(
                    id = 1,
                    name = "Иван Петров",
                    position = "Управляющий фермой",
                    phone = "+7 (999) 123-45-67",
                    email = "ivan@example.com",
                    hireDate = System.currentTimeMillis(),
                    salary = 50000.0,
                    status = StaffStatus.ACTIVE
                ),
                StaffEntity(
                    id = 2,
                    name = "Мария Сидорова",
                    position = "Ветеринар",
                    phone = "+7 (999) 987-65-43",
                    email = "maria@example.com",
                    status = StaffStatus.ON_VACATION
                ),
                StaffEntity(
                    id = 3,
                    name = "Петр Иванов",
                    position = "Рабочий",
                    phone = "+7 (999) 555-55-55",
                    status = StaffStatus.ACTIVE
                )
            ),
            searchQuery = "",
            selectedStatus = null,
            isLoading = false,
            onStaffClick = {},
            onAddClick = {},
            onSearchQueryChange = {},
            onStatusSelected = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun StaffListScreenEmptyPreview() {
    AgroDiaryTheme {
        StaffListScreenContent(
            staff = emptyList(),
            searchQuery = "",
            selectedStatus = null,
            isLoading = false,
            onStaffClick = {},
            onAddClick = {},
            onSearchQueryChange = {},
            onStatusSelected = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun StaffListScreenLoadingPreview() {
    AgroDiaryTheme {
        StaffListScreenContent(
            staff = emptyList(),
            searchQuery = "",
            selectedStatus = null,
            isLoading = true,
            onStaffClick = {},
            onAddClick = {},
            onSearchQueryChange = {},
            onStatusSelected = {}
        )
    }
}

/**
 * Вспомогательный Composable для Preview.
 */
@Composable
private fun StaffListScreenContent(
    staff: List<StaffEntity>,
    searchQuery: String,
    selectedStatus: StaffStatus?,
    isLoading: Boolean,
    onStaffClick: (Long) -> Unit,
    onAddClick: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onStatusSelected: (StaffStatus?) -> Unit
) {
    Scaffold(
        topBar = {
            AgroDiaryTopBar(
                title = "Персонал",
                onBackClick = null
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Добавить сотрудника"
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            AgroDiarySearchBar(
                query = searchQuery,
                onQueryChange = onSearchQueryChange,
                placeholder = "Поиск по имени и должности...",
                enabled = !isLoading
            )

            StaffStatusFilterRow(
                selectedStatus = selectedStatus,
                onStatusSelected = onStatusSelected
            )

            when {
                isLoading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                staff.isEmpty() -> {
                    EmptyStateView(
                        message = if (searchQuery.isNotBlank() || selectedStatus != null) {
                            "Сотрудники не найдены"
                        } else {
                            "Нет сотрудников.\nДобавьте первого сотрудника!"
                        },
                        icon = Icons.Default.Groups
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            items = staff,
                            key = { it.id }
                        ) { staffMember ->
                            StaffCard(
                                staff = staffMember,
                                onClick = { onStaffClick(staffMember.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}
