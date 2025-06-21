package com.darshan.notificity.ui

import androidx.activity.ComponentActivity
import com.darshan.notificity.analytics.core.AnalyticsManager
import com.darshan.notificity.analytics.enums.Screen
import javax.inject.Inject

abstract class BaseActivity : ComponentActivity() {

    @Inject
    lateinit var analyticsManager: AnalyticsManager

    abstract val screen: Screen

    companion object {
        private var lastScreenName: String? = null
    }

    override fun onResume() {
        super.onResume()

        if (lastScreenName != screen.screenName) {
            analyticsManager.app.onScreenViewed(screen)
            lastScreenName = screen.screenName
        }
    }
}
