package com.example.riji.ui.screens.diary

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.riji.data.AppDatabase
import com.example.riji.data.entity.Diary
import com.example.riji.ui.components.ProtectedTopBar
import com.example.riji.util.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryScreen(
    database: AppDatabase,
    onNavigateBack: () -> Unit,
    onNavigateToAdd: () -> Unit,
    onNavigateToDetail: (Long) -> Unit
) {
    val diaries by database.diaryDao().getAllDiaries().collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            ProtectedTopBar(
                title = "日记",
                onNavigateBack = onNavigateBack
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAdd,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "添加日记")
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        if (diaries.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📝", fontSize = 64.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "还没有日记",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "那些不想发朋友圈的话\n也可以安静记下来",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(diaries) { diary ->
                    DiaryFeedCard(
                        diary = diary,
                        onClick = { onNavigateToDetail(diary.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun DiaryFeedCard(
    diary: Diary,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Content
            Text(
                text = diary.content,
                style = MaterialTheme.typography.bodyLarge,
                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight
            )

            // Images grid
            if (diary.images.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))

                val imageUris = diary.images.split(",").filter { it.isNotBlank() }

                if (imageUris.size == 1) {
                    // Single image
                    val uri = try { Uri.parse(imageUris[0]) } catch (e: Exception) { null }
                    uri?.let {
                        AsyncImage(
                            model = it,
                            contentDescription = "Diary image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 300.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                } else {
                    // Multiple images in grid
                    val rows = imageUris.chunked(2)
                    rows.forEach { rowImages ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            rowImages.forEach { uriString ->
                                val uri = try { Uri.parse(uriString) } catch (e: Exception) { null }
                                uri?.let {
                                    AsyncImage(
                                        model = it,
                                        contentDescription = "Diary image",
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(150.dp)
                                            .clip(RoundedCornerShape(8.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                            // Fill empty space if odd number of images
                            if (rowImages.size < 2) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                        if (rowImages != rows.last()) {
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }
            }

            // Footer: Date + Tags
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Date
                Text(
                    text = DateUtils.formatDate(diary.createdAt),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Tags
                if (diary.tags.isNotBlank()) {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        diary.tags.split(",").take(3).forEach { tag ->
                            Text(
                                text = "#${tag.trim()}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}
