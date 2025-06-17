package com.darshan.notificity

import com.darshan.notificity.main.data.NotificationRepository
import com.darshan.notificity.main.data.NotificationRepositoryImpl
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class NotificationRepositoryTest {

    private lateinit var mockNotificationDao: NotificationDao
    private lateinit var notificationRepository: NotificationRepository

    @Before
    fun setUp() {
        mockNotificationDao = mockk(relaxed = true)
        notificationRepository = NotificationRepositoryImpl(mockNotificationDao)
    }

    @Test
    fun `insertNotification calls dao insertNotification`() = runTest {
        val notificationEntity =
            NotificationEntity(
                id = 1,
                notificationId = 101,
                packageName = "com.example.app",
                timestamp = System.currentTimeMillis(),
                appName = "Example App",
                title = "Test Title",
                content = "Test Content",
                imageUrl = null,
                extras = null)

        notificationRepository.insertNotification(notificationEntity)

        coVerify { mockNotificationDao.insertNotification(notificationEntity) }
    }

    @Test
    fun `getAllNotificationsFlow calls dao getAllNotificationsFlow`() = runTest {
        notificationRepository.getAllNotificationsFlow()
        verify { mockNotificationDao.getAllNotificationsFlow() }
    }

    @Test
    fun `deleteNotification calls dao deleteNotification`() = runTest {
        val notificationEntity =
            NotificationEntity(
                id = 1,
                notificationId = 101,
                packageName = "com.example.app",
                timestamp = System.currentTimeMillis(),
                appName = "Example App",
                title = "Test Title",
                content = "Test Content",
                imageUrl = null,
                extras = null)

        notificationRepository.deleteNotification(notificationEntity)

        coVerify { mockNotificationDao.deleteNotification(notificationEntity) }
    }

    @Test
    fun `deleteNotificationsOlderThan calls dao deleteNotificationsOlderThan`() = runTest {
        val cutoffTimestamp = System.currentTimeMillis() - 86400000L // 1 day ago
        coEvery { mockNotificationDao.deleteNotificationsOlderThan(cutoffTimestamp) } returns 5

        val result = notificationRepository.deleteNotificationsOlderThan(cutoffTimestamp)

        coVerify { mockNotificationDao.deleteNotificationsOlderThan(cutoffTimestamp) }
        assertEquals(5, result)
    }
}
