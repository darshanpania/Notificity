package com.darshan.notificity.utils

import android.util.Log
import com.darshan.notificity.BuildConfig

object Logger {
    private const val DEFAULT_TAG = "NotificityLog"
    private val ENABLE = BuildConfig.DEBUG

    fun d(tag: String = DEFAULT_TAG, message: String) {
        if (ENABLE) Log.d(tag, message)
    }

    fun i(tag: String = DEFAULT_TAG, message: String) {
        if (ENABLE) Log.i(tag, message)
    }

    fun w(tag: String = DEFAULT_TAG, message: String) {
        if (ENABLE) Log.w(tag, message)
    }

    fun e(tag: String = DEFAULT_TAG, message: String, throwable: Throwable? = null) {
        if (ENABLE) {
            if (throwable != null) {
                Log.e(tag, message, throwable)
            } else {
                Log.e(tag, message)
            }
        }
    }
}
