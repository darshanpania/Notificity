package com.darshan.notificity.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.darshan.notificity.AppDatabase
import com.darshan.notificity.Constants
import com.darshan.notificity.NotificationRepository
import com.darshan.notificity.utils.PreferenceManager
import kotlinx.coroutines.flow.first

class CleanupWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    companion object {
        const val WORK_NAME = "NotificationCleanup"
    }

    override suspend fun doWork(): Result {
        Log.d(WORK_NAME, "Starting daily notification cleanup task.")

        val repository = NotificationRepository(AppDatabase.getInstance(applicationContext).notificationDao())

        return try {
            // Get the current retention period from preferences
            val retentionPeriod = PreferenceManager.getIntFlow(
                applicationContext,
                Constants.PREF_KEY_RETENTION_PERIOD,
                Constants.RetentionPeriod.UNLIMITED
            ).first()

            if (retentionPeriod != Constants.RetentionPeriod.UNLIMITED) {
                val cutoffDays = retentionPeriod.toLong()
                val cutoffTimestamp = System.currentTimeMillis() - (cutoffDays * 24 * 60 * 60 * 1000)

                Log.d(WORK_NAME, "Retention period is $retentionPeriod days. Deleting notifications older than $cutoffTimestamp.")
                val deletedCount = repository.deleteNotificationsOlderThan(cutoffTimestamp)
                Log.d(WORK_NAME, "Cleanup task finished. Deleted $deletedCount notifications.")
            } else {
                Log.d(WORK_NAME, "Retention period is Unlimited. No cleanup needed.")
            }

            Result.success()
        } catch (e: Exception) {
            Log.e(WORK_NAME, "Error during notification cleanup task", e)
            Result.failure()
        }
    }
}
