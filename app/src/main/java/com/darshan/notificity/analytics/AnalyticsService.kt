package com.darshan.notificity.analytics

/**
 * Wrapper for [AnalyticsTracker] implementation.
 * Provides static access to log events, screens,
 * and set user properties through a configured tracker.
 */
object AnalyticsService {
    private lateinit var tracker: AnalyticsTracker

    fun init(tracker: AnalyticsTracker) {
        this.tracker = tracker
    }

    fun logEvent(event: AnalyticsEvent) = tracker.logEvent(event)

    fun logScreen(screenName: String, properties: Map<String, Any>? = null) =
        tracker.logScreen(screenName, properties)

    fun setUserId(userId: String?) = tracker.setUserId(userId)

    fun setUserProperty(key: String, value: String) = tracker.setUserProperty(key, value)
}