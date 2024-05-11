package com.darshan.notificity

import kotlinx.coroutines.flow.Flow

class NotificationRepository(private val notificationDao: NotificationDao) {

    suspend fun insertNotification(notification: Notification){
        notificationDao.insertNotification(notification)
    }

    fun getAllNotificationsFlow() : Flow<List<Notification>> = notificationDao.getAllNotificationsFlow()

}