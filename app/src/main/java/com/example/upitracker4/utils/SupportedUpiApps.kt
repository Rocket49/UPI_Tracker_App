package com.example.upitracker4.utils

data class UpiApp(val name: String, val packageName: String)

object SupportedUpiApps {
    val apps = listOf(
        UpiApp("Google Pay", "com.google.android.apps.nbu.paisa.user"),
        UpiApp("PhonePe", "com.phonepe.app"),
        UpiApp("Paytm", "net.one97.paytm")
    )
}
