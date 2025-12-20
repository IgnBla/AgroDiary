package com.agrodiary.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.agrodiary.ui.home.HomeScreen
import com.agrodiary.ui.animals.AnimalsListScreen
import com.agrodiary.ui.animals.AnimalDetailScreen
import com.agrodiary.ui.animals.AddEditAnimalScreen
import com.agrodiary.ui.staff.StaffListScreen
import com.agrodiary.ui.staff.StaffDetailScreen
import com.agrodiary.ui.staff.AddEditStaffScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        // Main tabs
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToAnimals = { navController.navigate(Screen.Animals.route) },
                onNavigateToTasks = { navController.navigate(Screen.Tasks.route) },
                onNavigateToFeed = { navController.navigate(Screen.FeedStock.route) },
                onNavigateToProducts = { navController.navigate(Screen.Products.route) },
                onNavigateToStaff = { navController.navigate(Screen.Staff.route) },
                onNavigateToJournal = { navController.navigate(Screen.Journal.route) }
            )
        }

        // Animals module
        composable(Screen.Animals.route) {
            AnimalsListScreen(
                onAnimalClick = { animalId ->
                    navController.navigate(Screen.AnimalDetail.createRoute(animalId))
                },
                onAddClick = {
                    navController.navigate(Screen.AddAnimal.route)
                }
            )
        }

        composable(
            route = Screen.AnimalDetail.route,
            arguments = listOf(navArgument("animalId") { type = NavType.LongType })
        ) { backStackEntry ->
            val animalId = backStackEntry.arguments?.getLong("animalId") ?: return@composable
            AnimalDetailScreen(
                animalId = animalId,
                onNavigateBack = { navController.navigateUp() },
                onEditClick = { id ->
                    navController.navigate(Screen.EditAnimal.createRoute(id))
                },
                onDeleteSuccess = { navController.navigateUp() }
            )
        }

        composable(Screen.AddAnimal.route) {
            AddEditAnimalScreen(
                animalId = null,
                onNavigateBack = { navController.navigateUp() },
                onSaveSuccess = { navController.navigateUp() }
            )
        }

        composable(
            route = Screen.EditAnimal.route,
            arguments = listOf(navArgument("animalId") { type = NavType.LongType })
        ) { backStackEntry ->
            val animalId = backStackEntry.arguments?.getLong("animalId") ?: return@composable
            AddEditAnimalScreen(
                animalId = animalId,
                onNavigateBack = { navController.navigateUp() },
                onSaveSuccess = { navController.navigateUp() }
            )
        }

        composable(Screen.Journal.route) {
            PlaceholderScreen(title = "Журнал")
        }

        // Staff module
        composable(Screen.Staff.route) {
            StaffListScreen(
                onStaffClick = { staffId ->
                    navController.navigate(Screen.StaffDetail.createRoute(staffId))
                },
                onAddClick = {
                    navController.navigate(Screen.AddStaff.route)
                }
            )
        }

        composable(
            route = Screen.StaffDetail.route,
            arguments = listOf(navArgument("staffId") { type = NavType.LongType })
        ) { backStackEntry ->
            val staffId = backStackEntry.arguments?.getLong("staffId") ?: return@composable
            StaffDetailScreen(
                staffId = staffId,
                onNavigateBack = { navController.navigateUp() },
                onEditClick = { id ->
                    navController.navigate(Screen.EditStaff.createRoute(id))
                },
                onDeleteSuccess = { navController.navigateUp() }
            )
        }

        composable(Screen.AddStaff.route) {
            AddEditStaffScreen(
                staffId = null,
                onNavigateBack = { navController.navigateUp() },
                onSaveSuccess = { navController.navigateUp() }
            )
        }

        composable(
            route = Screen.EditStaff.route,
            arguments = listOf(navArgument("staffId") { type = NavType.LongType })
        ) { backStackEntry ->
            val staffId = backStackEntry.arguments?.getLong("staffId") ?: return@composable
            AddEditStaffScreen(
                staffId = staffId,
                onNavigateBack = { navController.navigateUp() },
                onSaveSuccess = { navController.navigateUp() }
            )
        }

        // Tasks module
        composable(Screen.Tasks.route) {
            PlaceholderScreen(title = "Задачи")
        }

        composable(
            route = Screen.TaskDetail.route,
            arguments = listOf(navArgument("taskId") { type = NavType.LongType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getLong("taskId") ?: return@composable
            PlaceholderScreen(title = "Задача #$taskId")
        }

        composable(Screen.AddTask.route) {
            PlaceholderScreen(title = "Добавить задачу")
        }

        // Feed module
        composable(Screen.FeedStock.route) {
            PlaceholderScreen(title = "Запас кормов")
        }

        composable(
            route = Screen.FeedDetail.route,
            arguments = listOf(navArgument("feedId") { type = NavType.LongType })
        ) { backStackEntry ->
            val feedId = backStackEntry.arguments?.getLong("feedId") ?: return@composable
            PlaceholderScreen(title = "Корм #$feedId")
        }

        composable(Screen.AddFeed.route) {
            PlaceholderScreen(title = "Добавить корм")
        }

        // Products module
        composable(Screen.Products.route) {
            PlaceholderScreen(title = "Товары")
        }

        composable(
            route = Screen.ProductDetail.route,
            arguments = listOf(navArgument("productId") { type = NavType.LongType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getLong("productId") ?: return@composable
            PlaceholderScreen(title = "Товар #$productId")
        }

        composable(Screen.AddProduct.route) {
            PlaceholderScreen(title = "Добавить товар")
        }

        // Reports
        composable(Screen.Reports.route) {
            PlaceholderScreen(title = "Отчёты")
        }

        // Settings
        composable(Screen.Settings.route) {
            PlaceholderScreen(title = "Настройки")
        }
    }
}

@Composable
private fun PlaceholderScreen(title: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium
        )
    }
}
