package com.darshan.notificity

import android.R
import android.app.Application
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.pm.PackageManager.NameNotFoundException
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.map


class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application.applicationContext)
    private val packageManager = application.packageManager
    private val dao = db.notificationDao()

    val notifications: LiveData<List<NotificationEntity>> = dao.getAllNotifications()
    // Live data to observe apps with their notification counts
    val appsInfo: LiveData<List<AppInfo>> = notifications.map { notifications ->
        notifications.groupBy { it.packageName }.map { entry ->
            AppInfo(
                appName = loadAppNameFromPackageName(packageManager, entry.key),
                icon = loadIconFromPackageName(application,packageManager, entry.key),
                notificationCount = entry.value.size,
                packageName = entry.key
            )
        }
    }

    val notificationsGroupedByApp: LiveData<Map<String, List<NotificationEntity>>> = notifications.map { notificationList ->
        notificationList.groupBy { it.appName }
    }
}

fun loadAppNameFromPackageName(packageManager: PackageManager, packageName: String) : String {
    val ai: ApplicationInfo? = try {
        packageManager.getApplicationInfo(packageName, 0)
    } catch (e: PackageManager.NameNotFoundException) {
        null
    }
    val applicationName =
        (if (ai != null) packageManager.getApplicationLabel(ai) else "(unknown)") as String
    return applicationName
}

fun loadIconFromPackageName(application: Application,packageManager: PackageManager, packageName: String): ImageBitmap? {

    val ai: ApplicationInfo? = try {
        packageManager.getApplicationInfo(packageName, 0)
    } catch (e: PackageManager.NameNotFoundException) {
        null
    }


        return (ai?.loadIcon(packageManager)?.toBitmap()?.asImageBitmap())


}