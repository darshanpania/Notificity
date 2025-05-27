package com.darshan.notificity.fcm

import android.content.Context
import android.graphics.Bitmap
import com.darshan.notificity.utils.NotificationUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NotificationHandler(private val context: Context) {

    suspend fun handleNotification(notificationPayload: NotificationPayload) {
        if (notificationPayload.hasImage()) {
            handleNotificationWithImage(notificationPayload)
        } else {
            handleSimpleNotification(notificationPayload)
        }
    }

    private suspend fun handleNotificationWithImage(notificationPayload: NotificationPayload) {
        val notificationId = System.currentTimeMillis().toInt()

        // Show the notification instantly without image
        withContext(Dispatchers.Main) {
            showNotification(notificationPayload, null, notificationId)
        }

        // Launch a separate coroutine for image download & update the notification
        CoroutineScope(Dispatchers.IO).launch {
            val largeIcon = NotificationUtil.getLargeIcon(
                context,
                notificationPayload.imageUrl!!,
                timeout = 5
            )

            if (largeIcon != null) {
                withContext(Dispatchers.Main) {
                    // Update same notification ID with image
                    showNotification(notificationPayload, largeIcon, notificationId)
                }
            }
        }
    }

    private suspend fun handleSimpleNotification(notificationPayload: NotificationPayload) {
        val notificationId = System.currentTimeMillis().toInt()

        withContext(Dispatchers.Main) {
            showNotification(notificationPayload, null, notificationId)
        }
    }

    private fun showNotification(notificationPayload: NotificationPayload, largeIcon: Bitmap?, notificationId: Int) {
        NotificationUtil.showNotification(
            context = context,
            title = notificationPayload.title,
            message = notificationPayload.body,
            channelId = notificationPayload.channelId,
            largeIcon = largeIcon,
            notificationId = notificationId
        )
    }
}