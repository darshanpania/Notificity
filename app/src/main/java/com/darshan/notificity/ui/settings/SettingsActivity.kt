package com.darshan.notificity.ui.settings

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.work.WorkManager
import com.darshan.notificity.AboutActivity
import com.darshan.notificity.Constants
import com.darshan.notificity.NotificationEntity
import com.darshan.notificity.R
import com.darshan.notificity.analytics.AnalyticsConstants
import com.darshan.notificity.analytics.AnalyticsLogger
import com.darshan.notificity.components.NotificityAppBar
import com.darshan.notificity.extensions.launchActivity
import com.darshan.notificity.extensions.recommendApp
import com.darshan.notificity.main.data.NotificationRepository
import com.darshan.notificity.ui.BaseActivity
import com.darshan.notificity.ui.theme.NotificityTheme
import com.darshan.notificity.ui.theme.ThemeMode
import com.darshan.notificity.ui.theme.ThemePreferenceManager
import com.darshan.notificity.utils.PreferenceManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@AndroidEntryPoint
class SettingsActivity : BaseActivity() {

    private val settingsViewModel: SettingsViewModel by viewModels()

    override val screenName: String
        get() = AnalyticsConstants.Screens.SETTINGS

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val themeMode by settingsViewModel.themeMode.collectAsStateWithLifecycle()

            NotificityTheme(themeMode = themeMode) {
                SettingsScreen(settingsViewModel = settingsViewModel, onBack = { finish() })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(settingsViewModel: SettingsViewModel, onBack: () -> Unit) {
    val currentTheme by settingsViewModel.themeMode.collectAsStateWithLifecycle()
    val currentRetentionPeriod by settingsViewModel.retentionPeriod.collectAsStateWithLifecycle()
    val deletionResult by settingsViewModel.deletionResult.collectAsStateWithLifecycle()

    val themeSheetState = rememberModalBottomSheetState()
    val showThemeSheet = remember { mutableStateOf(false) }

    val retentionSheetState = rememberModalBottomSheetState()
    val showRetentionSheet = remember { mutableStateOf(false) }
    val context = LocalContext.current

    val showConfirmationDialog = remember { mutableStateOf(false) }
    val pendingRetentionPeriod = remember { mutableStateOf<Int?>(null) }

    // Handle deletion result toast
    LaunchedEffect(deletionResult) {
        deletionResult?.let { count ->
            val message =
                if (count == 0) {
                    context.getString(R.string.toast_no_notifications_deleted)
                } else {
                    context.getString(R.string.toast_notifications_deleted, count)
                }
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            settingsViewModel.clearDeletionResult()
        }
    }

    Scaffold(
        topBar = {
            NotificityAppBar(
                title = "Settings",
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                })
        }) { innerPadding ->
            Column(
                modifier =
                    Modifier.padding(innerPadding)
                        .padding(16.dp)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    SettingsCardWithLabel(
                        label = "Theme",
                        value = currentTheme.name.lowercase().replaceFirstChar { it.uppercase() },
                        onClick = { showThemeSheet.value = true })

                    SettingsCardWithLabel(
                        label = context.getString(R.string.setting_retention_period),
                        value = Constants.RetentionPeriod.getLabel(context, currentRetentionPeriod),
                        onClick = { showRetentionSheet.value = true })

                    SettingsCard(
                        icon = rememberVectorPainter(image = Icons.Outlined.Share),
                        text = "Recommend this app",
                        onClick = {
                            context.recommendApp()
                            AnalyticsLogger.onRecommendAppClicked()
                        })
                    SettingsCard(
                        icon = rememberVectorPainter(image = Icons.Outlined.Info),
                        text = "About",
                        onClick = { context.launchActivity<AboutActivity>() })
                }

            if (showThemeSheet.value) {
                ModalBottomSheet(
                    onDismissRequest = { showThemeSheet.value = false },
                    sheetState = themeSheetState,
                    containerColor = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)) {
                        Column(
                            Modifier.fillMaxWidth().padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                ThemeOptionItem(
                                    icon = painterResource(id = R.drawable.iv_settings),
                                    label = "System Default",
                                    selected = currentTheme == ThemeMode.SYSTEM,
                                    onClick = {
                                        if (currentTheme != ThemeMode.SYSTEM) {
                                            settingsViewModel.updateTheme(ThemeMode.SYSTEM)
                                        }
                                        showThemeSheet.value = false
                                    })
                                ThemeOptionItem(
                                    icon = painterResource(id = R.drawable.iv_light_theme),
                                    label = "Light Theme",
                                    selected = currentTheme == ThemeMode.LIGHT,
                                    onClick = {
                                        if (currentTheme != ThemeMode.LIGHT) {
                                            settingsViewModel.updateTheme(ThemeMode.LIGHT)
                                        }
                                        showThemeSheet.value = false
                                    })
                                ThemeOptionItem(
                                    icon = painterResource(id = R.drawable.iv_dark_theme),
                                    label = "Dark Theme",
                                    selected = currentTheme == ThemeMode.DARK,
                                    onClick = {
                                        if (currentTheme != ThemeMode.DARK) {
                                            settingsViewModel.updateTheme(ThemeMode.DARK)
                                        }
                                        showThemeSheet.value = false
                                    })
                            }
                    }
            }

            if (showRetentionSheet.value) {
                ModalBottomSheet(
                    onDismissRequest = { showRetentionSheet.value = false },
                    sheetState = retentionSheetState,
                    containerColor = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)) {
                        Column(
                            Modifier.fillMaxWidth().padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                Constants.RetentionPeriod.ALL_OPTIONS.forEach { period ->
                                    RetentionOptionItem(
                                        label = Constants.RetentionPeriod.getLabel(context, period),
                                        selected = currentRetentionPeriod == period,
                                        onClick = {
                                            if (currentRetentionPeriod != period) {
                                                if (period < currentRetentionPeriod ||
                                                    period == Constants.RetentionPeriod.DAYS_7 ||
                                                    period == Constants.RetentionPeriod.DAYS_30) {
                                                    pendingRetentionPeriod.value = period
                                                    showConfirmationDialog.value = true
                                                } else {
                                                    settingsViewModel.updateRetentionPeriod(period)
                                                }
                                            }
                                            showRetentionSheet.value = false
                                        })
                                }
                            }
                    }
            }

            if (showConfirmationDialog.value) {
                AlertDialog(
                    onDismissRequest = { showConfirmationDialog.value = false },
                    title = {
                        Text(
                            text =
                                context.getString(R.string.dialog_title_confirm_retention_change))
                    },
                    text = {
                        Text(
                            text =
                                context.getString(R.string.dialog_message_confirm_retention_change))
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                pendingRetentionPeriod.value?.let {
                                    settingsViewModel.updateRetentionPeriod(it)
                                }
                                showConfirmationDialog.value = false
                            }) {
                                Text(context.getString(R.string.dialog_button_confirm))
                            }
                    },
                    dismissButton = {
                        TextButton(onClick = { showConfirmationDialog.value = false }) {
                            Text(context.getString(R.string.dialog_button_cancel))
                        }
                    })
            }
        }
}

@Composable
fun SettingsCard(icon: Painter, text: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
    }
}

@Composable
fun SettingsCardWithLabel(label: String, value: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
    }
}

@Composable
fun ThemeOptionItem(icon: Painter, label: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier =
            Modifier.fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = icon,
                contentDescription = label,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onSurface)
            if (selected) {
                Icon(
                    Icons.Filled.Check,
                    contentDescription = "Selected",
                    tint = MaterialTheme.colorScheme.primary)
            }
        }
}

@Composable
fun RetentionOptionItem(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier =
            Modifier.fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onSurface)
            if (selected) {
                Icon(
                    Icons.Filled.Check,
                    contentDescription = "Selected",
                    tint = MaterialTheme.colorScheme.primary)
            }
        }
}

private class FakePreferenceManager(context: Context) : PreferenceManager(context) {
    private val intPrefs = mutableMapOf<String, Int>()
    private val stringPrefs = mutableMapOf<String, String>()

    override suspend fun saveInt(key: String, value: Int) {
        intPrefs[key] = value
    }

    override fun getIntFlow(key: String, defaultValue: Int): Flow<Int> {
        return flowOf(intPrefs.getOrDefault(key, defaultValue))
    }

    override suspend fun saveString(key: String, value: String) {
        stringPrefs[key] = value
    }

    override fun getStringFlow(key: String, defaultValue: String): Flow<String> {
        return flowOf(stringPrefs.getOrDefault(key, defaultValue))
    }
}

private class FakeThemePreferenceManager(private val prefManager: PreferenceManager) :
    ThemePreferenceManager(prefManager) {
    private var currentTheme = ThemeMode.SYSTEM

    override suspend fun saveTheme(theme: ThemeMode) {
        currentTheme = theme
    }

    override fun getThemeFlow(): Flow<ThemeMode> {
        return flowOf(currentTheme)
    }
}

private class FakeNotificationRepository : NotificationRepository {
    override suspend fun insertNotification(notificationEntity: NotificationEntity) {}

    override fun getAllNotificationsFlow(): Flow<List<NotificationEntity>> = flowOf(emptyList())

    override suspend fun deleteNotificationsOlderThan(cutoffTimestamp: Long): Int = 0

    override suspend fun deleteNotification(notificationEntity: NotificationEntity) {}
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    val context = LocalContext.current
    val fakePrefManager = FakePreferenceManager(context)
    val fakeThemePrefManager = FakeThemePreferenceManager(fakePrefManager)
    val fakeRepository = FakeNotificationRepository()
    val mockViewModel =
        SettingsViewModel(
            themePreferenceManager = fakeThemePrefManager,
            repository = fakeRepository,
            preferenceManager = fakePrefManager,
            workManager = WorkManager.getInstance(context))

    NotificityTheme(themeMode = ThemeMode.SYSTEM) {
        SettingsScreen(settingsViewModel = mockViewModel, onBack = {})
    }
}
