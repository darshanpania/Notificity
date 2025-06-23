package com.darshan.notificity.model

import androidx.annotation.Keep
import androidx.compose.ui.graphics.ImageBitmap

@Keep
data class AppInfo(
    val packageName: String,
    val appName: String,
    val icon: ImageBitmap?,
    val notificationCount: Int
)
