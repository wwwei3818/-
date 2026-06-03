package com.example.riji.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.riji.data.AppDatabase
import com.example.riji.ui.screens.home.HomeScreen
import com.example.riji.ui.screens.add.AddItemScreen
import com.example.riji.ui.screens.anniversary.AddAnniversaryScreen
import com.example.riji.ui.screens.anniversary.AnniversaryDetailScreen
import com.example.riji.ui.screens.checkin.AddCheckInScreen
import com.example.riji.ui.screens.checkin.CheckInScreen
import com.example.riji.ui.screens.asset.AssetScreen
import com.example.riji.ui.screens.asset.AddAssetScreen
import com.example.riji.ui.screens.subscription.SubscriptionScreen
import com.example.riji.ui.screens.subscription.AddSubscriptionScreen
import com.example.riji.ui.screens.plan.PlanScreen
import com.example.riji.ui.screens.plan.AddPlanScreen
import com.example.riji.ui.screens.plan.PlanDetailScreen
import com.example.riji.ui.screens.diary.DiaryScreen
import com.example.riji.ui.screens.diary.AddDiaryScreen
import com.example.riji.ui.screens.diary.DiaryDetailScreen
import com.example.riji.ui.screens.settings.SettingsScreen
import com.example.riji.ui.screens.category.CategoryManagementScreen

private const val CLICK_DEBOUNCE_MS = 300L

@Composable
fun RijiNavGraph(
    navController: NavHostController,
    database: AppDatabase,
    isDarkMode: Boolean = false,
    onDarkModeChanged: (Boolean) -> Unit = {}
) {
    var lastClickTime by remember { mutableLongStateOf(0L) }

    fun safeNavigate(route: String) {
        val now = System.currentTimeMillis()
        if (now - lastClickTime > CLICK_DEBOUNCE_MS) {
            lastClickTime = now
            navController.navigate(route)
        }
    }

    fun safePopBackStack(): Boolean {
        // Don't pop if we're at the start destination
        if (navController.currentDestination?.route == Screen.Home.route) {
            return false
        }
        return navController.popBackStack()
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        // Home
        composable(Screen.Home.route) {
            HomeScreen(
                database = database,
                onNavigateToAdd = { safeNavigate(Screen.AddItem.route) },
                onNavigateToSettings = { safeNavigate(Screen.Settings.route) },
                onNavigateToAnniversary = { id -> safeNavigate(Screen.AnniversaryDetail.createRoute(id)) },
                onNavigateToCheckIn = { id -> safeNavigate(Screen.CheckInDetail.createRoute(id)) },
                onNavigateToAssets = { safeNavigate(Screen.AssetList.route) },
                onNavigateToSubscriptions = { safeNavigate(Screen.SubscriptionList.route) },
                onNavigateToPlanDetail = { id -> safeNavigate(Screen.PlanDetail.createRoute(id)) },
                onNavigateToPlanList = { safeNavigate(Screen.PlanList.route) },
                onNavigateToDiaries = { safeNavigate(Screen.DiaryList.route) }
            )
        }

        // Add Item
        composable(Screen.AddItem.route) {
            AddItemScreen(
                onNavigateBack = { safePopBackStack() },
                onNavigateToAddAnniversary = {
                    navController.navigate(Screen.AddAnniversary.route) {
                        popUpTo(Screen.AddItem.route) { inclusive = true }
                    }
                },
                onNavigateToAddCheckIn = {
                    navController.navigate(Screen.AddCheckIn.route) {
                        popUpTo(Screen.AddItem.route) { inclusive = true }
                    }
                },
                onNavigateToAddAsset = {
                    navController.navigate(Screen.AddAsset.route) {
                        popUpTo(Screen.AddItem.route) { inclusive = true }
                    }
                },
                onNavigateToAddSubscription = {
                    navController.navigate(Screen.AddSubscription.route) {
                        popUpTo(Screen.AddItem.route) { inclusive = true }
                    }
                },
                onNavigateToAddPlan = {
                    navController.navigate(Screen.AddPlan.route) {
                        popUpTo(Screen.AddItem.route) { inclusive = true }
                    }
                },
                onNavigateToAddDiary = {
                    navController.navigate(Screen.AddDiary.route) {
                        popUpTo(Screen.AddItem.route) { inclusive = true }
                    }
                }
            )
        }

        // Settings
        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = { safePopBackStack() },
                isDarkMode = isDarkMode,
                onDarkModeChanged = onDarkModeChanged,
                onNavigateToCategoryManagement = { safeNavigate(Screen.CategoryManagement.route) }
            )
        }

        // Category Management
        composable(Screen.CategoryManagement.route) {
            CategoryManagementScreen(
                database = database,
                onNavigateBack = { safePopBackStack() }
            )
        }

        // Anniversary
        composable(
            route = Screen.AnniversaryDetail.route,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("id") ?: return@composable
            AnniversaryDetailScreen(
                anniversaryId = id,
                database = database,
                onNavigateBack = { safePopBackStack() }
            )
        }

        composable(Screen.AddAnniversary.route) {
            AddAnniversaryScreen(
                database = database,
                onNavigateBack = { safePopBackStack() }
            )
        }

        // Check-in
        composable(
            route = Screen.CheckInDetail.route,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("id") ?: return@composable
            CheckInScreen(
                checkInId = id,
                database = database,
                onNavigateBack = { safePopBackStack() }
            )
        }

        composable(Screen.AddCheckIn.route) {
            AddCheckInScreen(
                database = database,
                onNavigateBack = { safePopBackStack() }
            )
        }

        // Assets
        composable(Screen.AssetList.route) {
            AssetScreen(
                database = database,
                onNavigateBack = { safePopBackStack() },
                onNavigateToAdd = { safeNavigate(Screen.AddAsset.route) }
            )
        }

        composable(Screen.AddAsset.route) {
            AddAssetScreen(
                database = database,
                onNavigateBack = { safePopBackStack() }
            )
        }

        // Subscriptions
        composable(Screen.SubscriptionList.route) {
            SubscriptionScreen(
                database = database,
                onNavigateBack = { safePopBackStack() },
                onNavigateToAdd = { safeNavigate(Screen.AddSubscription.route) }
            )
        }

        composable(Screen.AddSubscription.route) {
            AddSubscriptionScreen(
                database = database,
                onNavigateBack = { safePopBackStack() }
            )
        }

        // Plans
        composable(Screen.PlanList.route) {
            PlanScreen(
                database = database,
                onNavigateBack = { safePopBackStack() },
                onNavigateToAdd = { navController.navigate(Screen.AddPlan.route) },
                onNavigateToDetail = { id -> safeNavigate(Screen.PlanDetail.createRoute(id)) }
            )
        }

        composable(Screen.AddPlan.route) {
            AddPlanScreen(
                database = database,
                onNavigateBack = { safePopBackStack() }
            )
        }

        composable(
            route = Screen.PlanDetail.route,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("id") ?: return@composable
            PlanDetailScreen(
                planId = id,
                database = database,
                onNavigateBack = { safePopBackStack() }
            )
        }

        // Diary
        composable(Screen.DiaryList.route) {
            DiaryScreen(
                database = database,
                onNavigateBack = { safePopBackStack() },
                onNavigateToAdd = { safeNavigate(Screen.AddDiary.route) },
                onNavigateToDetail = { id -> safeNavigate(Screen.DiaryDetail.createRoute(id)) }
            )
        }

        composable(Screen.AddDiary.route) {
            AddDiaryScreen(
                database = database,
                onNavigateBack = { safePopBackStack() }
            )
        }

        composable(
            route = Screen.DiaryDetail.route,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("id") ?: return@composable
            DiaryDetailScreen(
                diaryId = id,
                database = database,
                onNavigateBack = { safePopBackStack() }
            )
        }
    }
}
