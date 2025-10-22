package com.example.upitracker4.data

import kotlinx.coroutines.flow.Flow

class TransactionRepository(private val transactionDao: TransactionDao) {

    val allTransactions: Flow<List<Transaction>> = transactionDao.getAllTransactions()

    suspend fun insertTransaction(transaction: Transaction) {
        transactionDao.insertTransaction(transaction)
    }

    suspend fun updateTransaction(transaction: Transaction) {
        transactionDao.updateTransaction(transaction)
    }

    suspend fun deleteTransaction(transaction: Transaction) {
        transactionDao.deleteTransaction(transaction)
    }

    suspend fun getTransactionById(id: Long): Transaction? {
        return transactionDao.getTransactionById(id)
    }

    fun getTransactionsByStatus(status: String): Flow<List<Transaction>> {
        return transactionDao.getTransactionsByStatus(status)
    }

    suspend fun getPendingTransactions(currentTime: Long, timeWindow: Long): List<Transaction> {
        return transactionDao.getPendingTransactions(currentTime, timeWindow)
    }
}
