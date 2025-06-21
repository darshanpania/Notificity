package com.darshan.notificity.analytics.enums

import androidx.activity.ComponentActivity
import com.darshan.notificity.AboutActivity
import com.darshan.notificity.NotificationsActivity
import com.darshan.notificity.main.ui.MainActivity
import com.darshan.notificity.ui.settings.SettingsActivity
import com.darshan.notificity.ui.signin.SignInActivity
import kotlin.reflect.KClass

/**
 * Enumeration of application screens for consistent screen view tracking.
 * Each screen is associated with its display name and corresponding Activity class.
 *
 * @param screenName The display name of the screen for analytics
 * @param clazz The Activity class associated with this screen
 */
enum class Screen(val screenName: String, val clazz: KClass<out ComponentActivity>) {
    SIGNIN("Signin", SignInActivity::class),
    MAIN("Main", MainActivity::class),
    NOTIFICATION_LIST("Notification List", NotificationsActivity::class),
    SETTINGS("Settings", SettingsActivity::class),
    ABOUT("About", AboutActivity::class);

    /**
     * Gets the simple class name of the associated screen(i.e. Activity).
     * @return The simple class name as a string
     */
    val screenClass: String
        get() = clazz.java.simpleName
}