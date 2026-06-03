package com.example.riji.ui.screens.plan

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.riji.data.AppDatabase
import com.example.riji.data.entity.Plan
import com.example.riji.ui.components.ProtectedTopBar
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanScreen(
    database: AppDatabase,
    onNavigateBack: () -> Unit,
    onNavigateToAdd: () -> Unit,
    onNavigateToDetail: (Long) -> Unit
) {
    val plans by database.planDao().getAllPlans().collectAsState(initial = emptyList())
    var selectedCategory by remember { mutableStateOf("all") }
    val categories = listOf("all" to "全部", "life" to "生活", "work" to "工作", "travel" to "旅行", "couple" to "情侣")
    val scope = rememberCoroutineScope()

    var showDeleteDialog by remember { mutableStateOf<Plan?>(null) }
    var isNavigating by remember { mutableStateOf(false) }

    val filteredPlans = remember(plans, selectedCategory) {
        if (selectedCategory == "all") plans
        else plans.filter { it.category == selectedCategory }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("计划清单") },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (!isNavigating) {
                                isNavigating = true
                                onNavigateBack()
                            }
                        },
                        enabled = !isNavigating
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToAdd) {
                        Icon(Icons.Default.Add, contentDescription = "添加")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Category tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { (value, label) ->
                    FilterChip(
                        selected = selectedCategory == value,
                        onClick = { selectedCategory = value },
                        label = { Text(label) }
                    )
                }
            }

            if (filteredPlans.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📋", style = MaterialTheme.typography.headlineLarge)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("还没有计划", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    items(filteredPlans) { plan ->
                        PlanCard(
                            plan = plan,
                            database = database,
                            onClick = { onNavigateToDetail(plan.id) },
                            onDelete = { showDeleteDialog = plan }
                        )
                    }
                }
            }
        }
    }

    // Delete confirmation dialog
    showDeleteDialog?.let { plan ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("删除计划") },
            text = { Text("确定要删除「${plan.name}」及其所有任务吗？") },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            database.planDao().deletePlan(plan)
                            showDeleteDialog = null
                        }
                    }
                ) {
                    Text("删除", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("取消")
                }
            }
        )
    }
}

@Composable
private fun PlanCard(
    plan: Plan,
    database: AppDatabase,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val taskCount by database.planDao().getTaskCount(plan.id).collectAsState(initial = 0)
    val completedCount by database.planDao().getCompletedTaskCount(plan.id).collectAsState(initial = 0)

    // Use manual progress if set, otherwise calculate from tasks
    val progress = if (plan.manualProgress >= 0) {
        plan.manualProgress.toFloat() / 100f
    } else {
        if (taskCount > 0) completedCount.toFloat() / taskCount else 0f
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${plan.icon} ${plan.name}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "进度 ${(progress * 100).toInt()}%%",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "删除",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (plan.manualProgress >= 0) "手动设置" else "已完成 $completedCount/$taskCount",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
