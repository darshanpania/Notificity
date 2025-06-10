package com.darshan.notificity.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darshan.notificity.analytics.AnalyticsLogger
import com.darshan.notificity.ui.theme.ThemeMode
import com.darshan.notificity.ui.theme.ThemePreferenceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel
@Inject
constructor(
    @ApplicationContext private val context: Context,
    private val themePreferenceManager: ThemePreferenceManager
) : ViewModel() {

    private val _themeMode = MutableStateFlow(ThemeMode.SYSTEM)
    val themeMode = _themeMode.asStateFlow()

    init {
        viewModelScope.launch {
            themePreferenceManager.getThemeFlow(context).collect {
                _themeMode.value = it
            }
        }
    }

    fun updateTheme(theme: ThemeMode) {
        viewModelScope.launch {
            themePreferenceManager.saveTheme(context, theme)
            _themeMode.value = theme

            AnalyticsLogger.onThemeToggleClicked(theme.name)
        }
    }
}