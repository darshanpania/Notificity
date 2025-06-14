package com.darshan.notificity.ui.settings

import android.app.Application
import android.content.Context
import android.os.Environment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.darshan.notificity.NotificationRepository
import com.darshan.notificity.analytics.AnalyticsLogger
import com.darshan.notificity.ui.theme.ThemeMode
import com.darshan.notificity.ui.theme.ThemePreferenceManager
import com.darshan.notificity.utils.toCsvString
import com.darshan.notificity.utils.toJsonString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

enum class ExportFormat {
    CSV,
    JSON
}

sealed interface ExportState {
    object Idle : ExportState
    object Loading : ExportState
    data class Success(val filePath: String) : ExportState
    data class Error(val message: String) : ExportState
}

class SettingsViewModel(private val application: Application, private val repository: NotificationRepository) : AndroidViewModel(application) {

    private val _themeMode = MutableStateFlow(ThemeMode.SYSTEM)
    val themeMode = _themeMode.asStateFlow()

    private val _exportState = MutableStateFlow<ExportState>(ExportState.Idle)
    val exportState = _exportState.asStateFlow()

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

            AnalyticsLogger.onThemeToggleClicked(theme.name)
        }
    }

    fun exportData(format: ExportFormat, context: Context) {
        _exportState.value = ExportState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val notifications = repository.getAllNotifications()
                if (notifications.isEmpty()) {
                    _exportState.value = ExportState.Error("No notifications to export.")
                    return@launch
                }

                val content = when (format) {
                    ExportFormat.CSV -> notifications.toCsvString()
                    ExportFormat.JSON -> notifications.toJsonString()
                }

                val fileName = "notificity_export_${System.currentTimeMillis()}.${format.name.lowercase()}"
                val downloadsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                val file = File(downloadsDir, fileName)

                if (downloadsDir == null || (!downloadsDir.exists() && !downloadsDir.mkdirs())) {
                    _exportState.value = ExportState.Error("Could not access or create downloads directory.")
                    return@launch
                }

                file.writeText(content)

                _exportState.value = ExportState.Success(file.absolutePath)
                AnalyticsLogger.onExportInitiated(format.name)
            } catch (e: Exception) {
                _exportState.value = ExportState.Error("Failed to export data: ${e.localizedMessage}")
            }
        }
    }

    fun resetExportState() {
        _exportState.value = ExportState.Idle
    }
}