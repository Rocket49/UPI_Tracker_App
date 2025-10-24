package com.example.upitracker4

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.upitracker4.data.SettingsManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application, private val settingsManager: SettingsManager) : AndroidViewModel(application) {

    val keepScreenOn: StateFlow<Boolean> = settingsManager.keepScreenOnFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val timerDuration: StateFlow<Int> = settingsManager.timerDurationFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 3)

    fun setKeepScreenOn(keepOn: Boolean) {
        viewModelScope.launch {
            settingsManager.setKeepScreenOn(keepOn)
        }
    }

    fun setTimerDuration(duration: Int) {
        viewModelScope.launch {
            settingsManager.setTimerDuration(duration)
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
