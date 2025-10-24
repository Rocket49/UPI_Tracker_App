package com.example.upitracker4.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.upitracker4.HistoryViewModel
import com.example.upitracker4.data.Transaction

@Composable
fun HistoryScreen(viewModel: HistoryViewModel) {
    val paidTransactions by viewModel.paidTransactions.collectAsState(initial = emptyList())
    val paidTransactionsCount by viewModel.paidTransactionsCount.collectAsState(initial = 0)
    val totalAmountReceived by viewModel.totalAmountReceived.collectAsState(initial = 0.0)

    Column(modifier = Modifier.padding(16.dp)) {
        HistorySummary(paidTransactionsCount, totalAmountReceived ?: 0.0)
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(paidTransactions) { transaction ->
                HistoryItem(transaction)
            }
        }
    }
}

@Composable
fun HistorySummary(transactionCount: Int, totalAmount: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Total Payments Received: $transactionCount", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Total Amount: ₹${String.format("%.2f", totalAmount)}", style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
fun HistoryItem(transaction: Transaction) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Amount: ₹${transaction.receivedAmount}", style = MaterialTheme.typography.bodyLarge)
                Text("From: ${transaction.upiApp}", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
