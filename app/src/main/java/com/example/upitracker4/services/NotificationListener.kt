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
        "com.phonepe.app"                        // PhonePe
    )

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val packageName = sbn.packageName ?: return

        if (packageName !in allowedApps) {
            return
        }

        coroutineScope.launch {
            val amount = NotificationParser.parse(packageName, sbn.notification)

            if (amount != null) {
                Log.d("NotificationListener", "Parser found a transaction for \"$amount\" from $packageName")

                val pendingTransactions = repository.getPendingTransactions()
                val matchedTransaction = pendingTransactions.firstOrNull { it.expectedAmount == amount }

                if (matchedTransaction != null) {
                    Log.d("NotificationListener", "Found matching transaction: ${matchedTransaction.id}")
                    val updatedTransaction = matchedTransaction.copy(
                        status = "Paid",
                        receivedAmount = amount,
                        upiApp = sbn.packageName
                    )
                    repository.updateTransaction(updatedTransaction)
                } else {
                    // --- UPDATED: Use 'extra' status as requested ---
                    Log.d("NotificationListener", "No matching pending transaction found. Logging as new extra payment.")
                    val newTransaction = Transaction(
                        expectedAmount = 0.0, 
                        receivedAmount = amount,
                        status = "extra", // Set status to extra
                        timestamp = System.currentTimeMillis(),
                        upiApp = sbn.packageName
                    )
                    repository.insertTransaction(newTransaction)
                    // --- END UPDATE ---
                }
            } else {
                 Log.d("NotificationListener", "Parser ignored notification from $packageName.")
            }
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        // Optional: Handle notification removal if necessary
    }
}
