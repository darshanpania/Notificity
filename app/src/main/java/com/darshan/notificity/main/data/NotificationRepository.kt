package com.darshan.notificity.main.data

import com.darshan.notificity.NotificationEntity
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    suspend fun insertNotification(notificationEntity: NotificationEntity)

    suspend fun getAllNotification(): Flow<List<NotificationEntity>>

    suspend fun deleteNotification(notification: NotificationEntity)
}