package com.darshan.notificity.utils

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.core.app.NotificationCompat
import com.bumptech.glide.Glide
import com.darshan.notificity.MainActivity
import com.darshan.notificity.R

object NotificationUtil {

    /**
     * Loads a bitmap image from the provided URL within the specified timeout.
     *
     * @param context The context used to load the image.
     * @param imageUrl The URL of the image to load.
     * @param timeout The maximum time in seconds to wait for the image to load.
     * @return The loaded Bitmap if successful within the timeout; null otherwise.
     */
    fun getLargeIcon(context: Context?, imageUrl: String?, timeout: Long): Bitmap? {
        if (context == null || imageUrl.isNullOrBlank()) return null

        return try {
            Glide.with(context.applicationContext)
                .asBitmap()
                .load(imageUrl)
                .submit()
                .get(timeout, java.util.concurrent.TimeUnit.SECONDS)
        } catch (_: Exception) {
            null
        }
    }

    fun showNotification(
        context: Context,
        title: String,
        message: String,
        channelId: String,
        largeIcon: Bitmap? = null,
        notificationId: Int
    ) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = createNotification(context, title, message, channelId, largeIcon)
        notificationManager.notify(notificationId, notification)
    }

    private fun createNotification(
        context: Context, title: String, message: String, channelId: String, largeIcon: Bitmap?
    ): Notification {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.app_icon)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .apply {
                if (largeIcon != null) {
                    setLargeIcon(largeIcon)
                    setStyle(
                        NotificationCompat.BigPictureStyle()
                            .bigPicture(largeIcon)
                            .bigLargeIcon(null as Bitmap?)
                    )
                } else {
                    setStyle(NotificationCompat.BigTextStyle().bigText(message))
                }
            }
            .build()
    }
}
