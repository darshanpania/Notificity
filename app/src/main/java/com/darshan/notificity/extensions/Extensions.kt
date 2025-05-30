package com.darshan.notificity.extensions

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import androidx.core.net.toUri

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

inline fun <reified T : Activity> Context.launchActivity() {
    val intent = Intent(this, T::class.java)
    startActivity(intent)
}

fun Context.openUrl(url: String) {
    val intent = Intent(Intent.ACTION_VIEW, url.toUri())
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(intent)
}

