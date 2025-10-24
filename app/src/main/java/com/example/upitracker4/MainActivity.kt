
package com.example.upitracker4

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.upitracker4.data.Transaction
import com.example.upitracker4.ui.HistoryScreen
import com.example.upitracker4.ui.SettingsScreen
import com.example.upitracker4.ui.theme.UPITracker4Theme
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {

    private val transactionViewModel: TransactionViewModel by viewModels {
        TransactionViewModelFactory((application as UpiTrackerApplication).repository)
    }

    private val settingsViewModel: SettingsViewModel by viewModels {
        SettingsViewModelFactory(application, (application as UpiTrackerApplication).settingsManager)
    }

    private val historyViewModel: HistoryViewModel by viewModels {
        HistoryViewModelFactory((application as UpiTrackerApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!isNotificationServiceEnabled()) {
            startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
        }

        lifecycleScope.launch {
            (application as UpiTrackerApplication).settingsManager.keepScreenOnFlow.collectLatest {
                if (it) {
                    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                } else {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                }
            }
        }

        setContent {
            UPITracker4Theme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "tracker") {
                        composable("tracker") { TrackerScreen(navController, transactionViewModel) }
                        composable("settings") { SettingsScreen(settingsViewModel) }
                        composable("history") { HistoryScreen(historyViewModel) }
                    }
                }
            }
        }
    }

    private fun isNotificationServiceEnabled(): Boolean {
        val enabledListeners = Settings.Secure.getString(contentResolver, "enabled_notification_listeners")
        val componentName = ComponentName(this, com.example.upitracker4.services.NotificationListener::class.java)
        return enabledListeners?.contains(componentName.flattenToString()) == true
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackerScreen(navController: androidx.navigation.NavController, viewModel: TransactionViewModel) {
    val transactions by viewModel.pendingAndUnexpectedTransactions.collectAsState(initial = emptyList())
    var expectedAmount by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("UPI Tracker") },
                actions = {
                    IconButton(onClick = { navController.navigate("history") }) {
                        Icon(Icons.Default.History, contentDescription = "History")
                    }
                    IconButton(onClick = { navController.navigate("settings") }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = expectedAmount,
                    onValueChange = { expectedAmount = it },
                    label = { Text("Expected Amount") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = {
                    val amount = expectedAmount.toDoubleOrNull()
                    if (amount != null) {
                        viewModel.insert(Transaction(expectedAmount = amount, status = "Pending"))
                        expectedAmount = ""
                    }
                }) {
                    Text("Create")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(transactions) { transaction ->
                    TransactionItem(transaction = transaction, viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun TransactionItem(transaction: Transaction, viewModel: TransactionViewModel) {
    val isExtra = transaction.status == "extra"
    
    val statusColor = when (transaction.status) {
        "Pending" -> Color(0xFFFFA000)
        "extra" -> Color(0xFF2196F3) // Blue for extra payments
        else -> Color.Gray
    }
    
    val statusIcon = when (transaction.status) {
        "Pending" -> Icons.Default.HourglassTop
        "extra" -> Icons.Default.AddCircle
        else -> Icons.Default.History
    }
    
    // --- THE DEFINITIVE FIX --- 
    // For 'extra' payments, show the receivedAmount. For 'Pending', show expectedAmount.
    val amountText = if (isExtra) {
        "₹${transaction.receivedAmount}"
    } else {
        "₹${transaction.expectedAmount}"
    }

    val statusText = if (isExtra) {
        "extra"
    } else {
        "Pending"
    }
    // --- END FIX --- 

    ListItem(
        headlineContent = { Text(amountText, style = MaterialTheme.typography.bodyLarge) },
        supportingContent = {
            Text(statusText, style = MaterialTheme.typography.bodyMedium, color = statusColor)
        },
        leadingContent = {
            Icon(statusIcon, contentDescription = transaction.status, tint = statusColor)
        },
        trailingContent = {
            IconButton(onClick = { viewModel.delete(transaction) }) {
                Icon(Icons.Default.Delete, contentDescription = "Delete Transaction", tint = MaterialTheme.colorScheme.error)
            }
        }
    )
}
