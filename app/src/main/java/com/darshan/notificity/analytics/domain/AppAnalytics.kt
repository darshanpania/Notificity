package com.darshan.notificity.analytics.domain

import com.darshan.notificity.analytics.enums.Screen

/**
 * Interface for tracking general application analytics events.
 * Handles screen views, user interactions, and error reporting.
 */
interface AppAnalytics {
    /**
     * Tracks when the application is launched.
     *
     * @param source The source of the app launch (default: SOURCE constant)
     */
    fun onAppLaunch(source: String = SOURCE)

    /**
     * Tracks when a screen is viewed by the user.
     *
     * @param screenName The screen that was viewed
     */
    fun onScreenViewed(screenName: Screen)

    /**
     * Tracks when the notification list is opened.
     *
     * @param appName The name of the app whose notifications are being viewed
     * @param total The total number of notifications displayed
     */
    fun onNotificationListOpened(appName: String, total: Int)

    /**
     * Tracks when the user toggles the app theme.
     *
     * @param theme The theme that was selected ("DARK", "LIGHT" or "SYSTEM)
     */
    fun onThemeToggleClicked(theme: String)

    /**
     * Tracks when the user clicks the recommend app button.
     */
    fun onRecommendAppClicked()

    /**
     * Tracks when the user clicks the privacy policy link.
     */
    fun onPrivacyPolicyClicked()

    /**
     * Tracks when a contributor profile is clicked.
     *
     * @param name The name of the contributor whose profile was clicked
     */
    fun onContributorProfileClicked(name: String)

    /**
     * Tracks when the "Buy Me Coffee" button is clicked.
     */
    fun onBuyMeCoffeeClicked()

    /**
     * Reports when an error occurs in the application.
     *
     * @param screen The screen where the error occurred
     * @param error Description of the error that occurred
     */
    fun onErrorOccurred(screen: Screen, error: String)

    companion object {
        // Event name constants
        const val NOTIFICATION_LIST_VIEWED = "notification_list_viewed"
        const val THEME_TOGGLE = "theme_toggle"
        const val RECOMMEND_APP_CLICKED = "recommend_app_clicked"
        const val PRIVACY_POLICY_CLICKED = "privacy_policy_clicked"
        const val CONTRIBUTOR_CLICKED = "contributor_clicked"
        const val BUY_ME_COFFEE_CLICKED = "buy_me_coffee_clicked"
        const val ERROR_OCCURRED = "error_occurred"

        // Parameter key constants
        const val SOURCE = "source"
        const val ERROR = "error"
        const val APP_NAME = "app_name"
        const val TOTAL_NOTIFICATIONS = "total_notifications"
        const val THEME = "theme"
        const val CONTRIBUTOR_NAME = "contributor_name"
    }
}