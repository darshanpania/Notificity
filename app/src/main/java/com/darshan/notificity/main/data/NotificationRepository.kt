package com.darshan.notificity.main.data

import com.darshan.notificity.NotificationEntity
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    suspend fun insertNotification(notificationEntity: NotificationEntity)

    fun getAllNotificationsFlow(): Flow<List<NotificationEntity>>

    suspend fun deleteNotificationsOlderThan(cutoffTimestamp: Long): Int

    suspend fun deleteNotification(notificationEntity: NotificationEntity)
}
