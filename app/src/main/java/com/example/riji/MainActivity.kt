package com.example.riji

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.riji.navigation.RijiNavGraph
import com.example.riji.ui.theme.RijiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as RijiApp
        val database = app.database

        // Load dark mode preference from SharedPreferences
        val prefs = getSharedPreferences("riji_prefs", Context.MODE_PRIVATE)

        setContent {
            var isDarkMode by remember { mutableStateOf(prefs.getBoolean("dark_mode", false)) }

            // Save dark mode preference when changed
            LaunchedEffect(isDarkMode) {
                prefs.edit().putBoolean("dark_mode", isDarkMode).apply()
            }

            RijiTheme(darkTheme = isDarkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    // Debounce rapid back clicks to prevent white screen
                    var lastBackClickTime by remember { mutableStateOf(0L) }

                    // Ensure navController always has a valid start destination
                    LaunchedEffect(navController) {
                        navController.addOnDestinationChangedListener { _, destination, _ ->
                            // If somehow we end up with no destination, navigate to home
                            if (destination.route == null) {
                                navController.navigate("home") {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        }
                    }

                    RijiNavGraph(
                        navController = navController,
                        database = database,
                        isDarkMode = isDarkMode,
                        onDarkModeChanged = { isDarkMode = it }
                    )
                }
            }
        }
    }
}
