package com.darshan.notificity.ui.theme

import android.content.Context
import com.darshan.notificity.utils.PreferenceManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Manager responsible for saving and retrieving the app's theme preference.
 *
 * This object acts as a wrapper over the generic [PreferenceManager]
 */
object ThemePreferenceManager {
    private const val THEME_KEY = "app_theme"

    suspend fun saveTheme(context: Context, theme: ThemeMode) {
        PreferenceManager.saveString(context, THEME_KEY, theme.value)
    }

    fun getThemeFlow(context: Context): Flow<ThemeMode> {
        return PreferenceManager.getStringFlow(context, THEME_KEY, ThemeMode.SYSTEM.value)
            .map { ThemeMode.Companion.fromValue(it) }
    }
}