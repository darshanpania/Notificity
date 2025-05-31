package com.darshan.notificity.ui.theme

/**
 * Enum representing the app's theme modes.
 *
 * @property value String identifier for the theme mode,
 *                   useful for saving/loading from preferences.
 */
enum class ThemeMode(val value: String) {
    LIGHT("light"),
    DARK("dark"),
    SYSTEM("system");

    companion object {
        fun fromValue(value: String): ThemeMode {
            return entries.firstOrNull { it.value == value } ?: SYSTEM
        }
    }
}
