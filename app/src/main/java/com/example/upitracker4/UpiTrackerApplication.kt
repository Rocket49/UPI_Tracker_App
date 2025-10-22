package com.example.upitracker4

import android.app.Application
import com.example.upitracker4.data.AppDatabase
import com.example.upitracker4.data.SettingsManager
import com.example.upitracker4.data.TransactionRepository

class UpiTrackerApplication : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { TransactionRepository(database.transactionDao()) }
    val settingsManager by lazy { SettingsManager(this) }
}
