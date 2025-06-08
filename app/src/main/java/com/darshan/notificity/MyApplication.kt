package com.darshan.notificity

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val defaultChannel = NotificationChannel(
            Constants.DEFAULT_CHANNEL_ID,
            Constants.DEFAULT_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = Constants.DEFAULT_CHANNEL_DESCRIPTION
            enableLights(true)
            enableVibration(true)
        }

        notificationManager.createNotificationChannel(defaultChannel)
    }
}
