package com.example.riji.ui.screens.checkin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.riji.data.AppDatabase
import com.example.riji.data.entity.CheckIn
import com.example.riji.ui.components.IconSet
import com.example.riji.ui.components.ProtectedTopBar
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCheckInScreen(
    database: AppDatabase,
    onNavigateBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var name by remember { mutableStateOf("") }
    var icon by remember { mutableStateOf("Run") }
    var category by remember { mutableStateOf("") }

    // Load categories from database
    val dbCategories by database.categoryDao().getAllCategories().collectAsState(initial = emptyList())
    val categories = remember(dbCategories) {
        dbCategories.filter { it.name != "全部" }.map { it.name }
    }

    // Set default category
    LaunchedEffect(categories) {
        if (category.isEmpty() && categories.isNotEmpty()) {
            category = categories.firstOrNull() ?: ""
        }
    }

    // Preset templates
    val exerciseIcons = listOf(
        "Run" to Icons.Default.DirectionsRun,
        "Walk" to Icons.Default.DirectionsWalk,
        "Fitness" to Icons.Default.FitnessCenter,
        "Bike" to Icons.Default.DirectionsBike,
        "Swim" to Icons.Default.Pool,
        "Yoga" to Icons.Default.SelfImprovement
    )
    val lifeIcons = listOf(
        "TV" to Icons.Default.Tv,
        "Clean" to Icons.Default.CleaningServices,
        "Water" to Icons.Default.WaterDrop,
        "Food" to Icons.Default.Restaurant,
        "Book" to Icons.Default.MenuBook,
        "Music" to Icons.Default.MusicNote
    )

    Scaffold(
        topBar = {
            ProtectedTopBar(
                title = "添加打卡",
                onNavigateBack = onNavigateBack,
                actions = {
                    TextButton(
                        onClick = {
                            if (name.isNotBlank()) {
                                scope.launch {
                                    database.checkInDao().insertCheckIn(
                                        CheckIn(
                                            name = name,
                                            icon = icon,
                                            category = category
                                        )
                                    )
                                    onNavigateBack()
                                }
                            }
                        }
                    ) {
                        Text("保存", fontWeight = FontWeight.Bold)
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Quick templates
            Text("快速选择", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                (exerciseIcons + lifeIcons).forEach { (iconName, imageVector) ->
                    val label = when (iconName) {
                        "Run" -> "跑步"
                        "Walk" -> "散步"
                        "Fitness" -> "健身"
                        "Bike" -> "骑行"
                        "Swim" -> "游泳"
                        "Yoga" -> "瑜伽"
                        "TV" -> "看剧"
                        "Clean" -> "打扫"
                        "Water" -> "喝水"
                        "Food" -> "吃饭"
                        "Book" -> "阅读"
                        "Music" -> "音乐"
                        else -> iconName
                    }
                    FilterChip(
                        selected = icon == iconName && name == label,
                        onClick = {
                            icon = iconName
                            name = label
                        },
                        label = {
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(
                                    imageVector = imageVector,
                                    contentDescription = label,
                                    tint = if (icon == iconName && name == label) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                                Text(label)
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Name
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("打卡名称") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Category - Dynamic from database
            Text("分类", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))
            if (categories.isNotEmpty()) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    categories.forEach { categoryName ->
                        FilterChip(
                            selected = category == categoryName,
                            onClick = { category = categoryName },
                            label = { Text(categoryName) }
                        )
                    }
                }
            } else {
                Text(
                    text = "请先在设置中添加分类",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Icon selection
            Text("选择图标", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))
            val allIcons = listOf(
                "Run" to Icons.Default.DirectionsRun,
                "Walk" to Icons.Default.DirectionsWalk,
                "Fitness" to Icons.Default.FitnessCenter,
                "Bike" to Icons.Default.DirectionsBike,
                "Swim" to Icons.Default.Pool,
                "Yoga" to Icons.Default.SelfImprovement,
                "TV" to Icons.Default.Tv,
                "Clean" to Icons.Default.CleaningServices,
                "Water" to Icons.Default.WaterDrop,
                "Food" to Icons.Default.Restaurant,
                "Book" to Icons.Default.MenuBook,
                "Music" to Icons.Default.MusicNote
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                allIcons.forEach { (iconName, imageVector) ->
                    FilterChip(
                        selected = icon == iconName,
                        onClick = { icon = iconName },
                        label = {
                            Icon(
                                imageVector = imageVector,
                                contentDescription = iconName,
                                tint = if (icon == iconName) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    )
                }
            }
        }
    }
}
