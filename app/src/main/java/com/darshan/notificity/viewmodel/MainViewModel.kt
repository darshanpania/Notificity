package com.darshan.notificity.main.viewmodel

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.pm.PackageManager.NameNotFoundException
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.app.NotificationManagerCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darshan.notificity.database.NotificationEntity
import com.darshan.notificity.data.NotificationRepository
import com.darshan.notificity.model.AppInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel
@Inject
constructor(
    @ApplicationContext private val context: Context,
    val repository: NotificationRepository
) : ViewModel() {

    private val packageManager = context.packageManager

    private val _notificationsFlow = MutableStateFlow<List<NotificationEntity>>(emptyList())
    val notificationsFlow: StateFlow<List<NotificationEntity>> =
        _notificationsFlow.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getAllNotification().collect { notifications ->
                _notificationsFlow.value = notifications
            }
        }
    }

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
        val enabledListeners = NotificationManagerCompat.getEnabledListenerPackages(context)
        _isNotificationPermissionGranted.update {
            enabledListeners.contains(context.packageName)
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
}