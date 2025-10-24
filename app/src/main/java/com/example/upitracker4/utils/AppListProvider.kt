package com.example.upitracker4.utils

import android.content.Context
import android.content.pm.PackageManager

data class AppInfo(val name: String, val packageName: String)

object AppListProvider {
    fun getInstalledApps(context: Context): List<AppInfo> {
        val pm = context.packageManager
        val installedApps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        val appList = mutableListOf<AppInfo>()

        for (app in installedApps) {
            if (pm.getLaunchIntentForPackage(app.packageName) != null) {
                appList.add(AppInfo(pm.getApplicationLabel(app).toString(), app.packageName))
            }
        }

        return appList.sortedBy { it.name }
    }
}
