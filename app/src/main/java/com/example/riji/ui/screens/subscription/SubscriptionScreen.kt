package com.example.riji.ui.screens.subscription

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
import com.example.riji.data.entity.Subscription
import com.example.riji.ui.components.ProtectedTopBar
import com.example.riji.util.DateUtils
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionScreen(
    database: AppDatabase,
    onNavigateBack: () -> Unit,
    onNavigateToAdd: () -> Unit
) {
    val activeSubscriptions by database.subscriptionDao().getActiveSubscriptions().collectAsState(initial = emptyList())
    val inactiveSubscriptions by database.subscriptionDao().getInactiveSubscriptions().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()

    // Calculate monthly total by normalizing each subscription's price
    val activeTotal = remember(activeSubscriptions) {
        activeSubscriptions.sumOf { sub ->
            when (sub.cycle) {
                "yearly" -> sub.price / 12.0
                "weekly" -> sub.price * 4.3
                "monthly" -> sub.price
                else -> sub.price
            }
        }
    }
    val activeCount = activeSubscriptions.size

    var showDeleteDialog by remember { mutableStateOf<Subscription?>(null) }

    Scaffold(
        topBar = {
            ProtectedTopBar(
                title = "订阅管理",
                onNavigateBack = onNavigateBack,
                actions = {
                    IconButton(onClick = onNavigateToAdd) {
                        Icon(Icons.Default.Add, contentDescription = "添加")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            // Overview card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "📊 订阅概览",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Medium
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "¥%,.0f".format(activeTotal ?: 0.0),
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text("月支出", style = MaterialTheme.typography.bodySmall)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "$activeCount 个",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text("活跃订阅", style = MaterialTheme.typography.bodySmall)
                            }
                        }

                        if (activeCount > 0) {
                            val dailyCost = (activeTotal ?: 0.0) / 30
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "日均成本: ¥%.2f    年度支出: ¥%,.0f".format(dailyCost, (activeTotal ?: 0.0) * 12),
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }

            // Active subscriptions
            if (activeSubscriptions.isNotEmpty()) {
                item {
                    Text(
                        text = "📱 活跃订阅",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Medium
                    )
                }

                items(activeSubscriptions) { sub ->
                    SubscriptionItemCard(
                        subscription = sub,
                        isActive = true,
                        onDelete = { showDeleteDialog = sub }
                    )
                }
            }

            // Inactive subscriptions
            if (inactiveSubscriptions.isNotEmpty()) {
                item {
                    Text(
                        text = "❌ 已取消",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Medium
                    )
                }

                items(inactiveSubscriptions) { sub ->
                    SubscriptionItemCard(
                        subscription = sub,
                        isActive = false,
                        onDelete = { showDeleteDialog = sub }
                    )
                }
            }

            if (activeSubscriptions.isEmpty() && inactiveSubscriptions.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("📺", style = MaterialTheme.typography.headlineLarge)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("还没有订阅记录", style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            }
        }
    }

    // Delete confirmation dialog
    showDeleteDialog?.let { subscription ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("删除订阅") },
            text = { Text("确定要删除「${subscription.name}」吗？") },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            database.subscriptionDao().deleteSubscription(subscription)
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
private fun SubscriptionItemCard(
    subscription: Subscription,
    isActive: Boolean,
    onDelete: () -> Unit
) {
    val daysUsed = DateUtils.daysSince(subscription.startDate)
    val dailyCost = if (daysUsed > 0) subscription.price / daysUsed else subscription.price

    val cycleLabel = when (subscription.cycle) {
        "monthly" -> "月"
        "yearly" -> "年"
        "weekly" -> "周"
        else -> "月"
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${subscription.icon} ${subscription.name}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "¥%,.0f/$cycleLabel".format(subscription.price),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
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
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (isActive) "续订中" else "已取消",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
                Text(
                    text = "已用: $daysUsed 天  日均: ¥%.2f".format(dailyCost),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
