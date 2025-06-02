package com.darshan.notificity.fcm

import android.content.Context
import android.graphics.Bitmap
import com.darshan.notificity.utils.NotificationUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NotificationHandler(private val context: Context) {

    suspend fun handleNotification(notificationContent: NotificationContent) {
        if (notificationContent.hasImage()) {
            handleNotificationWithImage(notificationContent)
        } else {
            handleSimpleNotification(notificationContent)
        }
    }

    private suspend fun handleNotificationWithImage(notificationContent: NotificationContent) {
        val notificationId = System.currentTimeMillis().toInt()

        // Show the notification instantly without image
        withContext(Dispatchers.Main) {
            showNotification(notificationContent, null, notificationId)
        }

        // Launch a separate coroutine for image download & update the notification
        CoroutineScope(Dispatchers.IO).launch {
            val largeIcon = NotificationUtil.getLargeIcon(
                context,
                notificationContent.imageUrl!!,
                timeout = 5
            )

            if (largeIcon != null) {
                withContext(Dispatchers.Main) {
                    // Update same notification ID with image
                    showNotification(notificationContent, largeIcon, notificationId)
                }
            }
        }
    }

    private suspend fun handleSimpleNotification(notificationContent: NotificationContent) {
        val notificationId = System.currentTimeMillis().toInt()

        withContext(Dispatchers.Main) {
            showNotification(notificationContent, null, notificationId)
        }
    }

    private fun showNotification(notificationContent: NotificationContent, largeIcon: Bitmap?, notificationId: Int) {
        NotificationUtil.showNotification(
            context = context,
            title = notificationContent.title,
            message = notificationContent.body,
            channelId = notificationContent.channelId,
            largeIcon = largeIcon,
            notificationId = notificationId
        )
    }
}