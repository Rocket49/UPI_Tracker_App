package com.example.upitracker4.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.upitracker4.UpiTrackerApplication
import com.example.upitracker4.viewmodels.SettingsViewModel
import com.example.upitracker4.viewmodels.SettingsViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    val app = navController.context.applicationContext as UpiTrackerApplication
    val settingsViewModel: SettingsViewModel = viewModel(factory = SettingsViewModelFactory(app.settingsManager))

    val keepScreenOn by settingsViewModel.keepScreenOn.collectAsState(initial = false)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier
            .padding(padding)
            .padding(16.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text("Keep Screen On", modifier = Modifier.weight(1f))
                Switch(
                    checked = keepScreenOn,
                    onCheckedChange = { settingsViewModel.setKeepScreenOn(it) }
                )
            }
        }
    }
}
