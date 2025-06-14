package com.darshan.notificity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "notification")
@Serializable
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val notificationId: Int, // sbn.id
    val packageName: String,
    val timestamp: Long,
    val appName: String,
    val title: String,
    val content: String,
    val imageUrl: String? = null, // Store image as bitmap
    val extras: String? = null
)