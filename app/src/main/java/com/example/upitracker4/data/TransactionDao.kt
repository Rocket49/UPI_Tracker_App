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
    suspend fun insertTransaction(transaction: Transaction)

    @Update
    suspend fun updateTransaction(transaction: Transaction)

    @Delete
    suspend fun deleteTransaction(transaction: Transaction)

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getTransactionById(id: Long): Transaction?

    // UPDATED: Now includes "extra" status for the main screen.
    @Query("SELECT * FROM transactions WHERE status IN ('Pending', 'extra') ORDER BY timestamp DESC")
    fun getPendingAndExtraTransactions(): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE status = 'Paid' ORDER BY timestamp DESC")
    fun getPaidTransactions(): Flow<List<Transaction>>

    @Query("SELECT COUNT(id) FROM transactions WHERE status = 'Paid'")
    fun getPaidTransactionsCount(): Flow<Int>

    @Query("SELECT SUM(receivedAmount) FROM transactions WHERE status = 'Paid'")
    fun getTotalAmountReceived(): Flow<Double?>
    
    @Query("SELECT * FROM transactions WHERE status = 'Pending'")
    suspend fun getPendingTransactions(): List<Transaction>
}
