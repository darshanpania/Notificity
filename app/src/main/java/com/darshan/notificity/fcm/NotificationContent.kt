package com.darshan.notificity.fcm

import com.darshan.notificity.utils.Constants
import com.google.firebase.messaging.RemoteMessage

/**
 * Data class representing notification content extracted from a [RemoteMessage].
 */
data class NotificationContent(
    val title: String,
    val body: String,
    val channelId: String,
    val imageUrl: String?
) {
    companion object {
        /**
         * Parses a [RemoteMessage] to extract [NotificationContent].
         *
         * @param remoteMessage The FCM message containing the payload.
         * @return A [NotificationContent] instance if the notification payload is valid; otherwise, null.
         */
        fun fromRemoteMessage(remoteMessage: RemoteMessage): NotificationContent? {
            return fromNotificationPayload(remoteMessage)
        }

        private fun fromNotificationPayload(remoteMessage: RemoteMessage): NotificationContent? {
            val notification = remoteMessage.notification ?: return null
            val title = notification.title
            val body = notification.body

            if (title.isNullOrBlank() || body.isNullOrBlank()) {
                return null
            }

            return NotificationContent(
                title = title,
                body = body,
                channelId = notification.channelId ?: Constants.DEFAULT_CHANNEL_ID,
                imageUrl = notification.imageUrl?.toString()
            )
        }
    }

    fun hasImage(): Boolean = !imageUrl.isNullOrBlank()
}