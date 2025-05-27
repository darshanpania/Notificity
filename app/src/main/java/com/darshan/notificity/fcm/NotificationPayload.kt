package com.darshan.notificity.fcm

import com.darshan.notificity.Constants
import com.google.firebase.messaging.RemoteMessage

data class NotificationPayload(
    val title: String,
    val body: String,
    val channelId: String,
    val imageUrl: String?
) {
    companion object {
        fun fromRemoteMessage(remoteMessage: RemoteMessage): NotificationPayload? {
            return fromNotificationPayload(remoteMessage)
        }

        private fun fromNotificationPayload(remoteMessage: RemoteMessage): NotificationPayload? {
            val notification = remoteMessage.notification ?: return null
            val title = notification.title
            val body = notification.body

            if (title.isNullOrBlank() || body.isNullOrBlank()) {
                return null
            }

            return NotificationPayload(
                title = title,
                body = body,
                channelId = notification.channelId ?: Constants.DEFAULT_CHANNEL_ID,
                imageUrl = notification.imageUrl?.toString()
            )
        }
    }

    fun hasImage(): Boolean = !imageUrl.isNullOrBlank()
}