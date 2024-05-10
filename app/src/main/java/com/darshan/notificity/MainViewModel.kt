package com.darshan.notificity

import android.app.Application
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.pm.PackageManager.*
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.app.NotificationManagerCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


class MainViewModel(private val application: Application, repository: NotificationRepository) : AndroidViewModel(application) {


    private val packageManager = application.packageManager

    val notifications: LiveData<List<NotificationEntity>> = repository.getAllNotifications()
    // Live data to observe apps with their notification counts
    val appsInfo: LiveData<List<AppInfo>> = notifications.map { notifications ->
        notifications.groupBy { it.packageName }.map { entry ->
            AppInfo(
                appName = loadAppNameFromPackageName(packageManager, entry.key),
                icon = loadIconFromPackageName(packageManager, entry.key),
                notificationCount = entry.value.size,
                packageName = entry.key
            )
        }
    }

    val notificationsGroupedByApp: LiveData<Map<String, List<NotificationEntity>>> = notifications.map { notificationList ->
        notificationList.groupBy { it.appName }
    }


    private val _isNotificationPermissionGranted = MutableStateFlow(false)
    val isNotificationPermissionGranted = _isNotificationPermissionGranted.asStateFlow()
    fun refreshNotificationPermission() {
        val enabledListeners = NotificationManagerCompat.getEnabledListenerPackages(application)
        _isNotificationPermissionGranted.update {
            enabledListeners.contains(application.packageName)
        }
    }

    init {
        refreshNotificationPermission()
    }
}

fun loadAppNameFromPackageName(packageManager: PackageManager, packageName: String) : String {
    val ai: ApplicationInfo? = try {
        packageManager.getApplicationInfo(packageName, 0)
    } catch (e: NameNotFoundException) {
        null
    }
    val applicationName =
        (if (ai != null) packageManager.getApplicationLabel(ai) else "(unknown)") as String
    return applicationName
}

fun loadIconFromPackageName(packageManager: PackageManager, packageName: String): ImageBitmap? {

    val ai: ApplicationInfo? = try {
        packageManager.getApplicationInfo(packageName, 0)
    } catch (e: NameNotFoundException) {
        null
    }


        return (ai?.loadIcon(packageManager)?.toBitmap()?.asImageBitmap())


}