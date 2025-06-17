package com.darshan.notificity

object Constants {
    const val DB_NAME = "notification-db"
    const val DEFAULT_CHANNEL_ID = "default_channel_id"
    const val DEFAULT_CHANNEL_NAME = "Default Notifications"
    const val DEFAULT_CHANNEL_DESCRIPTION = "Default notification channel"
    const val BUY_ME_A_COFFEE_LINK = "https://buymeacoffee.com/darshanpania"

    object RetentionPeriod {
        const val DAYS_7 = 7
        const val DAYS_30 = 30
        const val UNLIMITED = -1 // Using -1 to represent unlimited

        val ALL_OPTIONS = listOf(DAYS_7, DAYS_30, UNLIMITED)

        fun getLabel(context: android.content.Context, period: Int): String {
            return when (period) {
                DAYS_7 -> context.getString(R.string.setting_retention_7_days)
                DAYS_30 -> context.getString(R.string.setting_retention_30_days)
                UNLIMITED -> context.getString(R.string.setting_retention_unlimited)
                else ->
                    context.getString(R.string.setting_retention_unlimited) // Default or error case
            }
        }
    }

    // SharedPreferences Keys
    const val PREFS_NAME = "notificity_prefs"
    const val PREF_KEY_THEME = "pref_key_theme"
    const val PREF_KEY_RETENTION_PERIOD = "pref_key_retention_period"
}
