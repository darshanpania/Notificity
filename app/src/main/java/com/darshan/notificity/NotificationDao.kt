package com.darshan.notificity

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Query("SELECT * FROM notification ORDER BY timestamp DESC")
    fun getAllNotificationsFlow(): Flow<List<NotificationEntity>>

    @Query("DELETE FROM notification WHERE timestamp < :cutoffTimestamp")
    suspend fun deleteNotificationsOlderThan(cutoffTimestamp: Long): Int

    @Delete suspend fun deleteNotification(notification: NotificationEntity)
}
