package com.darshan.notificity.service

import android.app.Notification
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.darshan.notificity.data.NotificationRepository
import com.darshan.notificity.database.NotificationEntity
import com.darshan.notificity.utils.Logger
import com.darshan.notificity.utils.NotificationValidator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NotificityListenerService : NotificationListenerService() {

    @Inject
    lateinit var repository: NotificationRepository

    val TAG = this::class.java.simpleName

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val packageName = sbn.packageName
        val notification = sbn.notification
        val extras = notification.extras
        val title = extras.getCharSequence(Notification.EXTRA_TITLE, "").toString()
        val text = extras.getCharSequence(Notification.EXTRA_TEXT, "").toString()
        val timestamp = sbn.postTime
        val image = extras.getString(Notification.EXTRA_PICTURE)

        // Find App Name from Package Name
        val pm = applicationContext.packageManager
        val ai: ApplicationInfo? =
            try {
                pm.getApplicationInfo(packageName, 0)
            } catch (_: PackageManager.NameNotFoundException) {
                null
            }
        val applicationName =
            (if (ai != null) pm.getApplicationLabel(ai) else "(unknown)") as String

        // Skip group summary notifications (e.g., "3 new messages")
        if (NotificationValidator.isGroupSummary(notification)) {
            Logger.d(TAG, "Skipped group summary notification from: $packageName")
            return
        }

        // Skip generic summary/count notifications using regex patterns
        if (NotificationValidator.isSummaryText(text)) {
            Logger.d(TAG, "Skipped summary notification: \"$text\" from $packageName")
            return
        }

        // Create a new notification entity
        val newNotification =
            NotificationEntity(
                notificationId = sbn.id,
                packageName = packageName,
                timestamp = timestamp,
                appName = applicationName,
                title = title,
                content = text,
                imageUrl = image,
                extras = extras.toString()
            )

        if (NotificationValidator.isValidContent(newNotification.title, newNotification.content)) {
            // Insert the notification into the database using coroutines
            CoroutineScope(Dispatchers.IO).launch {
                repository.insertNotification(newNotification)
            }
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        // Handle removed notifications if necessary
    }
}