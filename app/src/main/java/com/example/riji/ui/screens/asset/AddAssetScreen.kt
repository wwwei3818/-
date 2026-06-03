package com.example.riji.ui.screens.asset

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.riji.data.AppDatabase
import com.example.riji.data.entity.Asset
import com.example.riji.ui.components.IconSet
import com.example.riji.ui.components.ProtectedTopBar
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAssetScreen(
    database: AppDatabase,
    onNavigateBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var icon by remember { mutableStateOf("Phone") }
    var note by remember { mutableStateOf("") }

    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(System.currentTimeMillis()) }

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

    // Price validation
    val isValidPrice = price.toDoubleOrNull() != null && (price.toDoubleOrNull() ?: 0.0) > 0

    val icons = listOf(
        "Phone" to Icons.Default.Phone,
        "Computer" to Icons.Default.Computer,
        "Headphones" to Icons.Default.Headphones,
        "Home" to Icons.Default.Home
    )

    Scaffold(
        topBar = {
            ProtectedTopBar(
                title = "添加资产",
                onNavigateBack = onNavigateBack,
                actions = {
                    TextButton(
                        onClick = {
                            if (name.isNotBlank() && isValidPrice) {
                                scope.launch {
                                    database.assetDao().insertAsset(
                                        Asset(
                                            name = name,
                                            price = price.toDoubleOrNull() ?: 0.0,
                                            purchaseDate = selectedDate,
                                            category = category,
                                            icon = icon,
                                            note = note
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
                label = { Text("资产名称") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Price
            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text("购买价格 (¥)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = price.isNotBlank() && !isValidPrice,
                supportingText = {
                    if (price.isNotBlank() && !isValidPrice) {
                        Text("请输入有效的价格")
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Purchase date
            OutlinedButton(
                onClick = { showDatePicker = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("购买日期: ${java.time.Instant.ofEpochMilli(selectedDate).atZone(java.time.ZoneId.systemDefault()).toLocalDate()}")
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

            // Note
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("备注") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDate)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { selectedDate = it }
                    showDatePicker = false
                }) { Text("确定") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("取消") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
