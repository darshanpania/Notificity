package com.darshan.notificity.ui.settings

import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.darshan.notificity.Constants
import com.darshan.notificity.analytics.AnalyticsLogger
import com.darshan.notificity.main.data.NotificationRepository
import com.darshan.notificity.ui.theme.ThemeMode
import com.darshan.notificity.ui.theme.ThemePreferenceManager
import com.darshan.notificity.utils.PreferenceManager
import com.darshan.notificity.worker.CleanupWorker
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class SettingsViewModelTest {

    private lateinit var mockNotificationRepository: NotificationRepository
    private lateinit var mockPreferenceManager: PreferenceManager
    private lateinit var mockThemePreferenceManager: ThemePreferenceManager
    private lateinit var mockWorkManager: WorkManager

    private lateinit var settingsViewModel: SettingsViewModel
    private lateinit var testDispatcher: TestDispatcher

    @Before
    fun setUp() {
        testDispatcher = UnconfinedTestDispatcher()
        Dispatchers.setMain(testDispatcher)

        // Mock all dependencies
        mockNotificationRepository = mockk(relaxed = true)
        mockPreferenceManager = mockk(relaxed = true)
        mockThemePreferenceManager = mockk(relaxed = true)
        mockWorkManager = mockk(relaxed = true)

        // Mock static methods
        mockkObject(AnalyticsLogger)
        every { AnalyticsLogger.onThemeToggleClicked(any()) } just Runs

        // Setup default behavior
        every {
            mockPreferenceManager.getIntFlow(
                Constants.PREF_KEY_RETENTION_PERIOD, Constants.RetentionPeriod.UNLIMITED)
        } returns flowOf(Constants.RetentionPeriod.UNLIMITED)
        every { mockThemePreferenceManager.getThemeFlow() } returns flowOf(ThemeMode.SYSTEM)
    }

    private fun createViewModel() {
        settingsViewModel =
            SettingsViewModel(
                mockThemePreferenceManager,
                mockNotificationRepository,
                mockPreferenceManager,
                mockWorkManager)
    }

    @Test
    fun `updateRetentionPeriod to 7 days - deletes old notifications and schedules worker`() =
        runTest {
            createViewModel()
            val newPeriod = 7
            coEvery { mockNotificationRepository.deleteNotificationsOlderThan(any()) } returns 3

            settingsViewModel.updateRetentionPeriod(newPeriod)

            coVerify {
                mockPreferenceManager.saveInt(Constants.PREF_KEY_RETENTION_PERIOD, newPeriod)
            }
            coVerify { mockNotificationRepository.deleteNotificationsOlderThan(any()) }
            verify {
                mockWorkManager.enqueueUniquePeriodicWork(
                    eq(CleanupWorker.WORK_NAME),
                    eq(ExistingPeriodicWorkPolicy.REPLACE),
                    any<PeriodicWorkRequest>())
            }
        }

    @Test
    fun `updateRetentionPeriod to unlimited - cancels worker`() = runTest {
        createViewModel()
        val unlimitedPeriod = Constants.RetentionPeriod.UNLIMITED

        settingsViewModel.updateRetentionPeriod(unlimitedPeriod)

        coVerify {
            mockPreferenceManager.saveInt(Constants.PREF_KEY_RETENTION_PERIOD, unlimitedPeriod)
        }
        verify { mockWorkManager.cancelUniqueWork(CleanupWorker.WORK_NAME) }
        coVerify(exactly = 0) { mockNotificationRepository.deleteNotificationsOlderThan(any()) }
    }

    @Test
    fun `retentionPeriod returns saved value from preferences`() = runTest {
        val savedPeriod = 14
        every {
            mockPreferenceManager.getIntFlow(
                Constants.PREF_KEY_RETENTION_PERIOD, Constants.RetentionPeriod.UNLIMITED)
        } returns flowOf(savedPeriod)

        createViewModel()

        // Collect the flow to get the latest value
        val job = launch { settingsViewModel.retentionPeriod.collect { /* no-op */ } }

        assertEquals(savedPeriod, settingsViewModel.retentionPeriod.value)
        job.cancel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }
}
