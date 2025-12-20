package com.agrodiary.ui.animals

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Pets
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
import com.agrodiary.data.local.entity.AnimalEntity
import com.agrodiary.data.local.entity.AnimalStatus
import com.agrodiary.data.local.entity.AnimalType
import com.agrodiary.ui.animals.components.AnimalCard
import com.agrodiary.ui.animals.components.AnimalTypeFilterRow
import com.agrodiary.ui.components.AgroDiarySearchBar
import com.agrodiary.ui.components.AgroDiaryTopBar
import com.agrodiary.ui.components.EmptyStateView
import com.agrodiary.ui.theme.AgroDiaryTheme

/**
 * Экран списка животных.
 *
 * Отображает:
 * - Поиск по имени
 * - Фильтры по типу животного
 * - Список животных в LazyColumn
 * - FAB для добавления нового животного
 * - Пустое состояние, если животных нет
 * - Индикатор загрузки
 * - Сообщения об ошибках
 *
 * @param onAnimalClick Обработчик клика по животному
 * @param onAddClick Обработчик клика по кнопке добавления
 * @param viewModel ViewModel для управления состоянием
 * @param modifier Модификатор
 */
@Composable
fun AnimalsListScreen(
    onAnimalClick: (Long) -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AnimalsViewModel = hiltViewModel()
) {
    val animals by viewModel.animals.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedType by viewModel.selectedType.collectAsState()
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
                title = "Животные",
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
                    contentDescription = "Добавить животное"
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
                placeholder = "Поиск животных...",
                enabled = !uiState.isLoading
            )

            // Фильтры по типу
            AnimalTypeFilterRow(
                selectedType = selectedType,
                onTypeSelected = { viewModel.setSelectedType(it) }
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
                animals.isEmpty() -> {
                    // Пустое состояние
                    EmptyStateView(
                        message = if (searchQuery.isNotBlank() || selectedType != null) {
                            "Животные не найдены"
                        } else {
                            "Нет животных.\nДобавьте первое животное!"
                        },
                        icon = Icons.Default.Pets
                    )
                }
                else -> {
                    // Список животных
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            items = animals,
                            key = { it.id }
                        ) { animal ->
                            AnimalCard(
                                animal = animal,
                                onClick = { onAnimalClick(animal.id) }
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
private fun AnimalsListScreenPreview() {
    AgroDiaryTheme {
        // Preview с моком данных (без ViewModel)
        AnimalsListScreenContent(
            animals = listOf(
                AnimalEntity(
                    id = 1,
                    name = "Буренка",
                    type = AnimalType.COW,
                    breed = "Голштинская",
                    birthDate = System.currentTimeMillis(),
                    gender = "Ж",
                    weight = 450f,
                    status = AnimalStatus.ACTIVE
                ),
                AnimalEntity(
                    id = 2,
                    name = "Петух",
                    type = AnimalType.CHICKEN,
                    breed = "Леггорн",
                    weight = 2.5f,
                    status = AnimalStatus.SICK
                ),
                AnimalEntity(
                    id = 3,
                    name = "Козочка",
                    type = AnimalType.GOAT,
                    status = AnimalStatus.ACTIVE
                )
            ),
            searchQuery = "",
            selectedType = null,
            isLoading = false,
            onAnimalClick = {},
            onAddClick = {},
            onSearchQueryChange = {},
            onTypeSelected = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AnimalsListScreenEmptyPreview() {
    AgroDiaryTheme {
        AnimalsListScreenContent(
            animals = emptyList(),
            searchQuery = "",
            selectedType = null,
            isLoading = false,
            onAnimalClick = {},
            onAddClick = {},
            onSearchQueryChange = {},
            onTypeSelected = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AnimalsListScreenLoadingPreview() {
    AgroDiaryTheme {
        AnimalsListScreenContent(
            animals = emptyList(),
            searchQuery = "",
            selectedType = null,
            isLoading = true,
            onAnimalClick = {},
            onAddClick = {},
            onSearchQueryChange = {},
            onTypeSelected = {}
        )
    }
}

/**
 * Вспомогательный Composable для Preview.
 */
@Composable
private fun AnimalsListScreenContent(
    animals: List<AnimalEntity>,
    searchQuery: String,
    selectedType: AnimalType?,
    isLoading: Boolean,
    onAnimalClick: (Long) -> Unit,
    onAddClick: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onTypeSelected: (AnimalType?) -> Unit
) {
    Scaffold(
        topBar = {
            AgroDiaryTopBar(
                title = "Животные",
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
                    contentDescription = "Добавить животное"
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
                placeholder = "Поиск животных...",
                enabled = !isLoading
            )

            AnimalTypeFilterRow(
                selectedType = selectedType,
                onTypeSelected = onTypeSelected
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
                animals.isEmpty() -> {
                    EmptyStateView(
                        message = if (searchQuery.isNotBlank() || selectedType != null) {
                            "Животные не найдены"
                        } else {
                            "Нет животных.\nДобавьте первое животное!"
                        },
                        icon = Icons.Default.Pets
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            items = animals,
                            key = { it.id }
                        ) { animal ->
                            AnimalCard(
                                animal = animal,
                                onClick = { onAnimalClick(animal.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}
