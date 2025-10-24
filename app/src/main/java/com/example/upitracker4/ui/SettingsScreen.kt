package com.example.upitracker4.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.upitracker4.SettingsViewModel
import com.example.upitracker4.utils.AppInfo

@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
    val selectedApps by viewModel.selectedUpiApps.collectAsState()
    var tempSelectedApps by remember { mutableStateOf(selectedApps) }
    val keepScreenOn by viewModel.keepScreenOn.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Keep Screen On", modifier = Modifier.weight(1f))
            Switch(
                checked = keepScreenOn,
                onCheckedChange = { viewModel.setKeepScreenOn(it) }
            )
        }

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search Apps") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        val filteredApps = viewModel.installedApps.filter {
            it.name.contains(searchQuery, ignoreCase = true) || it.packageName.contains(searchQuery, ignoreCase = true)
        }

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(filteredApps) { app ->
                AppCheckbox(
                    app = app,
                    isSelected = tempSelectedApps.contains(app.packageName),
                    onCheckedChange = { isChecked ->
                        tempSelectedApps = if (isChecked) {
                            tempSelectedApps + app.packageName
                        } else {
                            tempSelectedApps - app.packageName
                        }
                    }
                )
            }
        }

        if (tempSelectedApps != selectedApps) {
            viewModel.updateSelectedUpiApps(tempSelectedApps)
        }
    }
}

@Composable
fun AppCheckbox(
    app: AppInfo,
    isSelected: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isSelected,
            onCheckedChange = onCheckedChange
        )
        Text(text = app.name, modifier = Modifier.padding(start = 16.dp))
    }
}
