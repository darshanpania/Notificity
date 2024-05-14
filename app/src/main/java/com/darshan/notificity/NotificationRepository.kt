package com.darshan.notificity

import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow

class NotificationRepository(private val notificationDao: NotificationDao) {

    suspend fun insertNotification(notificationEntity: NotificationEntity){
        notificationDao.insertNotification(notificationEntity)
    }

    fun getAllNotificationsFlow() : Flow<List<NotificationEntity>> = notificationDao.getAllNotificationsFlow()

}