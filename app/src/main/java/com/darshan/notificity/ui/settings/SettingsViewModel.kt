package com.darshan.notificity.ui.settings

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.darshan.notificity.ui.theme.ThemeMode
import com.darshan.notificity.ui.theme.ThemePreferenceManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(private val application: Application) : AndroidViewModel(application) {

    private val _themeMode = MutableStateFlow(ThemeMode.SYSTEM)
    val themeMode = _themeMode.asStateFlow()

    init {
        viewModelScope.launch {
            ThemePreferenceManager.getThemeFlow(application.applicationContext).collect {
                _themeMode.value = it
            }
        }
    }

    fun updateTheme(theme: ThemeMode) {
        viewModelScope.launch {
            ThemePreferenceManager.saveTheme(application.applicationContext, theme)
            _themeMode.value = theme
        }
    }


    fun openLink(context: Context, link: String) {
        val intent = Intent(Intent.ACTION_VIEW, link.toUri())
        context.startActivity(intent)
    }
}