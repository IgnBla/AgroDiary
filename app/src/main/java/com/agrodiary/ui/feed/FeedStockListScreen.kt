package com.agrodiary.ui.feed

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.agrodiary.data.local.entity.FeedCategory
import com.agrodiary.ui.components.AgroDiaryFilterChips
import com.agrodiary.ui.components.AgroDiaryTopBar
import com.agrodiary.ui.components.EmptyStateView
import com.agrodiary.ui.feed.components.FeedStockCard

@Composable
fun FeedStockListScreen(
    onFeedClick: (Long) -> Unit,
    onAddClick: () -> Unit,
    viewModel: FeedStockViewModel = hiltViewModel()
) {
    val feedStocks by viewModel.feedStocks.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            AgroDiaryTopBar(
                title = "Запас кормов",
                onBackClick = null
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(Icons.Default.Add, contentDescription = "Добавить корм")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            AgroDiaryFilterChips(
                items = FeedCategory.values().toList(),
                selectedItem = selectedCategory,
                onItemSelected = viewModel::setSelectedCategory,
                itemLabel = { it.displayName }
            )

            if (feedStocks.isEmpty()) {
                EmptyStateView(
                    message = "Список кормов пуст",
                    icon = Icons.Default.Grass
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(feedStocks) { feedStock ->
                        FeedStockCard(
                            feedStock = feedStock,
                            onClick = { onFeedClick(feedStock.id) }
                        )
                    }
                }
            }
        }
    }
}
