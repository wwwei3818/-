package com.example.riji.ui.screens.anniversary

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.riji.data.AppDatabase
import com.example.riji.data.entity.Anniversary
import com.example.riji.ui.components.ProtectedTopBar
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAnniversaryScreen(
    database: AppDatabase,
    onNavigateBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("anniversary") }
    var repeatType by remember { mutableStateOf("yearly") }
    var category by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var icon by remember { mutableStateOf("🎂") }
    var remindDays by remember { mutableStateOf("0") }

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

    // Date picker
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(System.currentTimeMillis()) }

    val types = listOf("birthday" to "生日", "anniversary" to "纪念日", "deadline" to "截止日", "payday" to "发薪日", "rent" to "交租日")
    val repeatTypes = listOf("none" to "不重复", "yearly" to "每年", "monthly" to "每月", "weekly" to "每周")
    val icons = listOf("🎂", "🎉", "💍", "📅", "💰", "🏠", "✈️", "🎓", "🎁", "⭐")

    Scaffold(
        topBar = {
            ProtectedTopBar(
                title = "添加纪念日",
                onNavigateBack = onNavigateBack,
                actions = {
                    TextButton(
                        onClick = {
                            if (name.isNotBlank()) {
                                scope.launch {
                                    database.anniversaryDao().insertAnniversary(
                                        Anniversary(
                                            name = name,
                                            date = selectedDate,
                                            type = type,
                                            repeatType = repeatType,
                                            category = category,
                                            note = note,
                                            icon = icon,
                                            remindDaysBefore = remindDays.toIntOrNull() ?: 0
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
            // Icon selection
            Text("选择图标", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                icons.forEach { emoji ->
                    FilterChip(
                        selected = icon == emoji,
                        onClick = { icon = emoji },
                        label = { Text(emoji) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Name
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("名称") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Date
            OutlinedButton(
                onClick = { showDatePicker = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("选择日期: ${java.time.Instant.ofEpochMilli(selectedDate).atZone(java.time.ZoneId.systemDefault()).toLocalDate()}")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Type
            Text("类型", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                types.forEach { (value, label) ->
                    FilterChip(
                        selected = type == value,
                        onClick = { type = value },
                        label = { Text(label) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Repeat
            Text("重复", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeatTypes.forEach { (value, label) ->
                    FilterChip(
                        selected = repeatType == value,
                        onClick = { repeatType = value },
                        label = { Text(label) }
                    )
                }
            }

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

            // Remind days
            OutlinedTextField(
                value = remindDays,
                onValueChange = { if (it.isEmpty() || it.toIntOrNull() != null) remindDays = it },
                label = { Text("提前提醒天数") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Note
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("备注") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
        }
    }

    // Date picker dialog
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { selectedDate = it }
                    showDatePicker = false
                }) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("取消")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
