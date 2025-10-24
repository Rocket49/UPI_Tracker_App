package com.example.upitracker4.utils

import android.app.Notification
import android.os.Bundle
import android.util.Log

object NotificationParser {

    fun parse(packageName: String, notification: Notification): Double? {
        val extras = notification.extras
        val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString() ?: ""
        val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString() ?: ""
        
        return when (packageName) {
            "com.google.android.apps.nbu.paisa.user" -> parseGPay(title)
            "com.phonepe.app" -> parsePhonePe(title, text)
            else -> null
        }
    }

    private fun parseGPay(title: String): Double? {
        val amountRegex = """(?:rs\.?|inr|\u20B9)\s*([\d,]+\.?\d*)""".toRegex(RegexOption.IGNORE_CASE)
        val keywordRegex = """(paid you|sent you|payment from|received from)""".toRegex(RegexOption.IGNORE_CASE)

        val amountMatch = amountRegex.find(title)
        val keywordMatch = keywordRegex.find(title)

        if (amountMatch != null && keywordMatch != null) {
            Log.d("NotificationParser", "GPay rule matched.")
            return amountMatch.groups[1]?.value?.replace(",", "")?.toDoubleOrNull()
        }
        return null
    }

    private fun parsePhonePe(title: String, text: String): Double? {
        // Use a more lenient check for the title
        if (title.contains("Money receiv", ignoreCase = true)) {
            // Use the robust regex that finds any currency symbol in the body
            val amountRegex = """(?:rs\.?|inr|\u20B9)\s*([\d,]+\.?\d*)""".toRegex(RegexOption.IGNORE_CASE)
            val amountMatch = amountRegex.find(text)
            
            if (amountMatch != null) {
                Log.d("NotificationParser", "PhonePe rule matched.")
                return amountMatch.groups[1]?.value?.replace(",", "")?.toDoubleOrNull()
            }
        }
        return null
    }
}
