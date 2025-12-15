package com.darshan.notificity.main.data

import com.darshan.notificity.NotificationEntity
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    suspend fun insertNotification(notificationEntity: NotificationEntity)

    suspend fun getAllNotification(): Flow<List<NotificationEntity>>

    suspend fun deleteNotification(notification: NotificationEntity)

    // Added: Delete all notifications for a specific app by appName (for Clear All feature)
    suspend fun deleteAllNotificationsForApp(appName: String)
}