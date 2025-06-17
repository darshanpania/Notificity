package com.darshan.notificity.ui.theme

import com.darshan.notificity.utils.PreferenceManager
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Manager responsible for saving and retrieving the app's theme preference.
 *
 * This object acts as a wrapper over the generic [PreferenceManager]
 */
@Singleton
open class ThemePreferenceManager
@Inject
constructor(private val preferenceManager: PreferenceManager) {
    companion object {
        private const val THEME_KEY = "app_theme"
    }

    open suspend fun saveTheme(theme: ThemeMode) {
        preferenceManager.saveString(THEME_KEY, theme.value)
    }

    open fun getThemeFlow(): Flow<ThemeMode> {
        return preferenceManager.getStringFlow(THEME_KEY, ThemeMode.SYSTEM.value).map {
            ThemeMode.Companion.fromValue(it)
        }
    }
}
