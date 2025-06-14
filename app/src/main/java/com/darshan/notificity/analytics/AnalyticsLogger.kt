package com.darshan.notificity.analytics

import com.darshan.notificity.enums.NotificationPermissionStatus

/**
 * Wrapper around [AnalyticsService] to log analytics events.
 * Simplifies event logging by providing easy-to-use functions for typical app actions.
 */
object AnalyticsLogger {

    /** Logs app launch event with the source of launch. */
    fun onAppLaunch(source: String) {
        AnalyticsService.logEvent(object : AnalyticsEvent {
            override val name = AnalyticsConstants.Events.APP_LAUNCH
            override val properties = mapOf(
                AnalyticsConstants.Params.SOURCE to source
            )
        })
    }

    /** Logs screen view event with screen name and class. */
    fun onScreenViewed(screenName: String, screenClass: String) {
        AnalyticsService.logScreen(
            screenName = screenName,
            properties = mapOf(
                AnalyticsConstants.Params.SCREEN_CLASS to screenClass
            )
        )
    }

    /**
     * Logs the event when the app requests notification permission from the user.
     */
    fun onNotificationPermissionRequested() {
        AnalyticsService.logEvent(object : AnalyticsEvent {
            override val name = AnalyticsConstants.Events.NOTIFICATION_PERMISSION_REQUESTED
            override val properties = null
        })
    }

    /**
     * Logs a change in the user's notification permission status (granted or denied).
     */
    fun onNotificationPermissionChanged(status: NotificationPermissionStatus) {
        AnalyticsService.logEvent(object : AnalyticsEvent {
            override val name = AnalyticsConstants.Events.NOTIFICATION_PERMISSION_CHANGED
            override val properties = mapOf(
                AnalyticsConstants.Params.PERMISSION_STATUS to status.code
            )
        })
    }

    /**
     * Sets the user's current notification permission status as a user property.
     * Enables filtering or segmentation based on permission status in analytics.
     */
    fun setNotificationPermissionProperty(status: NotificationPermissionStatus) {
        AnalyticsService.setUserProperty(
            AnalyticsConstants.Params.PERMISSION_STATUS,
            status.code.toString()
        )
    }

    /** Logs notification list viewed event with app name and total notifications. */
    fun onNotificationListOpened(appName: String, total: Int) {
        AnalyticsService.logEvent(object : AnalyticsEvent {
            override val name = AnalyticsConstants.Events.NOTIFICATION_LIST_VIEWED
            override val properties = mapOf(
                AnalyticsConstants.Params.APP_NAME to appName,
                AnalyticsConstants.Params.TOTAL_NOTIFICATIONS to total
            )
        })
    }

    /** Logs theme toggle click event with the selected theme. */
    fun onThemeToggleClicked(theme: String) {
        AnalyticsService.logEvent(object : AnalyticsEvent {
            override val name = AnalyticsConstants.Events.THEME_TOGGLE
            override val properties = mapOf(
                AnalyticsConstants.Params.THEME to theme
            )
        })
    }

    /** Logs recommend app clicked event. */
    fun onRecommendAppClicked() {
        AnalyticsService.logEvent(object : AnalyticsEvent {
            override val name = AnalyticsConstants.Events.RECOMMEND_APP_CLICKED
            override val properties = null
        })
    }

    /** Logs privacy policy clicked event. */
    fun onPrivacyPolicyClicked() {
        AnalyticsService.logEvent(object : AnalyticsEvent {
            override val name = AnalyticsConstants.Events.PRIVACY_POLICY_CLICKED
            override val properties = null
        })
    }

    /** Logs contributor profile clicked event with contributor name. */
    fun onContributorProfileClicked(name: String) {
        AnalyticsService.logEvent(object : AnalyticsEvent {
            override val name = AnalyticsConstants.Events.CONTRIBUTOR_CLICKED
            override val properties = mapOf(
                AnalyticsConstants.Params.CONTRIBUTOR_NAME to name
            )
        })
    }

    /** Logs buy me coffee clicked event. */
    fun onBuyMeCoffeeClicked() {
        AnalyticsService.logEvent(object : AnalyticsEvent {
            override val name = AnalyticsConstants.Events.BUY_ME_COFFEE_CLICKED
            override val properties = null
        })
    }

    /**
     * Logs user login event.
     * Also sets user ID and user type property on the analytics service.
     */
    fun onLogin(type: String, userId: String) {
        AnalyticsService.setUserId(userId)
        AnalyticsService.setUserProperty(AnalyticsConstants.Params.USER_TYPE, type)
        AnalyticsService.logEvent(object : AnalyticsEvent {
            override val name = AnalyticsConstants.Events.LOGIN
            override val properties = mapOf(
                AnalyticsConstants.Params.USER_TYPE to type,
                AnalyticsConstants.Params.USER_ID to userId
            )
        })
    }
}