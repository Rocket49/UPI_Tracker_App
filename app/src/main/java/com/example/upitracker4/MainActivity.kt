
package com.example.upitracker4

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.upitracker4.data.Transaction
import com.example.upitracker4.ui.theme.UPITracker4Theme

class MainActivity : ComponentActivity() {

    private val transactionViewModel: TransactionViewModel by viewModels {
        TransactionViewModelFactory((application as UpiTrackerApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!isNotificationServiceEnabled()) {
            startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
        }

        setContent {
            UPITracker4Theme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    TrackerScreen(transactionViewModel)
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

@Composable
fun TrackerScreen(viewModel: TransactionViewModel) {
    val transactions by viewModel.allTransactions.collectAsState(initial = emptyList())
    var expectedAmount by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
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

        // --- CORRECTED TEST BUTTON LOGIC ---
        Button(onClick = {
            // Find the last transaction that is still pending.
            val lastPending = transactions.lastOrNull { it.status == "Pending" }
            if (lastPending != null) {
                // Create an updated version of the transaction.
                val updatedTransaction = lastPending.copy(
                    status = "Paid",
                    receivedAmount = lastPending.expectedAmount, // Simulate receiving the correct amount
                    transactionId = "TEST_ID_FROM_BUTTON"
                )
                // Directly update the database via the ViewModel.
                viewModel.update(updatedTransaction)
            }
        }) {
            Text("Test Last Pending Transaction")
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(transactions) { transaction ->
                TransactionItem(transaction)
            }
        }
    }
}

@Composable
fun TransactionItem(transaction: Transaction) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Amount: ₹${transaction.expectedAmount}", style = MaterialTheme.typography.bodyLarge)
                Text(text = "Status: ${transaction.status}", style = MaterialTheme.typography.bodyMedium)
                if (transaction.receivedAmount != null) {
                    Text(text = "Received: ₹${transaction.receivedAmount}", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
