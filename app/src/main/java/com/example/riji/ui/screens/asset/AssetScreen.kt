package com.example.riji.ui.screens.asset

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
import com.example.riji.data.entity.Asset
import com.example.riji.ui.components.IconSet
import com.example.riji.ui.components.ProtectedTopBar
import com.example.riji.util.DateUtils
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssetScreen(
    database: AppDatabase,
    onNavigateBack: () -> Unit,
    onNavigateToAdd: () -> Unit
) {
    val assets by database.assetDao().getAllAssets().collectAsState(initial = emptyList())
    val totalValue by database.assetDao().getTotalAssetValue().collectAsState(initial = 0.0)
    val assetCount by database.assetDao().getAssetCount().collectAsState(initial = 0)
    val scope = rememberCoroutineScope()

    var showDeleteDialog by remember { mutableStateOf<Asset?>(null) }

    val groupedAssets = remember(assets) {
        assets.groupBy { it.category }
    }

    Scaffold(
        topBar = {
            ProtectedTopBar(
                title = "资产管理",
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
                            text = "💰 资产概览",
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
                                    text = "¥%,.0f".format(totalValue ?: 0.0),
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text("总价值", style = MaterialTheme.typography.bodySmall)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "$assetCount 件",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text("资产数量", style = MaterialTheme.typography.bodySmall)
                            }
                        }

                        if (assetCount > 0) {
                            val dailyCost = (totalValue ?: 0.0) / assetCount / 365
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "日均成本: ¥%.2f".format(dailyCost),
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }

            // Assets by category
            groupedAssets.forEach { (category, categoryAssets) ->
                item {
                    val categoryLabel = when (category) {
                        "electronics" -> "📱 电子产品"
                        "appliance" -> "🏠 家电"
                        "clothing" -> "👕 衣物"
                        "daily" -> "📦 生活用品"
                        else -> "📦 其他"
                    }
                    val categoryTotal = categoryAssets.sumOf { it.price }

                    Text(
                        text = "$categoryLabel    ${categoryAssets.size} 件  ¥%,.0f".format(categoryTotal),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                items(categoryAssets) { asset ->
                    AssetItemCard(
                        asset = asset,
                        onDelete = { showDeleteDialog = asset }
                    )
                }
            }

            if (assets.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("💰", style = MaterialTheme.typography.headlineLarge)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("还没有资产记录", style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            }
        }
    }

    // Delete confirmation dialog
    showDeleteDialog?.let { asset ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("删除资产") },
            text = { Text("确定要删除「${asset.name}」吗？") },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            database.assetDao().deleteAsset(asset)
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
private fun AssetItemCard(
    asset: Asset,
    onDelete: () -> Unit
) {
    val daysUsed = DateUtils.daysSince(asset.purchaseDate)
    val dailyCost = if (daysUsed > 0) asset.price / daysUsed else asset.price

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = IconSet.fromName(asset.icon),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = asset.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Medium
                    )
                }
                Text(
                    text = "¥%,.0f".format(asset.price),
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
            Text(
                text = "购买: ${DateUtils.formatDate(asset.purchaseDate)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "已用: $daysUsed 天  日均: ¥%.1f".format(dailyCost),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
