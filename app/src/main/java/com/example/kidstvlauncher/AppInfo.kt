package com.example.kidstvlauncher

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable

data class AppInfo(
    val applicationInfo : ApplicationInfo,
    val packageManager: PackageManager
) {
    val label : String
        get() = packageManager.getApplicationLabel(applicationInfo).toString()
    val icon : Drawable
        get() = packageManager.getApplicationIcon(applicationInfo)
    val packageName: String
        get() = applicationInfo.packageName
}