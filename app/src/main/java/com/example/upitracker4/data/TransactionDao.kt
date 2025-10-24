package com.example.upitracker4.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Insert
    suspend fun insertTransaction(transaction: Transaction): Long

    @Update
    suspend fun updateTransaction(transaction: Transaction)

    @Delete
    suspend fun deleteTransaction(transaction: Transaction)

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getTransactionById(id: Long): Transaction?

    @Query("SELECT * FROM transactions WHERE status IN ('Pending', 'extra', 'Follow-up') ORDER BY timestamp DESC")
    fun getPendingAndExtraTransactions(): Flow<List<Transaction>>

    // CORRECTED: Now accepts a start time to filter transactions.
    @Query("SELECT * FROM transactions WHERE status = 'Paid' AND timestamp >= :startTime ORDER BY timestamp DESC")
    fun getPaidTransactions(startTime: Long): Flow<List<Transaction>>

    @Query("SELECT COUNT(id) FROM transactions WHERE status = 'Paid' AND timestamp >= :startTime")
    fun getPaidTransactionsCount(startTime: Long): Flow<Int>

    @Query("SELECT SUM(receivedAmount) FROM transactions WHERE status = 'Paid' AND timestamp >= :startTime")
    fun getTotalAmountReceived(startTime: Long): Flow<Double?>
    
    @Query("SELECT * FROM transactions WHERE status IN ('Pending', 'Follow-up')")
    suspend fun getPendingAndFollowUpTransactions(): List<Transaction>
}
