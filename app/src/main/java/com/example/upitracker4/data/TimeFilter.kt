package com.example.upitracker4.data

enum class TimeFilter(val displayName: String) {
    LAST_HOUR("Last Hour"),
    LAST_2_HOURS("Last 2 Hours"),
    LAST_6_HOURS("Last 6 Hours"),
    LAST_12_HOURS("Last 12 Hours"),
    TODAY("Today"),
    THIS_WEEK("This Week"),
    THIS_MONTH("This Month"),
    ALL_TIME("All Time")
}
