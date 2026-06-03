package com.example.riji.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProtectedTopBar(
    title: String,
    onNavigateBack: () -> Unit,
    actions: @Composable RowScope.() -> Unit = {}
) {
    var isNavigating by remember { mutableStateOf(false) }

    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            IconButton(
                onClick = {
                    if (!isNavigating) {
                        isNavigating = true
                        onNavigateBack()
                    }
                },
                enabled = !isNavigating
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
            }
        },
        actions = actions
    )
}

@Composable
fun ProtectedIconButton(
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    var isClicked by remember { mutableStateOf(false) }

    IconButton(
        onClick = {
            if (!isClicked) {
                isClicked = true
                onClick()
            }
        },
        enabled = !isClicked
    ) {
        content()
    }
}
