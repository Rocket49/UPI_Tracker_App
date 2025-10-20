package com.example.upitracker4.utils

import android.os.Bundle

object NotificationParser {

    fun parse(notificationExtras: Bundle): Pair<Double, String>? {
        // --- CORRECTED REGEX ---
        // Now handles "rs" and "rs." (with a period)
        val amountRegex = """(?:rs\.?|inr|\u20B9)\s*([\d,]+\.?\d*)""".toRegex(RegexOption.IGNORE_CASE)
        val transactionIdRegex = """txn\s*id[:\s]*([\w\d]+)""".toRegex(RegexOption.IGNORE_CASE)

        val textSources = listOf(
            "android.bigText",
            "android.text",
            "android.title"
        )

        for (sourceKey in textSources) {
            val notificationText = notificationExtras.getCharSequence(sourceKey)?.toString()

            if (notificationText != null) {
                val amountMatch = amountRegex.find(notificationText)
                if (amountMatch != null) {
                    val amount = amountMatch.groups[1]?.value?.replace(",", "")?.toDoubleOrNull()
                    if (amount != null) {
                        val transactionIdMatch = transactionIdRegex.find(notificationText)
                        val transactionId = transactionIdMatch?.groups?.get(1)?.value
                        return Pair(amount, transactionId ?: "N/A")
                    }
                }
            }
        }

        return null
    }
}
