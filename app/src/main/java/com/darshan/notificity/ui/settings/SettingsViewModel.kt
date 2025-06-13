package com.darshan.notificity.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.darshan.notificity.AppDatabase
import com.darshan.notificity.Constants
import com.darshan.notificity.Constants.PREF_KEY_RETENTION_PERIOD
import com.darshan.notificity.NotificationRepository
import com.darshan.notificity.analytics.AnalyticsLogger
import com.darshan.notificity.ui.theme.ThemeMode
import com.darshan.notificity.ui.theme.ThemePreferenceManager
import com.darshan.notificity.utils.PreferenceManager
import com.darshan.notificity.worker.CleanupWorker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class SettingsViewModel(private val application: Application) : AndroidViewModel(application) {

    private val repository: NotificationRepository

    private val _themeMode = MutableStateFlow(ThemeMode.SYSTEM)
    val themeMode = _themeMode.asStateFlow()

    private val _retentionPeriod = MutableStateFlow(Constants.RetentionPeriod.UNLIMITED)
    val retentionPeriod = _retentionPeriod.asStateFlow()

    init {
        val notificationDao = AppDatabase.getInstance(application).notificationDao()
        repository = NotificationRepository(notificationDao)

        viewModelScope.launch {
            ThemePreferenceManager.getThemeFlow(application.applicationContext).collect {
                _themeMode.value = it
            }
        }

        viewModelScope.launch {
            PreferenceManager.getIntFlow(
                application.applicationContext,
                PREF_KEY_RETENTION_PERIOD,
                Constants.RetentionPeriod.UNLIMITED
            ).collect {
                _retentionPeriod.value = it
            }
        }
    }

    fun updateTheme(theme: ThemeMode) {
        viewModelScope.launch {
            ThemePreferenceManager.saveTheme(application.applicationContext, theme)
            _themeMode.value = theme

            AnalyticsLogger.onThemeToggleClicked(theme.name)
        }
    }

    fun updateRetentionPeriod(newPeriod: Int) {
        viewModelScope.launch {
            PreferenceManager.saveInt(application.applicationContext, PREF_KEY_RETENTION_PERIOD, newPeriod)

            val workManager = WorkManager.getInstance(application.applicationContext)

            if (newPeriod == Constants.RetentionPeriod.UNLIMITED) {
                // If retention is set to unlimited, cancel the periodic cleanup task.
                workManager.cancelUniqueWork(CleanupWorker.WORK_NAME)
            } else {
                // If a specific retention period is set, perform an immediate cleanup for instant feedback.
                val cutoffDays = newPeriod.toLong()
                val cutoffTimestamp = System.currentTimeMillis() - (cutoffDays * 24 * 60 * 60 * 1000)
                repository.deleteNotificationsOlderThan(cutoffTimestamp)

                // And ensure the periodic cleanup task is scheduled.
                // Using REPLACE ensures that if the work already exists, it's updated.
                val constraints = Constraints.Builder()
                    .setRequiresCharging(true)
                    .build()

                val repeatingRequest = PeriodicWorkRequestBuilder<CleanupWorker>(1, TimeUnit.DAYS)
                    .setConstraints(constraints)
                    .build()

                workManager.enqueueUniquePeriodicWork(
                    CleanupWorker.WORK_NAME,
                    ExistingPeriodicWorkPolicy.REPLACE,
                    repeatingRequest
                )
            }
        }
    }
}