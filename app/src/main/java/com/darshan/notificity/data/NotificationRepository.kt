package com.darshan.notificity.data

import com.darshan.notificity.database.NotificationEntity
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    suspend fun insertNotification(notificationEntity: NotificationEntity)

    suspend fun getAllNotification(): Flow<List<NotificationEntity>>

    suspend fun deleteNotification(notification: NotificationEntity)
}