package com.example.riji.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object AddItem : Screen("add_item")
    data object Settings : Screen("settings")
    data object Search : Screen("search")

    // Anniversary
    data object AnniversaryDetail : Screen("anniversary/{id}") {
        fun createRoute(id: Long) = "anniversary/$id"
    }
    data object AddAnniversary : Screen("add_anniversary")
    data object EditAnniversary : Screen("edit_anniversary/{id}") {
        fun createRoute(id: Long) = "edit_anniversary/$id"
    }

    // Check-in
    data object CheckInDetail : Screen("checkin/{id}") {
        fun createRoute(id: Long) = "checkin/$id"
    }
    data object AddCheckIn : Screen("add_checkin")

    // Asset
    data object AssetList : Screen("assets")
    data object AddAsset : Screen("add_asset")
    data object AssetDetail : Screen("asset/{id}") {
        fun createRoute(id: Long) = "asset/$id"
    }

    // Subscription
    data object SubscriptionList : Screen("subscriptions")
    data object AddSubscription : Screen("add_subscription")

    // Plan
    data object PlanList : Screen("plans")
    data object AddPlan : Screen("add_plan")
    data object PlanDetail : Screen("plan/{id}") {
        fun createRoute(id: Long) = "plan/$id"
    }

    // Diary
    data object DiaryList : Screen("diaries")
    data object AddDiary : Screen("add_diary")
    data object DiaryDetail : Screen("diary/{id}") {
        fun createRoute(id: Long) = "diary/$id"
    }

    // Category
    data object CategoryManagement : Screen("category_management")
}
