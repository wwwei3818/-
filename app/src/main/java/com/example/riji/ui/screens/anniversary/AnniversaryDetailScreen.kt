package com.example.riji.ui.screens.anniversary

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.riji.data.AppDatabase
import com.example.riji.data.entity.Anniversary
import com.example.riji.ui.components.ProtectedTopBar
import com.example.riji.util.DateUtils
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnniversaryDetailScreen(
    anniversaryId: Long,
    database: AppDatabase,
    onNavigateBack: () -> Unit
) {
    var anniversary by remember { mutableStateOf<Anniversary?>(null) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(anniversaryId) {
        anniversary = database.anniversaryDao().getAnniversaryById(anniversaryId)
    }

    Scaffold(
        topBar = {
            ProtectedTopBar(
                title = anniversary?.name ?: "纪念日",
                onNavigateBack = onNavigateBack,
                actions = {
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "删除")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        anniversary?.let { ann ->
            val days = DateUtils.daysUntil(ann.date)
            val birthDate = java.time.Instant.ofEpochMilli(ann.date)
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDate()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Countdown card
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
                        Text(
                            text = ann.icon,
                            style = MaterialTheme.typography.headlineLarge
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (days > 0) "还有 $days 天" else if (days == 0L) "就是今天!" else "已过 ${-days} 天",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = DateUtils.formatDate(ann.date),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Date info card
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "📅 日期信息",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Medium
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                        if (ann.type == "birthday") {
                            val age = DateUtils.getAge(ann.date)
                            val virtualAge = DateUtils.getVirtualAge(ann.date)
                            val zodiac = DateUtils.getChineseZodiac(birthDate.year)
                            val constellation = DateUtils.getConstellation(birthDate.monthValue, birthDate.dayOfMonth)

                            InfoRow("出生日期", DateUtils.formatDate(ann.date))
                            InfoRow("年龄", "$age 岁")
                            InfoRow("虚岁", "$virtualAge 岁")
                            InfoRow("星座", constellation)
                            InfoRow("生肖", zodiac)
                        } else {
                            InfoRow("日期", DateUtils.formatDate(ann.date))
                            InfoRow("类型", when(ann.type) {
                                "anniversary" -> "纪念日"
                                "deadline" -> "截止日"
                                "payday" -> "发薪日"
                                "rent" -> "交租日"
                                else -> "纪念日"
                            })
                        }
                        InfoRow("重复", when(ann.repeatType) {
                            "yearly" -> "每年"
                            "monthly" -> "每月"
                            "weekly" -> "每周"
                            "daily" -> "每日"
                            else -> "不重复"
                        })
                        InfoRow("分类", when(ann.category) {
                            "life" -> "生活"
                            "work" -> "工作"
                            "couple" -> "情侣"
                            "travel" -> "旅行"
                            else -> "生活"
                        })
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Reminder card
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "🔔 提醒设置",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Medium
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        InfoRow("提前提醒", "${ann.remindDaysBefore} 天前")
                        InfoRow("提醒时间", ann.remindTime ?: "未设置")
                    }
                }

                // Note card
                if (ann.note.isNotBlank()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "📝 备注",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Medium
                            )
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                            Text(
                                text = ann.note,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }
        } ?: Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text("加载中...", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }

    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("删除纪念日") },
            text = { Text("确定要删除「${anniversary?.name}」吗？此操作不可撤销。") },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            anniversary?.let {
                                database.anniversaryDao().deleteAnniversary(it)
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
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}
