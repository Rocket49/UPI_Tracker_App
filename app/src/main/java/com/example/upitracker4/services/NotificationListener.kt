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

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        // --- ADDING LOGGING FOR DEBUGGING ---
        val packageName = sbn.packageName ?: "Unknown"
        val extras = sbn.notification.extras
        Log.d("NotificationListener", "--- New Notification Received ---")
        Log.d("NotificationListener", "Package: $packageName")
        for (key in extras.keySet()) {
            Log.d("NotificationListener", "Key: $key, Value: ${extras.get(key)}")
        }
        Log.d("NotificationListener", "---------------------------------")

        // The rest of the logic remains the same
        val parsedData = NotificationParser.parse(sbn.notification.extras)

        if (parsedData != null) {
            val (amount, transactionId) = parsedData
            Log.d("NotificationListener", "Parsed Amount: $amount, Transaction ID: $transactionId")

            coroutineScope.launch {
                val pendingTransactions = repository.getPendingTransactions(System.currentTimeMillis(), 600000)
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
             Log.d("NotificationListener", "Parser did not find a valid amount.")
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        // Optional: Handle notification removal if necessary
    }
}
