package com.example.upitracker4.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.upitracker4.HistoryViewModel
import com.example.upitracker4.data.TimeFilter
import com.example.upitracker4.data.Transaction
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(navController: NavController, viewModel: HistoryViewModel) {
    val paidTransactions by viewModel.paidTransactions.collectAsState(initial = emptyList())
    val paidTransactionsCount by viewModel.paidTransactionsCount.collectAsState(initial = 0)
    val totalAmountReceived by viewModel.totalAmountReceived.collectAsState(initial = 0.0)
    val selectedFilter by viewModel.timeFilter.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Transaction History") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    FilterDropDown(selectedFilter, onFilterSelected = { viewModel.setFilter(it) })
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            HistorySummary(paidTransactionsCount, totalAmountReceived ?: 0.0)
            
            LazyColumn(contentPadding = PaddingValues(16.dp)) {
                items(paidTransactions) { transaction ->
                    HistoryItem(transaction)
                    Divider(color = Color.LightGray, thickness = 0.5.dp)
                }
            }
        }
    }
}

@Composable
fun FilterDropDown(selectedFilter: TimeFilter, onFilterSelected: (TimeFilter) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(Icons.Default.MoreVert, contentDescription = "Filter")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            TimeFilter.values().forEach { filter ->
                DropdownMenuItem(text = { Text(filter.displayName) }, onClick = { 
                    onFilterSelected(filter)
                    expanded = false
                })
            }
        }
    }
}

@Composable
fun HistorySummary(transactionCount: Int, totalAmount: Double) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Total Payments", style = MaterialTheme.typography.titleMedium)
                Text(
                    text = transactionCount.toString(),
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Total Received", style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "₹${String.format("%.2f", totalAmount)}",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF4CAF50)
                )
            }
        }
    }
}

@Composable
fun HistoryItem(transaction: Transaction) {
    val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    val formattedDate = sdf.format(Date(transaction.timestamp))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Default.CheckCircle, 
            contentDescription = "Paid", 
            tint = Color(0xFF4CAF50),
            modifier = Modifier.size(40.dp)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "₹${transaction.receivedAmount}", 
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        }
        
        Text(
            text = formattedDate,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
    }
}
