package com.agrodiary.ui.navigation
sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Animals : Screen("animals")
    object Journal : Screen("journal")
    object AnimalDetail : Screen("animal/{animalId}") {
        fun createRoute(animalId: Long) = "animal/$animalId"
    }
    object AddAnimal : Screen("animal/add")
    object EditAnimal : Screen("animal/edit/{animalId}") {
        fun createRoute(animalId: Long) = "animal/edit/$animalId"
    }
    object Staff : Screen("staff")
    object StaffDetail : Screen("staff/{staffId}") {
        fun createRoute(staffId: Long) = "staff/$staffId"
    }
    object AddStaff : Screen("staff/add")
    object EditStaff : Screen("staff/edit/{staffId}") {
        fun createRoute(staffId: Long) = "staff/edit/$staffId"
    }
    object Tasks : Screen("tasks")
    object TaskDetail : Screen("task/{taskId}") {
        fun createRoute(taskId: Long) = "task/$taskId"
    }
    object AddTask : Screen("task/add")
    object EditTask : Screen("task/edit/{taskId}") {
        fun createRoute(taskId: Long) = "task/edit/$taskId"
    }
    object JournalDetail : Screen("journal/{entryId}") {
        fun createRoute(entryId: Long) = "journal/$entryId"
    }
    object AddJournalEntry : Screen("journal/add")
    object EditJournalEntry : Screen("journal/edit/{entryId}") {
        fun createRoute(entryId: Long) = "journal/edit/$entryId"
    }
    object ActivityLog : Screen("journal/activity")
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
    object Reports : Screen("reports")
    object Settings : Screen("settings")
}
