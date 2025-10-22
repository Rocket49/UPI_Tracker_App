package com.example.upitracker4

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.upitracker4.data.Transaction
import com.example.upitracker4.data.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class TransactionViewModel(private val repository: TransactionRepository) : ViewModel() {

    val allTransactions: Flow<List<Transaction>> = repository.allTransactions
    val pendingTransactions: Flow<List<Transaction>> = repository.getTransactionsByStatus("Pending")

    fun insert(transaction: Transaction) = viewModelScope.launch {
        repository.insertTransaction(transaction)
    }

    fun update(transaction: Transaction) = viewModelScope.launch {
        repository.updateTransaction(transaction)
    }

    // --- NEW ---
    fun delete(transaction: Transaction) = viewModelScope.launch {
        repository.deleteTransaction(transaction)
    }
}

class TransactionViewModelFactory(private val repository: TransactionRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TransactionViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
