package com.agrodiary.ui.products

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.agrodiary.data.local.entity.ProductEntity
import com.agrodiary.ui.components.AgroDiaryTopBar
import com.agrodiary.ui.components.ConfirmDialog
import com.agrodiary.ui.components.LoadingView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProductDetailScreen(
    productId: Long,
    onNavigateBack: () -> Unit,
    onEditClick: (Long) -> Unit,
    viewModel: ProductViewModel = hiltViewModel()
) {
    var product by remember { mutableStateOf<ProductEntity?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(productId) {
        product = viewModel.getProductById(productId)
        isLoading = false
    }

    if (showDeleteDialog && product != null) {
        ConfirmDialog(
            title = "Удалить товар?",
            message = "Вы уверены, что хотите удалить \"${product?.name}\"?",
            onConfirm = {
                viewModel.deleteProduct(product!!)
                showDeleteDialog = false
                onNavigateBack()
            },
            onDismiss = { showDeleteDialog = false }
        )
    }

    Scaffold(
        topBar = {
            AgroDiaryTopBar(
                title = "Детали товара",
                onBackClick = onNavigateBack,
                actions = {
                    androidx.compose.material3.IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Удалить")
                    }
                }
            )
        },
        floatingActionButton = {
            if (product != null) {
                FloatingActionButton(onClick = { onEditClick(productId) }) {
                    Icon(Icons.Default.Edit, contentDescription = "Редактировать")
                }
            }
        }
    ) { padding ->
        if (isLoading) {
            LoadingView(modifier = Modifier.padding(padding))
        } else {
            product?.let { p ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp)
                ) {
                    Text(
                        text = p.name,
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Text(
                        text = p.category.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "Количество",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${p.currentQuantity} ${p.unit.displayName}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    
                    if (p.pricePerUnit != null && p.pricePerUnit > 0) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Цена",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${p.pricePerUnit} ₽ за ${p.unit.shortName}",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Общая стоимость: ${p.currentQuantity * p.pricePerUnit} ₽",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    if (!p.notes.isNullOrBlank()) {
                        Text(
                            text = "Примечание",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = p.notes,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    
                    Text(
                        text = "Последнее обновление",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = SimpleDateFormat("dd MMMM yyyy HH:mm", Locale("ru")).format(Date(p.lastUpdated)),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } ?: run {
                Text("Товар не найден", modifier = Modifier.padding(padding))
            }
        }
    }
}
