package com.example.upitracker4

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.upitracker4.data.SettingsManager
import com.example.upitracker4.utils.AppInfo
import com.example.upitracker4.utils.AppListProvider
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application, private val settingsManager: SettingsManager) : AndroidViewModel(application) {

    val installedApps: List<AppInfo> = AppListProvider.getInstalledApps(application.applicationContext)

    val selectedUpiApps: StateFlow<Set<String>> = settingsManager.selectedUpiAppsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    val keepScreenOn: StateFlow<Boolean> = settingsManager.keepScreenOnFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun updateSelectedUpiApps(selectedApps: Set<String>) {
        viewModelScope.launch {
            settingsManager.setSelectedUpiApps(selectedApps)
        }
    }

    fun setKeepScreenOn(keepOn: Boolean) {
        viewModelScope.launch {
            settingsManager.setKeepScreenOn(keepOn)
        }
    }
}

class SettingsViewModelFactory(private val application: Application, private val settingsManager: SettingsManager) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(application, settingsManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
