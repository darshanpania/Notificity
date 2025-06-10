package com.darshan.notificity.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notification")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val notificationId: Int, // sbn.id
    val packageName: String,
    val timestamp: Long,
    val appName: String,
    val title: String,
    val content: String,
    val imageUrl: String?, // Store image as bitmap
    val extras: String?
)