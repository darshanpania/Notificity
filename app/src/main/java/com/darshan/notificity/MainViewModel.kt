package com.darshan.notificity

import android.app.Application
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.pm.PackageManager.NameNotFoundException
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.app.NotificationManagerCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(private val application: Application, private val repository: NotificationRepository) :
    AndroidViewModel(application) {

    private val packageManager = application.packageManager

    val notificationsFlow: Flow<List<NotificationEntity>> = repository.getAllNotificationsFlow()

    val appInfoFromFlow: Flow<List<AppInfo>> =
        notificationsFlow.map { notifications ->
            notifications
                .groupBy { it.packageName }
                .map { entry ->
                    AppInfo(
                        appName = loadAppNameFromPackageName(packageManager, entry.key),
                        icon = loadIconFromPackageName(packageManager, entry.key),
                        notificationCount = entry.value.size,
                        packageName = entry.key
                    )
                }
        }

    val notificationsGroupedByAppFlow: Flow<Map<String, List<NotificationEntity>>> =
        notificationsFlow.map { notificationsFLow -> notificationsFLow.groupBy { it.appName } }

    private val _isNotificationPermissionGranted = MutableStateFlow(false)
    val isNotificationPermissionGranted = _isNotificationPermissionGranted.asStateFlow()

    private val _showNotificationPermissionBlockedDialog = MutableStateFlow(false)
    val showNotificationPermissionBlockedDialog =
        _showNotificationPermissionBlockedDialog.asStateFlow()

    fun refreshNotificationPermission() {
        val enabledListeners = NotificationManagerCompat.getEnabledListenerPackages(application)
        _isNotificationPermissionGranted.update {
            enabledListeners.contains(application.packageName)
        }
    }

    fun showNotificationPermissionBlockedDialog(show: Boolean) {
        _showNotificationPermissionBlockedDialog.update {
            show
        }
    }

    init {
        refreshNotificationPermission()
    }

    fun deleteNotification(notificationEntity: NotificationEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteNotification(notificationEntity)
        }
    }

}

fun loadAppNameFromPackageName(packageManager: PackageManager, packageName: String): String {
    val ai: ApplicationInfo? =
        try {
            packageManager.getApplicationInfo(packageName, 0)
        } catch (_: NameNotFoundException) {
            null
        }
    val applicationName =
        (if (ai != null) packageManager.getApplicationLabel(ai) else "(unknown)") as String
    return applicationName
}

fun loadIconFromPackageName(packageManager: PackageManager, packageName: String): ImageBitmap? {

    val ai: ApplicationInfo? =
        try {
            packageManager.getApplicationInfo(packageName, 0)
        } catch (_: NameNotFoundException) {
            null
        }

    return (ai?.loadIcon(packageManager)?.toBitmap()?.asImageBitmap())
}
