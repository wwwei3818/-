package com.example.riji.ui.screens.plan

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.riji.data.AppDatabase
import com.example.riji.data.entity.Plan
import com.example.riji.data.entity.PlanTask
import com.example.riji.ui.components.ProtectedTopBar
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanDetailScreen(
    planId: Long,
    database: AppDatabase,
    onNavigateBack: () -> Unit
) {
    var plan by remember { mutableStateOf<Plan?>(null) }
    val tasks by database.planDao().getTasksForPlan(planId).collectAsState(initial = emptyList())
    val taskCount by database.planDao().getTaskCount(planId).collectAsState(initial = 0)
    val completedCount by database.planDao().getCompletedTaskCount(planId).collectAsState(initial = 0)
    val scope = rememberCoroutineScope()

    var showAddTask by remember { mutableStateOf(false) }
    var newTaskTitle by remember { mutableStateOf("") }
    var showDeletePlanDialog by remember { mutableStateOf(false) }
    var isManualMode by remember { mutableStateOf(false) }
    var manualProgress by remember { mutableStateOf(0f) }

    LaunchedEffect(planId) {
        val loadedPlan = database.planDao().getPlanById(planId)
        plan = loadedPlan
        if (loadedPlan != null && loadedPlan.manualProgress >= 0) {
            isManualMode = true
            manualProgress = loadedPlan.manualProgress.toFloat()
        }
    }

    // Calculate progress based on mode
    val autoProgress = if (taskCount > 0) completedCount.toFloat() / taskCount else 0f
    val displayProgress = if (isManualMode) manualProgress / 100f else autoProgress

    Scaffold(
        topBar = {
            ProtectedTopBar(
                title = plan?.name ?: "计划",
                onNavigateBack = onNavigateBack,
                actions = {
                    IconButton(onClick = { showDeletePlanDialog = true }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "删除计划",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddTask = true }) {
                Icon(Icons.Default.Add, contentDescription = "添加任务")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Progress card
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${plan?.icon ?: "📋"} ${plan?.name ?: ""}",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "进度 ${(displayProgress * 100).roundToInt()}%%",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        LinearProgressIndicator(
                            progress = { displayProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // Progress mode switch
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isManualMode) "手动设置" else "自动计算 (${completedCount}/${taskCount})",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Switch(
                                checked = isManualMode,
                                onCheckedChange = { enabled ->
                                    isManualMode = enabled
                                    scope.launch {
                                        val newPlan = plan?.copy(
                                            manualProgress = if (enabled) manualProgress.roundToInt() else -1
                                        )
                                        newPlan?.let {
                                            database.planDao().updatePlan(it)
                                            plan = it
                                        }
                                    }
                                }
                            )
                        }

                        // Manual progress slider
                        if (isManualMode) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Slider(
                                    value = manualProgress,
                                    onValueChange = { manualProgress = it },
                                    onValueChangeFinished = {
                                        scope.launch {
                                            val newPlan = plan?.copy(
                                                manualProgress = manualProgress.roundToInt()
                                            )
                                            newPlan?.let {
                                                database.planDao().updatePlan(it)
                                                plan = it
                                            }
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    valueRange = 0f..100f
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "${manualProgress.roundToInt()}%",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Tasks list header
            item {
                Text(
                    text = "任务列表",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Medium
                )
            }

            // Tasks
            if (tasks.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "还没有任务，点击 + 添加",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(tasks) { task ->
                    TaskItem(
                        task = task,
                        onToggle = { updatedTask ->
                            scope.launch {
                                database.planDao().updateTask(updatedTask)
                            }
                        },
                        onDelete = { deletedTask ->
                            scope.launch {
                                database.planDao().deleteTask(deletedTask)
                            }
                        }
                    )
                }
            }
        }
    }

    // Add task dialog
    if (showAddTask) {
        AlertDialog(
            onDismissRequest = { showAddTask = false },
            title = { Text("添加任务") },
            text = {
                OutlinedTextField(
                    value = newTaskTitle,
                    onValueChange = { newTaskTitle = it },
                    label = { Text("任务标题") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newTaskTitle.isNotBlank()) {
                            scope.launch {
                                database.planDao().insertTask(
                                    PlanTask(
                                        planId = planId,
                                        title = newTaskTitle,
                                        sortOrder = tasks.size
                                    )
                                )
                                newTaskTitle = ""
                                showAddTask = false
                            }
                        }
                    }
                ) {
                    Text("添加")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddTask = false }) {
                    Text("取消")
                }
            }
        )
    }

    // Delete plan dialog
    if (showDeletePlanDialog) {
        AlertDialog(
            onDismissRequest = { showDeletePlanDialog = false },
            title = { Text("删除计划") },
            text = { Text("确定要删除「${plan?.name}」及其所有任务吗？此操作不可撤销。") },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            plan?.let { database.planDao().deletePlan(it) }
                            showDeletePlanDialog = false
                            onNavigateBack()
                        }
                    }
                ) {
                    Text("删除", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeletePlanDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
}

@Composable
private fun TaskItem(
    task: PlanTask,
    onToggle: (PlanTask) -> Unit,
    onDelete: (PlanTask) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    onToggle(
                        task.copy(
                            isCompleted = !task.isCompleted,
                            completedAt = if (!task.isCompleted) System.currentTimeMillis() else null
                        )
                    )
                }
            ) {
                Icon(
                    imageVector = if (task.isCompleted) Icons.Default.Check else Icons.Default.Close,
                    contentDescription = if (task.isCompleted) "完成" else "未完成",
                    tint = if (task.isCompleted) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = task.title,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f),
                color = if (task.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant
                else MaterialTheme.colorScheme.onSurface
            )

            IconButton(onClick = { onDelete(task) }) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "删除",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
