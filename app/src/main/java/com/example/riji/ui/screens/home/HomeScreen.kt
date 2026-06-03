package com.example.riji.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.riji.data.AppDatabase
import com.example.riji.data.entity.*
import com.example.riji.ui.components.CategoryTabs
import com.example.riji.ui.components.ItemCard
import com.example.riji.ui.theme.*
import com.example.riji.util.DateUtils

data class HomeItem(
    val id: Long,
    val icon: String,
    val title: String,
    val subtitle: String,
    val extraInfo: String?,
    val category: String,
    val type: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    database: AppDatabase,
    onNavigateToAdd: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToAnniversary: (Long) -> Unit,
    onNavigateToCheckIn: (Long) -> Unit,
    onNavigateToAssets: () -> Unit,
    onNavigateToSubscriptions: () -> Unit,
    onNavigateToPlanDetail: (Long) -> Unit,
    onNavigateToPlanList: () -> Unit,
    onNavigateToDiaries: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf("全部") }

    // Load categories from database and add "日记" as a fixed category
    val dbCategories by database.categoryDao().getAllCategories().collectAsState(initial = emptyList())
    val categories = remember(dbCategories) {
        listOf("日记") + dbCategories.map { it.name }
    }

    val anniversaries by database.anniversaryDao().getAllAnniversaries().collectAsState(initial = emptyList())
    val checkIns by database.checkInDao().getAllCheckIns().collectAsState(initial = emptyList())
    val assets by database.assetDao().getAllAssets().collectAsState(initial = emptyList())
    val subscriptions by database.subscriptionDao().getActiveSubscriptions().collectAsState(initial = emptyList())
    val plans by database.planDao().getAllPlans().collectAsState(initial = emptyList())
    val diaries by database.diaryDao().getAllDiaries().collectAsState(initial = emptyList())

    // Add navigation callbacks for plan details
    var onNavigateToPlanDetail by remember { mutableStateOf<(Long) -> Unit>({ }) }

    val allItems = remember(anniversaries, checkIns, assets, subscriptions, plans, diaries) {
        val items = mutableListOf<HomeItem>()

        anniversaries.forEach { ann ->
            val days = DateUtils.daysUntil(ann.date)
            val subtitle = when {
                days > 365 -> "${days / 365}年${days % 365}天"
                days > 0 -> "${days}天"
                days == 0L -> "今天"
                else -> "已过${-days}天"
            }
            val prefix = if (days > 0) "还有" else if (days == 0L) "就是" else "已过"
            items.add(
                HomeItem(
                    id = ann.id,
                    icon = ann.icon,
                    title = "${ann.name}$prefix",
                    subtitle = subtitle,
                    extraInfo = DateUtils.formatDate(ann.date),
                    category = ann.category,
                    type = "anniversary"
                )
            )
        }

        checkIns.forEach { ci ->
            items.add(
                HomeItem(
                    id = ci.id,
                    icon = ci.icon,
                    title = ci.name,
                    subtitle = "点击打卡",
                    extraInfo = null,
                    category = ci.category,
                    type = "checkin"
                )
            )
        }

        if (assets.isNotEmpty()) {
            val total = assets.sumOf { it.price }
            items.add(
                HomeItem(
                    id = -1,
                    icon = "💰",
                    title = "我的资产",
                    subtitle = "¥%,.0f".format(total),
                    extraInfo = "${assets.size} 件",
                    category = "life",
                    type = "assets"
                )
            )
        }

        if (subscriptions.isNotEmpty()) {
            // Normalize to monthly price
            val monthly = subscriptions.sumOf { sub ->
                when (sub.cycle) {
                    "yearly" -> sub.price / 12.0
                    "weekly" -> sub.price * 4.3
                    else -> sub.price
                }
            }
            items.add(
                HomeItem(
                    id = -2,
                    icon = "📺",
                    title = "我的订阅",
                    subtitle = "¥%,.0f/月".format(monthly),
                    extraInfo = "${subscriptions.size} 个",
                    category = "life",
                    type = "subscriptions"
                )
            )
        }

        plans.forEach { plan ->
            items.add(
                HomeItem(
                    id = plan.id,
                    icon = plan.icon,
                    title = plan.name,
                    subtitle = "点击查看",
                    extraInfo = null,
                    category = plan.category,
                    type = "plan"
                )
            )
        }

        diaries.take(3).forEach { diary ->
            val preview = diary.content.take(20) + if (diary.content.length > 20) "..." else ""
            items.add(
                HomeItem(
                    id = diary.id,
                    icon = "📝",
                    title = "日记",
                    subtitle = preview,
                    extraInfo = DateUtils.formatDate(diary.createdAt),
                    category = "日记",
                    type = "diary"
                )
            )
        }

        items
    }

    val filteredItems = remember(allItems, selectedCategory) {
        if (selectedCategory == "全部") allItems
        else allItems.filter { it.category == selectedCategory || (selectedCategory == "日记" && it.type == "diary") }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "☁️ 日迹 ☁️",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "设置")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAdd,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "添加")
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Category tabs
            if (categories.isNotEmpty()) {
                CategoryTabs(
                    categories = categories,
                    selectedCategory = selectedCategory,
                    onCategorySelected = { selectedCategory = it },
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 8.dp)
                )
            }

            if (filteredItems.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("☁️", fontSize = 64.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "日迹",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "生活值得你记录",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredItems) { item ->
                        val accentColor = when (item.type) {
                            "anniversary" -> AnniversaryCardBg
                            "checkin" -> CheckInCardBg
                            "assets" -> AssetCardBg
                            "subscriptions" -> BlueAccent
                            "plan" -> PlanCardBg
                            "diary" -> DiaryCardBg
                            else -> LightSurfaceVariant
                        }

                        ItemCard(
                            icon = item.icon,
                            title = item.title,
                            subtitle = item.subtitle,
                            extraInfo = item.extraInfo,
                            accentColor = accentColor,
                            onClick = {
                                when (item.type) {
                                    "anniversary" -> onNavigateToAnniversary(item.id)
                                    "checkin" -> onNavigateToCheckIn(item.id)
                                    "assets" -> onNavigateToAssets()
                                    "subscriptions" -> onNavigateToSubscriptions()
                                    "plan" -> onNavigateToPlanList()
                                    "diary" -> onNavigateToDiaries()
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
