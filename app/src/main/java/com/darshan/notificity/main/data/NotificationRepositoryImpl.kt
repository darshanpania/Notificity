package com.darshan.notificity.main.data

import com.darshan.notificity.NotificationDao
import com.darshan.notificity.NotificationEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepositoryImpl
@Inject
constructor(
    private val notificationDao: NotificationDao
) : NotificationRepository {
    override suspend fun insertNotification(notificationEntity: NotificationEntity) {
        notificationDao.insertNotification(notificationEntity)
    }

    override suspend fun getAllNotification(): Flow<List<NotificationEntity>> {
        return notificationDao.getAllNotificationsFlow()
    }

    override suspend fun deleteNotification(notification: NotificationEntity) {
        notificationDao.deleteNotification(notification)
    }
}