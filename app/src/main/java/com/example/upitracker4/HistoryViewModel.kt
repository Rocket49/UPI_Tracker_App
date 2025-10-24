package com.example.upitracker4

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.upitracker4.data.TransactionRepository
import kotlinx.coroutines.flow.asStateFlow

class HistoryViewModel(private val repository: TransactionRepository) : ViewModel() {

    val paidTransactions = repository.getPaidTransactions()
    val paidTransactionsCount = repository.getPaidTransactionsCount()
    val totalAmountReceived = repository.getTotalAmountReceived()

}

class HistoryViewModelFactory(private val repository: TransactionRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HistoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HistoryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
