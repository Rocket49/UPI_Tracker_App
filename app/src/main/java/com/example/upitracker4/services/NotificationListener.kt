package com.example.upitracker4.services

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.example.upitracker4.UpiTrackerApplication
import com.example.upitracker4.data.Transaction
import com.example.upitracker4.utils.NotificationParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationListener : NotificationListenerService() {

    private val repository by lazy { (application as UpiTrackerApplication).repository }
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private val allowedApps = setOf(
        "com.google.android.apps.nbu.paisa.user", // Google Pay
        "com.phonepe.app",                       // PhonePe
        "net.one97.paytm"                        // Paytm
    )

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val packageName = sbn.packageName ?: return

        if (packageName !in allowedApps) {
            return
        }

        Log.d("NotificationListener", "--- New UPI Notification Received ---")
        Log.d("NotificationListener", "Package: $packageName")

        val parsedData = NotificationParser.parse(sbn.notification.extras)

        if (parsedData != null) {
            val (amount, transactionId) = parsedData
            Log.d("NotificationListener", "Parsed Amount: $amount, Transaction ID: $transactionId")

            coroutineScope.launch {
                // --- REVERTED: Using a fixed 10-minute time window (600,000 milliseconds) ---
                val timeWindowMillis = 600000L

                val pendingTransactions = repository.getPendingTransactions(System.currentTimeMillis(), timeWindowMillis)
                val matchedTransaction = pendingTransactions.firstOrNull { it.expectedAmount == amount }

                if (matchedTransaction != null) {
                    Log.d("NotificationListener", "Found matching transaction: ${matchedTransaction.id}")
                    val updatedTransaction = matchedTransaction.copy(
                        status = "Paid",
                        receivedAmount = amount,
                        transactionId = transactionId,
                        upiApp = sbn.packageName
                    )
                    repository.updateTransaction(updatedTransaction)
                } else {
                    Log.d("NotificationListener", "No matching pending transaction found for amount: $amount")
                }
            }
        } else {
             Log.d("NotificationListener", "Parser did not find a valid amount in notification from $packageName.")
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        // Optional: Handle notification removal if necessary
    }
}
