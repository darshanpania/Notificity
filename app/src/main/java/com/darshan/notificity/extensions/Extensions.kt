package com.darshan.notificity.extensions

import android.content.Context
import android.content.Intent

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