package com.agrodiary.ui.products

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.agrodiary.data.local.entity.MeasureUnit
import com.agrodiary.data.local.entity.ProductCategory
import com.agrodiary.data.local.entity.ProductEntity
import com.agrodiary.ui.components.AgroDiaryTextField
import com.agrodiary.ui.components.AgroDiaryTopBar
import com.agrodiary.ui.components.DropdownField

@Composable
fun AddEditProductScreen(
    productId: Long?,
    onNavigateBack: () -> Unit,
    onSaveSuccess: () -> Unit,
    viewModel: ProductViewModel = hiltViewModel()
) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(ProductCategory.OTHER) }
    var currentQuantity by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf(MeasureUnit.KILOGRAM) }
    var pricePerUnit by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    
    var isEditing by remember { mutableStateOf(false) }
    var existingProduct by remember { mutableStateOf<ProductEntity?>(null) }

    LaunchedEffect(productId) {
        if (productId != null) {
            isEditing = true
            existingProduct = viewModel.getProductById(productId)
            existingProduct?.let {
                name = it.name
                category = it.category
                currentQuantity = it.currentQuantity.toString()
                unit = it.unit
                pricePerUnit = it.pricePerUnit?.toString() ?: ""
                notes = it.notes ?: ""
            }
        }
    }

    Scaffold(
        topBar = {
            AgroDiaryTopBar(
                title = if (isEditing) "Редактировать товар" else "Добавить товар",
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
                items = ProductCategory.values().toList(),
                selectedItem = category,
                onItemSelected = { category = it },
                itemLabel = { it.displayName }
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            AgroDiaryTextField(
                value = currentQuantity,
                onValueChange = { if (it.all { char -> char.isDigit() || char == '.' }) currentQuantity = it },
                label = "Количество",
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
                value = pricePerUnit,
                onValueChange = { if (it.all { char -> char.isDigit() || char == '.' }) pricePerUnit = it },
                label = "Цена за единицу (₽)",
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
                    val product = if (isEditing && existingProduct != null) {
                        existingProduct!!.copy(
                            name = name,
                            category = category,
                            currentQuantity = currentQuantity.toDoubleOrNull() ?: 0.0,
                            unit = unit,
                            pricePerUnit = pricePerUnit.toDoubleOrNull(),
                            notes = notes,
                            lastUpdated = System.currentTimeMillis()
                        )
                    } else {
                        ProductEntity(
                            name = name,
                            category = category,
                            currentQuantity = currentQuantity.toDoubleOrNull() ?: 0.0,
                            unit = unit,
                            pricePerUnit = pricePerUnit.toDoubleOrNull(),
                            notes = notes
                        )
                    }
                    
                    if (isEditing) {
                        viewModel.updateProduct(product, onSaveSuccess)
                    } else {
                        viewModel.addProduct(product, onSaveSuccess)
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
