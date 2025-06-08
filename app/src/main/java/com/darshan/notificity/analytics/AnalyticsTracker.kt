package com.darshan.notificity.analytics

/**
 * Interface for tracking analytics events and user properties.
 */
interface AnalyticsTracker {

    /**
     * Logs a custom analytics event.
     *
     * @param event The event to log.
     */
    fun logEvent(event: AnalyticsEvent)

    /**
     * Logs a screen view with optional properties.
     *
     * @param screenName Name of the screen.
     * @param properties Optional additional properties.
     */
    fun logScreen(screenName: String, properties: Map<String, Any>? = null)

    /**
     * Sets the user ID for tracking.
     *
     * @param userId Unique identifier for the user.
     */
    fun setUserId(userId: String?)

    /**
     * Sets a custom user property.
     *
     * @param key Property name.
     * @param value Property value.
     */
    fun setUserProperty(key: String, value: String)
}
