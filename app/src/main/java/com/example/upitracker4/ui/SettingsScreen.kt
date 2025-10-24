package com.example.upitracker4.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.upitracker4.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController, viewModel: SettingsViewModel) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { GeneralSettings(viewModel) }
        }
    }
}

@Composable
fun GeneralSettings(viewModel: SettingsViewModel) {
    val keepScreenOn by viewModel.keepScreenOn.collectAsState()
    val timerDuration by viewModel.timerDuration.collectAsState()
    // Use a local state for the text field to allow for temporary empty states
    var timerText by remember { mutableStateOf(timerDuration.toString()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("General", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Keep Screen On", modifier = Modifier.weight(1f))
                Switch(checked = keepScreenOn, onCheckedChange = { viewModel.setKeepScreenOn(it) })
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            OutlinedTextField(
                value = timerText,
                onValueChange = { value ->
                    timerText = value
                    // Only update the viewmodel if the value is a valid integer
                    value.toIntOrNull()?.let {
                        viewModel.setTimerDuration(it)
                    }
                },
                label = { Text("Pending Timer (minutes)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
