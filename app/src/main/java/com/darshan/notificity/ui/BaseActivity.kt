package com.darshan.notificity.ui

import androidx.activity.ComponentActivity
import com.darshan.notificity.analytics.AnalyticsLogger

abstract class BaseActivity : ComponentActivity() {

    abstract val screenName: String

    companion object {
        private var lastScreenName: String? = null
    }

    override fun onResume() {
        super.onResume()

        if (lastScreenName != screenName) {
            AnalyticsLogger.onScreenViewed(screenName, this::class.java.simpleName)
            lastScreenName = screenName
        }
    }
}
