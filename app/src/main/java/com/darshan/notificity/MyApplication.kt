package com.darshan.notificity

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.darshan.notificity.analytics.AnalyticsService
import com.darshan.notificity.analytics.FirebaseAnalyticsTracker
import com.darshan.notificity.worker.CleanupWorker
import java.util.concurrent.TimeUnit

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        AnalyticsService.init(FirebaseAnalyticsTracker())
        createNotificationChannels()
        setupRecurringWork()
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

    private fun setupRecurringWork() {
        val constraints = Constraints.Builder()
            .setRequiresCharging(true)
            .build()

        val repeatingRequest = PeriodicWorkRequestBuilder<CleanupWorker>(
            1, TimeUnit.DAYS
        )
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            CleanupWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            repeatingRequest
        )
    }
}
