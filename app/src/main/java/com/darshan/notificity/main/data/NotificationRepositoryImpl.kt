package com.darshan.notificity.main.data

import com.darshan.notificity.NotificationDao
import com.darshan.notificity.NotificationEntity
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class NotificationRepositoryImpl @Inject constructor(private val notificationDao: NotificationDao) :
    NotificationRepository {
    override suspend fun insertNotification(notificationEntity: NotificationEntity) {
        notificationDao.insertNotification(notificationEntity)
    }

    override fun getAllNotificationsFlow(): Flow<List<NotificationEntity>> {
        return notificationDao.getAllNotificationsFlow()
    }

    override suspend fun deleteNotification(notificationEntity: NotificationEntity) {
        notificationDao.deleteNotification(notificationEntity)
    }

    override suspend fun deleteNotificationsOlderThan(cutoffTimestamp: Long): Int {
        return notificationDao.deleteNotificationsOlderThan(cutoffTimestamp)
    }
}
