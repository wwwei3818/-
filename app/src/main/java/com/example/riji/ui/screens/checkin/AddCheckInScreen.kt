package com.example.riji.ui.screens.checkin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.riji.data.AppDatabase
import com.example.riji.data.entity.CheckIn
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
    var icon by remember { mutableStateOf("🏃") }
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
    val exerciseIcons = listOf("🏃" to "跑步", "🚶" to "散步", "💪" to "健身", "🚴" to "骑行", "🏊" to "游泳", "🧘" to "瑜伽")
    val lifeIcons = listOf("📺" to "看剧", "🧹" to "打扫", "💧" to "喝水", "🍜" to "吃饭", "📚" to "阅读", "🎵" to "音乐")

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
                (exerciseIcons + lifeIcons).forEach { (emoji, label) ->
                    FilterChip(
                        selected = icon == emoji && name == label,
                        onClick = {
                            icon = emoji
                            name = label
                        },
                        label = { Text("$emoji $label") }
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
            val allIcons = listOf("🏃", "🚶", "💪", "🚴", "🏊", "🧘", "📺", "🧹", "💧", "🍜", "📚", "🎵", "⚽", "🎮", "✍️", "🎤")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                allIcons.forEach { emoji ->
                    FilterChip(
                        selected = icon == emoji,
                        onClick = { icon = emoji },
                        label = { Text(emoji) }
                    )
                }
            }
        }
    }
}
