package com.darshan.notificity.ui.settings

import android.os.Bundle
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import android.widget.Toast
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.mutableStateOf
import com.darshan.notificity.AboutActivity
import com.darshan.notificity.AppDatabase
import com.darshan.notificity.CardColor
import com.darshan.notificity.NotificationRepository
import com.darshan.notificity.NotificationViewModelFactory
import com.darshan.notificity.R
import com.darshan.notificity.analytics.AnalyticsConstants
import com.darshan.notificity.analytics.AnalyticsLogger
import com.darshan.notificity.components.NotificityAppBar
import com.darshan.notificity.extensions.getActivity
import com.darshan.notificity.extensions.launchActivity
import com.darshan.notificity.extensions.recommendApp
import com.darshan.notificity.ui.BaseActivity
import com.darshan.notificity.ui.theme.LocalIsDarkTheme
import com.darshan.notificity.ui.theme.NotificityTheme
import com.darshan.notificity.ui.theme.ThemeMode

class SettingsActivity : BaseActivity() {

    private val repository: NotificationRepository by lazy {
        NotificationRepository(AppDatabase.getInstance(application).notificationDao())
    }

    private val settingsViewModel: SettingsViewModel by viewModels {
        NotificationViewModelFactory(application, repository)
    }
    override val screenName: String
        get() = AnalyticsConstants.Screens.SETTINGS

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val themeMode by settingsViewModel.themeMode.collectAsState()

            NotificityTheme(themeMode = themeMode) {
                val context = LocalContext.current

                SettingsScreen(settingsViewModel = settingsViewModel) {
                    context.getActivity()?.finish()
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun SettingsScreen(
        settingsViewModel: SettingsViewModel,
        onBack: () -> Unit
    ) {
        val currentTheme by settingsViewModel.themeMode.collectAsState()
        val exportState by settingsViewModel.exportState.collectAsState()
        val context = LocalContext.current

        val themeSheetState = rememberModalBottomSheetState()
        var showThemeSheet by remember { mutableStateOf(false) }

        val exportSheetState = rememberModalBottomSheetState()
        var showExportSheet by remember { mutableStateOf(false) }
        var selectedExportFormat by remember { mutableStateOf<ExportFormat?>(null) }

        Scaffold(
            topBar = {
                NotificityAppBar(
                    title = "Settings",
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SettingsCard(
                    icon = painterResource(id = R.drawable.iv_theme),
                    text = "Change Theme",
                    onClick = { showThemeSheet = true })
                SettingsCard(
                    icon = painterResource(id = R.drawable.ic_export_data),
                    text = "Export Data",
                    onClick = { showExportSheet = true }
                )
                SettingsCard(
                    icon = rememberVectorPainter(image = Icons.Outlined.Info),
                    text = "About",
                    onClick = { launchActivity<AboutActivity>() })
                SettingsCard(
                    icon = rememberVectorPainter(image = Icons.Outlined.Share),
                    text = "Recommend this app",
                    onClick = {
                        recommendApp()

                        AnalyticsLogger.onRecommendAppClicked()
                    }
                )
            }

            if (showThemeSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showThemeSheet = false },
                    sheetState = themeSheetState,
                    containerColor = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                ) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ThemeOptionItem(
                            icon = painterResource(id = R.drawable.iv_settings),
                            label = "System Default",
                            selected = currentTheme == ThemeMode.SYSTEM,
                            onClick = {
                                if (currentTheme != ThemeMode.SYSTEM) {
                                    settingsViewModel.updateTheme(ThemeMode.SYSTEM)
                                }
                                showThemeSheet = false
                            })
                        ThemeOptionItem(
                            icon = painterResource(id = R.drawable.iv_light_theme),
                            label = "Light Theme",
                            selected = currentTheme == ThemeMode.LIGHT,
                            onClick = {
                                if (currentTheme != ThemeMode.LIGHT) {
                                    settingsViewModel.updateTheme(ThemeMode.LIGHT)
                                }
                                showThemeSheet = false
                            })
                        ThemeOptionItem(
                            icon = painterResource(id = R.drawable.iv_dark_theme),
                            label = "Dark Theme",
                            selected = currentTheme == ThemeMode.DARK,
                            onClick = {
                                if (currentTheme != ThemeMode.DARK) {
                                    settingsViewModel.updateTheme(ThemeMode.DARK)
                                }
                                showThemeSheet = false
                            })
                    }
                }
            }

            if (showExportSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showExportSheet = false },
                    sheetState = exportSheetState,
                    containerColor = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text("Select Export Format", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp))
                        Button(
                            onClick = {
                                selectedExportFormat = ExportFormat.CSV
                                AnalyticsLogger.onExportInitiated(ExportFormat.CSV.name)
                                settingsViewModel.exportData(ExportFormat.CSV, context)
                                showExportSheet = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Export as CSV")
                        }
                        Button(
                            onClick = {
                                selectedExportFormat = ExportFormat.JSON
                                AnalyticsLogger.onExportInitiated(ExportFormat.JSON.name)
                                settingsViewModel.exportData(ExportFormat.JSON, context)
                                showExportSheet = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Export as JSON")
                        }
                    }
                }
            }
        }

        when (val state = exportState) {
            is ExportState.Loading -> {
                AlertDialog(
                    onDismissRequest = { /* Optionally allow dismissing loading, or do nothing */ },
                    title = { Text("Exporting Data") },
                    text = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Please wait...")
                        }
                    },
                    confirmButton = { }
                )
            }
            is ExportState.Success -> {
                LaunchedEffect(state) { 
                    Toast.makeText(context, "Exported to: ${state.filePath}", Toast.LENGTH_LONG).show()
                    AnalyticsLogger.onExportCompleted(selectedExportFormat?.name ?: "UNKNOWN", success = true)
                    settingsViewModel.resetExportState() 
                }
            }
            is ExportState.Error -> {
                LaunchedEffect(state) { 
                    Toast.makeText(context, "Error: ${state.message}", Toast.LENGTH_LONG).show()
                    AnalyticsLogger.onExportCompleted(selectedExportFormat?.name ?: "UNKNOWN", success = false, error = state.message)
                    settingsViewModel.resetExportState() 
                }
            }
            is ExportState.Idle -> { /* Do nothing */ }
        }
    }

    @Composable
    fun ThemeOptionItem(
        icon: Painter,
        label: String,
        selected: Boolean,
        onClick: () -> Unit
    ) {
        val textColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        val iconTint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = icon,
                    contentDescription = label,
                    modifier = Modifier.size(24.dp),
                    tint = iconTint
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyLarge.copy(color = textColor)
                )
            }

            if (selected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }

    @Composable
    fun SettingsCard(
        icon: Painter, text: String, onClick: () -> Unit
    ) {
        val isDark = LocalIsDarkTheme.current

        Card(
            onClick = onClick,
            modifier = Modifier,
            colors = CardDefaults.cardColors(
                containerColor = if (isDark) CardColor else Color.White,
            ),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(18.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        icon,
                        modifier = Modifier.size(32.dp),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.width(24.dp))
                    Text(
                        text,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Normal)
                    )
                }
                Icon(
                    painterResource(id = R.drawable.iv_next),
                    modifier = Modifier.size(16.dp),
                    contentDescription = "Navigate"
                )
            }
        }
    }

    @Composable
    @Preview(showSystemUi = true, showBackground = true)
    fun ShowSettingsScreen() {
        SettingsScreen(settingsViewModel) {}
    }
}