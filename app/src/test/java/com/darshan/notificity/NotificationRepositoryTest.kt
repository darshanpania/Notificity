package com.darshan.notificity

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class NotificationRepositoryTest {

    @Mock
    private lateinit var mockNotificationDao: NotificationDao

    private lateinit var notificationRepository: NotificationRepository

    @Before
    fun setUp() {
        notificationRepository = NotificationRepository(mockNotificationDao)
    }

    @Test
    fun `insertNotification calls dao insertNotification`() = runTest {
        val notificationEntity = NotificationEntity(
            id = 1, notificationId = 101, packageName = "com.example.app",
            timestamp = System.currentTimeMillis(), appName = "Example App",
            title = "Test Notification", content = "This is a test.",
            imageUrl = null, extras = null
        )
        notificationRepository.insertNotification(notificationEntity)
        verify(mockNotificationDao).insertNotification(notificationEntity)
    }

    @Test
    fun `getAllNotificationsFlow calls dao getAllNotificationsFlow`() {
        notificationRepository.getAllNotificationsFlow()
        verify(mockNotificationDao).getAllNotificationsFlow()
    }

    @Test
    fun `deleteNotification calls dao deleteNotification`() = runTest {
        val notificationEntity = NotificationEntity(
            id = 1, notificationId = 101, packageName = "com.example.app",
            timestamp = System.currentTimeMillis(), appName = "Example App",
            title = "Test Notification", content = "This is a test.",
            imageUrl = null, extras = null
        )
        notificationRepository.deleteNotification(notificationEntity)
        verify(mockNotificationDao).deleteNotification(notificationEntity)
    }

    @Test
    fun `deleteNotificationsOlderThan calls dao deleteNotificationsOlderThan`() = runTest {
        val cutoffTimestamp = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000) // 7 days ago
        notificationRepository.deleteNotificationsOlderThan(cutoffTimestamp)
        verify(mockNotificationDao).deleteNotificationsOlderThan(cutoffTimestamp)
    }
}
