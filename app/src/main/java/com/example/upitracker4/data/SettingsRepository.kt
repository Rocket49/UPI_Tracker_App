package com.example.upitracker4.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsRepository(private val context: Context) {

    private val selectedUpiAppsKey = stringSetPreferencesKey("selected_upi_apps")

    val selectedUpiApps: Flow<Set<String>> = context.dataStore.data
        .map {
            it[selectedUpiAppsKey] ?: emptySet()
        }

    suspend fun updateSelectedUpiApps(selectedApps: Set<String>) {
        context.dataStore.edit {
            it[selectedUpiAppsKey] = selectedApps
        }
    }
}
