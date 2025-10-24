
package com.example.upitracker4

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
        val application = (application as UpiTrackerApplication)
        TransactionViewModelFactory(application, application.repository, application.settingsManager)
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
                        composable("settings") { SettingsScreen(navController, settingsViewModel) }
                        composable("history") { HistoryScreen(navController, historyViewModel) }
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
    val transactions by viewModel.pendingAndExtraTransactions.collectAsState(initial = emptyList())
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
        ) {
            CreateTransactionCard(expectedAmount, onAmountChange = { expectedAmount = it }) {
                val amount = expectedAmount.toDoubleOrNull()
                if (amount != null) {
                    val newTransaction = Transaction(expectedAmount = amount, status = "Pending", timestamp = System.currentTimeMillis())
                    viewModel.insert(newTransaction)
                    expectedAmount = ""
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)) {
                items(transactions, key = { it.id }) { transaction ->
                    AnimatedVisibility(
                        visible = true, // This will be updated later
                        enter = fadeIn(animationSpec = tween(durationMillis = 300)),
                        exit = fadeOut(animationSpec = tween(durationMillis = 300))
                    ) {
                        TransactionItem(transaction = transaction, viewModel = viewModel)
                        Divider(color = Color.LightGray, thickness = 0.5.dp)
                    }
                }
            }
        }
    }
}

@Composable
fun CreateTransactionCard(amount: String, onAmountChange: (String) -> Unit, onCreateClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = amount,
                onValueChange = onAmountChange,
                label = { Text("Expected Amount") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Button(onClick = onCreateClick) {
                Text("Create")
            }
        }
    }
}


@Composable
fun TransactionItem(transaction: Transaction, viewModel: TransactionViewModel) {
    val isExtra = transaction.status == "extra"
    val isFollowUp = transaction.status == "Follow-up"
    val isPending = transaction.status == "Pending"

    val statusColor = when {
        isFollowUp -> Color.Red
        isPending -> Color(0xFFFFA000)
        isExtra -> Color(0xFF2196F3)
        else -> Color.Gray
    }

    val statusIcon = when {
        isFollowUp -> Icons.Default.Warning
        isPending -> Icons.Default.HourglassTop
        isExtra -> Icons.Default.AddCircle
        else -> Icons.Default.History
    }

    val amountText = if (isExtra) {
        "₹${transaction.receivedAmount}"
    } else {
        "₹${transaction.expectedAmount}"
    }

    val statusText = when {
        isFollowUp -> "Pending"
        isPending -> "Pending"
        isExtra -> "extra"
        else -> transaction.status
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            statusIcon, 
            contentDescription = transaction.status, 
            tint = statusColor,
            modifier = Modifier.size(40.dp)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = amountText, 
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = statusText, 
                style = MaterialTheme.typography.bodyLarge,
                color = statusColor
            )
        }
        
        IconButton(onClick = { viewModel.delete(transaction) }) {
            Icon(Icons.Default.Delete, contentDescription = "Delete Transaction", tint = MaterialTheme.colorScheme.error)
        }
    }
}

