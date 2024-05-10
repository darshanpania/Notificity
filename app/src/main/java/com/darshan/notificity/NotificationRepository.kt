package com.darshan.notificity

import androidx.lifecycle.LiveData

class NotificationRepository(private val notificationDao: NotificationDao) {

    suspend fun insertNotification(notificationEntity: NotificationEntity){
        notificationDao.insertNotification(notificationEntity)
    }

    fun getAllNotifications() : LiveData<List<NotificationEntity>> = notificationDao.getAllNotifications()

}