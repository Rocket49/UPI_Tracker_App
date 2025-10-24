package com.example.upitracker4.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val expectedAmount: Double,
    val receivedAmount: Double? = null,
    val status: String, // e.g., "Pending", "Paid", "Cancelled"
    val timestamp: Long = System.currentTimeMillis(),
    val upiApp: String? = null
)
