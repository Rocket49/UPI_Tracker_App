package com.example.upitracker4.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsManager(context: Context) {

    private val dataStore = context.dataStore

    companion object {
        val KEEP_SCREEN_ON_KEY = booleanPreferencesKey("keep_screen_on")
        val SELECTED_UPI_APPS_KEY = stringSetPreferencesKey("selected_upi_apps")
    }

    val keepScreenOnFlow: Flow<Boolean> = dataStore.data
        .map {
            it[KEEP_SCREEN_ON_KEY] ?: false
        }

    suspend fun setKeepScreenOn(keepOn: Boolean) {
        dataStore.edit {
            it[KEEP_SCREEN_ON_KEY] = keepOn
        }
    }

    val selectedUpiAppsFlow: Flow<Set<String>> = dataStore.data
        .map {
            it[SELECTED_UPI_APPS_KEY] ?: emptySet()
        }

    suspend fun setSelectedUpiApps(selectedApps: Set<String>) {
        dataStore.edit {
            it[SELECTED_UPI_APPS_KEY] = selectedApps
        }
    }
}
