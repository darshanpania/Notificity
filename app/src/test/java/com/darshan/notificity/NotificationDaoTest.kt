package com.darshan.notificity

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.IOException

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Config.OLDEST_SDK]) // Configure for a specific SDK if needed
class NotificationDaoTest {

    private lateinit var notificationDao: NotificationDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java)
            .allowMainThreadQueries() // Allowing main thread queries for simplicity in tests
            .build()
        notificationDao = db.notificationDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetNotification() = runBlocking {
        val notification = NotificationEntity(notificationId = 1, packageName = "com.test", timestamp = System.currentTimeMillis(), appName = "Test App", title = "Test Title", content = "Test Content", imageUrl = null, extras = null)
        notificationDao.insertNotification(notification)
        val allNotifications = notificationDao.getAllNotificationsFlow().first()
        assertEquals(allNotifications[0].packageName, notification.packageName)
    }

    @Test
    @Throws(Exception::class)
    fun deleteNotificationsOlderThan() = runBlocking {
        val currentTime = System.currentTimeMillis()
        val oldNotification = NotificationEntity(id = 1, notificationId = 1, packageName = "com.old", timestamp = currentTime - (2 * 24 * 60 * 60 * 1000), appName = "Old App", title = "Old Title", content = "Old Content", null, null) // 2 days old
        val newNotification = NotificationEntity(id = 2, notificationId = 2, packageName = "com.new", timestamp = currentTime, appName = "New App", title = "New Title", content = "New Content", null, null) // Today

        notificationDao.insertNotification(oldNotification)
        notificationDao.insertNotification(newNotification)

        var allNotifications = notificationDao.getAllNotificationsFlow().first()
        assertEquals(2, allNotifications.size)

        // Delete notifications older than 1 day
        val cutoffTimestamp = currentTime - (1 * 24 * 60 * 60 * 1000)
        notificationDao.deleteNotificationsOlderThan(cutoffTimestamp)

        allNotifications = notificationDao.getAllNotificationsFlow().first()
        assertEquals(1, allNotifications.size)
        assertEquals("com.new", allNotifications[0].packageName)
    }

    @Test
    @Throws(Exception::class)
    fun deleteNotificationsOlderThan_noNotificationsDeleted() = runBlocking {
        val currentTime = System.currentTimeMillis()
        val notification1 = NotificationEntity(id = 1, notificationId = 1, packageName = "com.test1", timestamp = currentTime - (1 * 24 * 60 * 60 * 1000), appName = "App1", title = "T1", content = "C1", null, null) // 1 day old
        val notification2 = NotificationEntity(id = 2, notificationId = 2, packageName = "com.test2", timestamp = currentTime, appName = "App2", title = "T2", content = "C2", null, null) // Today

        notificationDao.insertNotification(notification1)
        notificationDao.insertNotification(notification2)

        // Cutoff is 2 days ago, so no notifications should be deleted
        val cutoffTimestamp = currentTime - (2 * 24 * 60 * 60 * 1000)
        notificationDao.deleteNotificationsOlderThan(cutoffTimestamp)

        val allNotifications = notificationDao.getAllNotificationsFlow().first()
        assertEquals(2, allNotifications.size)
    }

     @Test
    @Throws(Exception::class)
    fun deleteNotificationsOlderThan_allNotificationsDeleted() = runBlocking {
        val currentTime = System.currentTimeMillis()
        val notification1 = NotificationEntity(id = 1, notificationId = 1, packageName = "com.test1", timestamp = currentTime - (2 * 24 * 60 * 60 * 1000), appName = "App1", title = "T1", content = "C1", null, null) // 2 days old
        val notification2 = NotificationEntity(id = 2, notificationId = 2, packageName = "com.test2", timestamp = currentTime - (3 * 24 * 60 * 60 * 1000), appName = "App2", title = "T2", content = "C2", null, null) // 3 days old

        notificationDao.insertNotification(notification1)
        notificationDao.insertNotification(notification2)

        // Cutoff is 1 day ago, so all notifications should be deleted
        val cutoffTimestamp = currentTime - (1 * 24 * 60 * 60 * 1000)
        notificationDao.deleteNotificationsOlderThan(cutoffTimestamp)

        val allNotifications = notificationDao.getAllNotificationsFlow().first()
        assertTrue(allNotifications.isEmpty())
    }
}
