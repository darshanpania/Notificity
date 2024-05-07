package com.darshan.notificity

import android.app.Notification
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager.NameNotFoundException
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class NotificityListener : NotificationListenerService() {
    private lateinit var database: AppDatabase

    override fun onCreate() {
        super.onCreate()
        // Initialize the Room database
        database = AppDatabase.getInstance(applicationContext)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val packageName = sbn.packageName
        val notification = sbn.notification
        val extras = notification.extras
        val title = extras.getString(Notification.EXTRA_TITLE,"")
        val text = extras.getCharSequence(Notification.EXTRA_TEXT,"").toString()
        val timestamp = sbn.postTime
        val image = extras.getString(Notification.EXTRA_PICTURE)

        //Find App Name from Package Name
        val pm = applicationContext.packageManager
        val ai: ApplicationInfo? = try {
            pm.getApplicationInfo(packageName, 0)
        } catch (e: NameNotFoundException) {
            null
        }
        val applicationName =
            (if (ai != null) pm.getApplicationLabel(ai) else "(unknown)") as String

        // Create a new notification entity
        val notificationEntity = NotificationEntity(
            packageName = packageName,
            timestamp = timestamp,
            appName = applicationName,
            title = title,
            content = text,
            imageUrl = image,
            extras = extras.toString()
        )

        // Insert the notification into the database using coroutines
        CoroutineScope(Dispatchers.IO).launch {
            database.notificationDao().insertNotification(notificationEntity)
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        // Handle removed notifications if necessary
    }
}

