package com.example.riji.ui.screens.subscription

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
import com.example.riji.data.entity.Subscription
import com.example.riji.ui.components.IconSet
import com.example.riji.ui.components.ProtectedTopBar
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSubscriptionScreen(
    database: AppDatabase,
    onNavigateBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var cycle by remember { mutableStateOf("monthly") }
    var icon by remember { mutableStateOf("Music") }
    var note by remember { mutableStateOf("") }

    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(System.currentTimeMillis()) }

    // Price validation
    val isValidPrice = price.toDoubleOrNull() != null && (price.toDoubleOrNull() ?: 0.0) > 0

    val cycles = listOf("monthly" to "月付", "yearly" to "年付", "weekly" to "周付")
    val icons = listOf(
        "Music" to Icons.Default.MusicNote,
        "TV" to Icons.Default.Tv,
        "Subscription" to Icons.Default.Subscriptions
    )

    Scaffold(
        topBar = {
            ProtectedTopBar(
                title = "添加订阅",
                onNavigateBack = onNavigateBack,
                actions = {
                    TextButton(
                        onClick = {
                            if (name.isNotBlank() && isValidPrice) {
                                scope.launch {
                                    database.subscriptionDao().insertSubscription(
                                        Subscription(
                                            name = name,
                                            price = price.toDoubleOrNull() ?: 0.0,
                                            cycle = cycle,
                                            startDate = selectedDate,
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
                label = { Text("订阅名称") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Price
            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text("价格 (¥)") },
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

            // Cycle
            Text("订阅周期", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                cycles.forEach { (value, label) ->
                    FilterChip(
                        selected = cycle == value,
                        onClick = { cycle = value },
                        label = { Text(label) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Start date
            OutlinedButton(
                onClick = { showDatePicker = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("开始日期: ${java.time.Instant.ofEpochMilli(selectedDate).atZone(java.time.ZoneId.systemDefault()).toLocalDate()}")
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
