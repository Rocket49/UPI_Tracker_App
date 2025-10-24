package com.example.upitracker4

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.upitracker4.data.SettingsManager
import com.example.upitracker4.data.Transaction
import com.example.upitracker4.data.TransactionRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class TransactionViewModel(application: Application, private val repository: TransactionRepository, private val settingsManager: SettingsManager) : AndroidViewModel(application) {

    val pendingAndExtraTransactions: Flow<List<Transaction>> = repository.getPendingAndExtraTransactions()

    private val timerJobs = mutableMapOf<Long, Job>()

    fun insert(transaction: Transaction) = viewModelScope.launch {
        // Get the new ID from the repository
        val newId = repository.insertTransaction(transaction)
        // Start the timer with the correct ID
        startTimerForTransaction(transaction.copy(id = newId))
    }

    private fun startTimerForTransaction(transaction: Transaction) {
        // Cancel any existing timer for this transaction
        timerJobs[transaction.id]?.cancel()

        timerJobs[transaction.id] = viewModelScope.launch {
            val durationMinutes = settingsManager.timerDurationFlow.first()
            delay(durationMinutes * 60 * 1000L) // Convert minutes to milliseconds

            // Check if the transaction is still pending
            val currentTransaction = repository.getTransactionById(transaction.id)
            if (currentTransaction?.status == "Pending") {
                val updatedTransaction = currentTransaction.copy(status = "Follow-up")
                repository.updateTransaction(updatedTransaction)
            }
        }
    }

    fun update(transaction: Transaction) = viewModelScope.launch {
        repository.updateTransaction(transaction)
        // If a transaction is updated (e.g., paid), cancel its timer
        timerJobs[transaction.id]?.cancel()
    }

    fun delete(transaction: Transaction) = viewModelScope.launch {
        repository.deleteTransaction(transaction)
        timerJobs[transaction.id]?.cancel()
    }

    // Function for the UI to get the timer duration
    suspend fun getTimerDuration(): Int {
        return settingsManager.timerDurationFlow.first()
    }

    override fun onCleared() {
        super.onCleared()
        timerJobs.values.forEach { it.cancel() }
    }
}

class TransactionViewModelFactory(private val application: Application, private val repository: TransactionRepository, private val settingsManager: SettingsManager) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TransactionViewModel(application, repository, settingsManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
