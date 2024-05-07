package com.darshan.notificity

import android.app.Application
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.ColorSpace
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageBitmapConfig
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.colorspace.ColorSpaces
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
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
                icon = loadIconFromPackageName(packageManager, entry.key),
                notificationCount = entry.value.size,
                packageName = entry.key
            )
        }.sortedBy { it.appName }
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

fun loadIconFromPackageName(packageManager: PackageManager, packageName: String): ImageBitmap {

    val appInfo = packageManager.getApplicationInfo(packageName,0)
    return appInfo.loadIcon(packageManager).toBitmap().asImageBitmap()

}