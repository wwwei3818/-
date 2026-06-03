package com.example.riji.ui.screens.checkin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.riji.data.AppDatabase
import com.example.riji.data.entity.CheckIn
import com.example.riji.data.entity.CheckInRecord
import com.example.riji.ui.components.IconSet
import com.example.riji.ui.components.ProtectedTopBar
import com.example.riji.util.DateUtils
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckInScreen(
    checkInId: Long,
    database: AppDatabase,
    onNavigateBack: () -> Unit
) {
    var checkIn by remember { mutableStateOf<CheckIn?>(null) }
    val scope = rememberCoroutineScope()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var isCheckingIn by remember { mutableStateOf(false) }

    val records by database.checkInDao().getRecordsForCheckIn(checkInId).collectAsState(initial = emptyList())
    val totalRecords by database.checkInDao().getRecordCount(checkInId).collectAsState(initial = 0)
    val lastCheckInDate by database.checkInDao().getLastCheckInDate(checkInId).collectAsState(initial = null)

    LaunchedEffect(checkInId) {
        checkIn = database.checkInDao().getCheckInById(checkInId)
    }

    // Calculate stats
    val today = DateUtils.getStartOfDay()
    val todayCount = records.count { DateUtils.getStartOfDay(it.date) == today }

    // Current month records
    val currentMonth = YearMonth.now()
    val monthStart = currentMonth.atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    val monthEnd = currentMonth.atEndOfMonth().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    val monthRecords = records.filter { it.date in monthStart..monthEnd }
    val monthCount = monthRecords.size

    // Streak calculation (with safety limit of 365 days)
    val streak = remember(records) {
        var count = 0
        var checkDate = LocalDate.now()
        val maxIterations = 365
        while (count < maxIterations && records.any { DateUtils.getStartOfDay(it.date) == DateUtils.getStartOfDay(checkDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()) }) {
            count++
            checkDate = checkDate.minusDays(1)
        }
        count
    }

    Scaffold(
        topBar = {
            ProtectedTopBar(
                title = checkIn?.name ?: "打卡",
                onNavigateBack = onNavigateBack,
                actions = {
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "删除")
                    }
                }
            )
        }
    ) { paddingValues ->
        checkIn?.let { ci ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Check-in status card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = IconSet.fromName(ci.icon),
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        val isCheckedToday = records.any { DateUtils.getStartOfDay(it.date) == today }
                        Text(
                            text = if (isCheckedToday) "今日已打卡 ✓" else "今日未打卡",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )

                        if (streak > 0) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "🔥 连续 $streak 天",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Stats card
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "📊 数据统计",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Medium
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            StatItem("今日", "$todayCount 次")
                            StatItem("本月", "$monthCount 次")
                            StatItem("总计", "$totalRecords 次")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Monthly heatmap
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "📅 月度热力图",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Medium
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                        val daysInMonth = currentMonth.lengthOfMonth()
                        val firstDayOfWeek = currentMonth.atDay(1).dayOfWeek.value % 7

                        // Day headers
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            listOf("日", "一", "二", "三", "四", "五", "六").forEach { day ->
                                Text(
                                    text = day,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.weight(1f),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Calendar grid
                        var dayCounter = 1
                        for (week in 0..5) {
                            if (dayCounter > daysInMonth) break
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                for (dayOfWeek in 0..6) {
                                    if (week == 0 && dayOfWeek < firstDayOfWeek || dayCounter > daysInMonth) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    } else {
                                        val day = dayCounter
                                        val date = currentMonth.atDay(day).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                                        val isChecked = records.any { DateUtils.getStartOfDay(it.date) == DateUtils.getStartOfDay(date) }

                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .aspectRatio(1f)
                                                .padding(2.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    if (isChecked) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                                    else MaterialTheme.colorScheme.surface
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "$day",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = if (isChecked) MaterialTheme.colorScheme.primary
                                                else MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                        dayCounter++
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Check-in button
                Button(
                    onClick = {
                        if (!isCheckingIn) {
                            isCheckingIn = true
                            scope.launch {
                                try {
                                    val todayStart = DateUtils.getStartOfDay()
                                    val hasRecord = database.checkInDao().hasRecordOnDate(checkInId, todayStart)
                                    if (!hasRecord) {
                                        database.checkInDao().insertRecord(
                                            CheckInRecord(
                                                checkInId = checkInId,
                                                date = todayStart
                                            )
                                        )
                                    }
                                } finally {
                                    isCheckingIn = false
                                }
                            }
                        }
                    },
                    enabled = !isCheckingIn,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("打卡", style = MaterialTheme.typography.headlineSmall)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Recent records
                if (records.isNotEmpty()) {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "📋 打卡记录",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Medium
                            )
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                            records.take(10).forEach { record ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = DateUtils.formatDate(record.date),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    if (record.note.isNotBlank()) {
                                        Text(
                                            text = record.note,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } ?: Box(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }

    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("删除打卡") },
            text = { Text("确定要删除「${checkIn?.name}」及其所有打卡记录吗？此操作不可撤销。") },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            checkIn?.let {
                                database.checkInDao().deleteCheckIn(it)
                                onNavigateBack()
                            }
                        }
                    }
                ) {
                    Text("删除", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
