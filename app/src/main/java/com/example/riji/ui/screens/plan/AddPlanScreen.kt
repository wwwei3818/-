package com.example.riji.ui.screens.plan

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
import com.example.riji.data.entity.Plan
import com.example.riji.ui.components.IconSet
import com.example.riji.ui.components.ProtectedTopBar
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPlanScreen(
    database: AppDatabase,
    onNavigateBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var icon by remember { mutableStateOf("Plan") }

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

    val icons = listOf(
        "Plan" to Icons.Default.Assignment,
        "Flight" to Icons.Default.Flight,
        "Work" to Icons.Default.Work
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("添加计划") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            if (name.isNotBlank()) {
                                scope.launch {
                                    database.planDao().insertPlan(
                                        Plan(
                                            name = name,
                                            category = category,
                                            icon = icon
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
            // Icon
            Text("选择图标", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                icons.forEach { (name, imageVector) ->
                    FilterChip(
                        selected = icon == name,
                        onClick = { icon = name },
                        label = {
                            Icon(
                                imageVector = imageVector,
                                contentDescription = name,
                                tint = if (icon == name) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Name
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("计划名称") },
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
        }
    }
}
