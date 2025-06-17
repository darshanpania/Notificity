package com.darshan.notificity.analytics

/** Defines constants for analytics events, parameters, and screen names. */
object AnalyticsConstants {

    // Event names used for logging analytics.
    object Events {
        const val APP_LAUNCH = "app_launched"
        const val NOTIFICATION_LIST_VIEWED = "notification_list_viewed"
        const val THEME_TOGGLE = "theme_toggle"
        const val RECOMMEND_APP_CLICKED = "recommend_app_clicked"
        const val PRIVACY_POLICY_CLICKED = "privacy_policy_clicked"
        const val CONTRIBUTOR_CLICKED = "contributor_clicked"
        const val BUY_ME_COFFEE_CLICKED = "buy_me_coffee_clicked"
        const val LOGIN = "login"
        const val NOTIFICATION_PERMISSION_REQUESTED = "notification_permission_requested"
        const val NOTIFICATION_PERMISSION_CHANGED = "notification_permission_changed"
    }

    // Parameter keys used with analytics events.
    object Params {
        const val SOURCE = "source"
        const val SCREEN_CLASS = "screen_class"
        const val APP_NAME = "app_name"
        const val TOTAL_NOTIFICATIONS = "total_notifications"
        const val THEME = "theme"
        const val CONTRIBUTOR_NAME = "contributor_name"
        const val USER_TYPE = "user_type"
        const val USER_ID = "user_id"
        const val PERMISSION_STATUS = "permission_status" // e.g. granted, denied
    }

    // Names of screens for screen view tracking.
    object Screens {
        const val MAIN = "Main"
        const val SETTINGS = "Settings"
        const val NOTIFICATION_LIST = "Notification List"
        const val ABOUT = "About"
    }
}
