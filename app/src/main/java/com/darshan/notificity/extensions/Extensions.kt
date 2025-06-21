package com.darshan.notificity.extensions

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.darshan.notificity.auth.AuthType
import com.darshan.notificity.enums.NotificationPermissionStatus
import kotlin.collections.component1
import kotlin.collections.component2

/**
 * Launches the app settings screen
 */
fun Context.openAppSettings(appSettingsLauncher: ActivityResultLauncher<Intent>) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", packageName, null)
    }
    appSettingsLauncher.launch(intent)
}

fun Context.recommendApp() {
    val appName = "Notificity"
    val appPackageName = packageName
    val playStoreUrl = "https://play.google.com/store/apps/details?id=$appPackageName"
    val shareText = """
        Try $appName – the smart notification tracker!
        $playStoreUrl
    """.trimIndent()
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, appName)
        putExtra(Intent.EXTRA_TEXT, shareText)
    }

    val chooser = Intent.createChooser(intent, "Share $appName via")
    if (intent.resolveActivity(packageManager) != null) {
        startActivity(chooser)
    }
}

fun Context.getActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.getActivity()
    else -> null
}


inline fun <reified T : Activity> Context.launchActivity(
    noinline extras: (Intent.() -> Unit)? = null
) {
    val intent = Intent(this, T::class.java)
    extras?.let { intent.it() }
    startActivity(intent)
}

fun Context.openUrl(url: String) {
    val intent = Intent(Intent.ACTION_VIEW, url.toUri())
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(intent)
}

fun Intent?.isLaunchedFromLauncher(): Boolean {
    return this?.action == Intent.ACTION_MAIN && this.hasCategory(Intent.CATEGORY_LAUNCHER)
}

fun Context.getNotificationPermissionStatus(): NotificationPermissionStatus {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val granted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
        if (granted) NotificationPermissionStatus.GRANTED else NotificationPermissionStatus.DENIED
    } else {
        // For below Android 13, check if notifications are enabled at system level
        val notificationsEnabled = NotificationManagerCompat.from(this).areNotificationsEnabled()
        if (notificationsEnabled) NotificationPermissionStatus.GRANTED else NotificationPermissionStatus.DENIED
    }
}

fun String.toTitleCase(): String =
    lowercase().split(" ").joinToString(" ") { it.replaceFirstChar { char -> char.uppercaseChar() } }

inline fun <reified T> Map<AuthType, Any>.getProviderOrError(
    type: AuthType,
    errorMessage: String = "$type provider is not available or of incorrect type"
): T {
    return this[type] as? T ?: throw IllegalStateException(errorMessage)
}

fun Map<String, Any>.toBundle(): Bundle {
    return Bundle().apply {
        forEach { (key, value) ->
            when (value) {
                is String -> putString(key, value)
                is Int -> putInt(key, value)
                is Long -> putLong(key, value)
                is Double -> putDouble(key, value)
                is Boolean -> putBoolean(key, value)
                else -> putString(key, value.toString())
            }
        }
    }
}