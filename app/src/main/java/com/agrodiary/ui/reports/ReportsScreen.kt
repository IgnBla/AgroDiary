package com.agrodiary.ui.reports

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.agrodiary.ui.components.AgroDiaryTopBar
import com.agrodiary.ui.reports.components.ReportStatCard

@Composable
fun ReportsScreen(
    viewModel: ReportsViewModel = hiltViewModel()
) {
    val reportData by viewModel.reportData.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            AgroDiaryTopBar(
                title = "Отчёты и статистика",
                onBackClick = null // Top level screen
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ReportStatCard(
                        title = "Всего животных",
                        value = reportData.totalAnimals.toString(),
                        icon = Icons.Default.Pets,
                        modifier = Modifier.weight(1f)
                    )
                    ReportStatCard(
                        title = "Задач выполнено",
                        value = "${reportData.completedTasks}/${reportData.totalTasks}",
                        icon = Icons.Default.Assignment,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ReportStatCard(
                        title = "Низкий запас",
                        value = reportData.lowStockCount.toString(),
                        icon = Icons.Default.Warning,
                        iconColor = MaterialTheme.colorScheme.error,
                        modifier = Modifier.weight(1f)
                    )
                    ReportStatCard(
                        title = "Стоимость товаров",
                        value = "${reportData.totalProductsValue.toInt()} ₽",
                        icon = Icons.Default.AttachMoney,
                        iconColor = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
