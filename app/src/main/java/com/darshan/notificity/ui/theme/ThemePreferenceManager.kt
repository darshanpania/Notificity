package com.darshan.notificity.ui.theme

import android.content.Context
import com.darshan.notificity.utils.PreferenceManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manager responsible for saving and retrieving the app's theme preference.
 *
 * This object acts as a wrapper over the generic [PreferenceManager]
 */
@Singleton
class ThemePreferenceManager
@Inject
constructor(
    private val preferenceManager: PreferenceManager
) {
    companion object {
        private const val THEME_KEY = "app_theme"
    }

    suspend fun saveTheme(context: Context, theme: ThemeMode) {
        preferenceManager.saveString(context, THEME_KEY, theme.value)
    }

    fun getThemeFlow(context: Context): Flow<ThemeMode> {
        return preferenceManager.getStringFlow(context, THEME_KEY, ThemeMode.SYSTEM.value)
            .map { ThemeMode.Companion.fromValue(it) }
    }
}