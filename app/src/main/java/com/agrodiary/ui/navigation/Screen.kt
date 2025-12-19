package com.agrodiary.ui.navigation

sealed class Screen(val route: String) {
    // Main tabs
    object Home : Screen("home")
    object Animals : Screen("animals")
    object Journal : Screen("journal")

    // Animals module
    object AnimalDetail : Screen("animal/{animalId}") {
        fun createRoute(animalId: Long) = "animal/$animalId"
    }
    object AddAnimal : Screen("animal/add")
    object EditAnimal : Screen("animal/edit/{animalId}") {
        fun createRoute(animalId: Long) = "animal/edit/$animalId"
    }

    // Staff module
    object Staff : Screen("staff")
    object StaffDetail : Screen("staff/{staffId}") {
        fun createRoute(staffId: Long) = "staff/$staffId"
    }
    object AddStaff : Screen("staff/add")
    object EditStaff : Screen("staff/edit/{staffId}") {
        fun createRoute(staffId: Long) = "staff/edit/$staffId"
    }

    // Tasks module
    object Tasks : Screen("tasks")
    object TaskDetail : Screen("task/{taskId}") {
        fun createRoute(taskId: Long) = "task/$taskId"
    }
    object AddTask : Screen("task/add")
    object EditTask : Screen("task/edit/{taskId}") {
        fun createRoute(taskId: Long) = "task/edit/$taskId"
    }

    // Journal module
    object JournalDetail : Screen("journal/{entryId}") {
        fun createRoute(entryId: Long) = "journal/$entryId"
    }
    object AddJournalEntry : Screen("journal/add")

    // Feed module
    object FeedStock : Screen("feed")
    object FeedDetail : Screen("feed/{feedId}") {
        fun createRoute(feedId: Long) = "feed/$feedId"
    }
    object AddFeed : Screen("feed/add")
    object EditFeed : Screen("feed/edit/{feedId}") {
        fun createRoute(feedId: Long) = "feed/edit/$feedId"
    }
    object FeedTransaction : Screen("feed/{feedId}/transaction") {
        fun createRoute(feedId: Long) = "feed/$feedId/transaction"
    }

    // Products module
    object Products : Screen("products")
    object ProductDetail : Screen("product/{productId}") {
        fun createRoute(productId: Long) = "product/$productId"
    }
    object AddProduct : Screen("product/add")
    object EditProduct : Screen("product/edit/{productId}") {
        fun createRoute(productId: Long) = "product/edit/$productId"
    }
    object ProductTransaction : Screen("product/{productId}/transaction") {
        fun createRoute(productId: Long) = "product/$productId/transaction"
    }

    // Reports
    object Reports : Screen("reports")

    // Settings
    object Settings : Screen("settings")
}
