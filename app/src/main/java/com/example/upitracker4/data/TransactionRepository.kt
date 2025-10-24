package com.example.upitracker4.data

import kotlinx.coroutines.flow.Flow

class TransactionRepository(private val transactionDao: TransactionDao) {

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

    // CORRECTED to getPendingAndExtraTransactions
    fun getPendingAndExtraTransactions(): Flow<List<Transaction>> {
        return transactionDao.getPendingAndExtraTransactions()
    }

    fun getPaidTransactions(): Flow<List<Transaction>> {
        return transactionDao.getPaidTransactions()
    }

    fun getPaidTransactionsCount(): Flow<Int> {
        return transactionDao.getPaidTransactionsCount()
    }

    fun getTotalAmountReceived(): Flow<Double?> {
        return transactionDao.getTotalAmountReceived()
    }

    suspend fun getPendingTransactions(): List<Transaction> {
        return transactionDao.getPendingTransactions()
    }
}
