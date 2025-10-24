package com.example.upitracker4.data

import kotlinx.coroutines.flow.Flow

class TransactionRepository(private val transactionDao: TransactionDao) {

    suspend fun insertTransaction(transaction: Transaction): Long {
        return transactionDao.insertTransaction(transaction)
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

    fun getPendingAndExtraTransactions(): Flow<List<Transaction>> {
        return transactionDao.getPendingAndExtraTransactions()
    }

    fun getPaidTransactions(startTime: Long): Flow<List<Transaction>> {
        return transactionDao.getPaidTransactions(startTime)
    }

    fun getPaidTransactionsCount(startTime: Long): Flow<Int> {
        return transactionDao.getPaidTransactionsCount(startTime)
    }

    fun getTotalAmountReceived(startTime: Long): Flow<Double?> {
        return transactionDao.getTotalAmountReceived(startTime)
    }

    suspend fun getPendingAndFollowUpTransactions(): List<Transaction> {
        return transactionDao.getPendingAndFollowUpTransactions()
    }
}
