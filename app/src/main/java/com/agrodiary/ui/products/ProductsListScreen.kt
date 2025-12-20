package com.agrodiary.ui.products

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.agrodiary.data.local.entity.ProductCategory
import com.agrodiary.ui.components.AgroDiaryFilterChips
import com.agrodiary.ui.components.AgroDiaryTopBar
import com.agrodiary.ui.components.EmptyStateView
import com.agrodiary.ui.products.components.ProductCard

@Composable
fun ProductsListScreen(
    onProductClick: (Long) -> Unit,
    onAddClick: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: ProductViewModel = hiltViewModel()
) {
    val products by viewModel.products.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            AgroDiaryTopBar(
                title = "Товары",
                onBackClick = onNavigateBack
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(Icons.Default.Add, contentDescription = "Добавить товар")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            AgroDiaryFilterChips(
                items = ProductCategory.values().toList(),
                selectedItem = selectedCategory,
                onItemSelected = viewModel::setSelectedCategory,
                itemLabel = { it.displayName }
            )

            if (products.isEmpty()) {
                EmptyStateView(
                    message = "Список товаров пуст",
                    icon = Icons.Default.Inventory
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(products) { product ->
                        ProductCard(
                            product = product,
                            onClick = { onProductClick(product.id) }
                        )
                    }
                }
            }
        }
    }
}
