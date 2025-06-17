package com.darshan.notificity.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.darshan.notificity.Constants
import com.darshan.notificity.Constants.PREF_KEY_RETENTION_PERIOD
import com.darshan.notificity.analytics.AnalyticsLogger
import com.darshan.notificity.main.data.NotificationRepository
import com.darshan.notificity.ui.theme.ThemeMode
import com.darshan.notificity.ui.theme.ThemePreferenceManager
import com.darshan.notificity.utils.PreferenceManager
import com.darshan.notificity.worker.CleanupWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel
@Inject
constructor(
    private val themePreferenceManager: ThemePreferenceManager,
    private val repository: NotificationRepository,
    private val preferenceManager: PreferenceManager,
    private val workManager: WorkManager
) : ViewModel() {

    private val _themeMode = MutableStateFlow(ThemeMode.SYSTEM)
    val themeMode = _themeMode.asStateFlow()

    private val _retentionPeriod = MutableStateFlow(Constants.RetentionPeriod.UNLIMITED)
    val retentionPeriod = _retentionPeriod.asStateFlow()

    private val _deletionResult = MutableStateFlow<Int?>(null)
    val deletionResult = _deletionResult.asStateFlow()

    init {
        viewModelScope.launch {
            themePreferenceManager.getThemeFlow().collect { _themeMode.value = it }
        }

        viewModelScope.launch {
            preferenceManager
                .getIntFlow(PREF_KEY_RETENTION_PERIOD, Constants.RetentionPeriod.UNLIMITED)
                .collect { _retentionPeriod.value = it }
        }
    }

    fun updateTheme(theme: ThemeMode) {
        viewModelScope.launch {
            themePreferenceManager.saveTheme(theme)
            _themeMode.value = theme

            AnalyticsLogger.onThemeToggleClicked(theme.name)
        }
    }

    fun updateRetentionPeriod(newPeriod: Int) {
        viewModelScope.launch {
            preferenceManager.saveInt(PREF_KEY_RETENTION_PERIOD, newPeriod)

            if (newPeriod == Constants.RetentionPeriod.UNLIMITED) {
                workManager.cancelUniqueWork(CleanupWorker.WORK_NAME)
            } else {
                val cutoffDays = newPeriod.toLong()
                val cutoffTimestamp =
                    System.currentTimeMillis() - (cutoffDays * 24 * 60 * 60 * 1000)
                val deletedCount = repository.deleteNotificationsOlderThan(cutoffTimestamp)
                _deletionResult.value = deletedCount

                val constraints = Constraints.Builder().setRequiresCharging(true).build()

                val repeatingRequest =
                    PeriodicWorkRequestBuilder<CleanupWorker>(1, TimeUnit.DAYS)
                        .setConstraints(constraints)
                        .build()

                workManager.enqueueUniquePeriodicWork(
                    CleanupWorker.WORK_NAME, ExistingPeriodicWorkPolicy.REPLACE, repeatingRequest)
            }
        }
    }

    fun clearDeletionResult() {
        _deletionResult.value = null
    }
}
