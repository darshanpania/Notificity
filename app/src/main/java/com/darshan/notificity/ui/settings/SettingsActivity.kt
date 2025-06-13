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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.darshan.notificity.AboutActivity
import com.darshan.notificity.CardColor
import com.darshan.notificity.Constants
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

    private val settingsViewModel: SettingsViewModel by viewModels()
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
        val currentRetentionPeriod by settingsViewModel.retentionPeriod.collectAsState()

        val themeSheetState = rememberModalBottomSheetState()
        val showThemeSheet = remember { mutableStateOf(false) }

        val retentionSheetState = rememberModalBottomSheetState()
        val showRetentionSheet = remember { mutableStateOf(false) }

        val showConfirmationDialog = remember { mutableStateOf(false) }
        val pendingRetentionPeriod = remember { mutableStateOf<Int?>(null) }

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
                SettingsCardWithLabel(
                    label = "Theme",
                    value = currentTheme.name.lowercase().replaceFirstChar { it.uppercase() },
                    onClick = { showThemeSheet.value = true }
                )

                SettingsCardWithLabel(
                    label = getString(R.string.setting_retention_period),
                    value = Constants.RetentionPeriod.getLabel(LocalContext.current, currentRetentionPeriod),
                    onClick = { showRetentionSheet.value = true }
                )

                SettingsCard(
                    icon = rememberVectorPainter(image = Icons.Outlined.Share),
                    text = "Recommend this app",
                    onClick = {
                        recommendApp()

                        AnalyticsLogger.onRecommendAppClicked()
                    }
                )
                SettingsCard(
                    icon = rememberVectorPainter(image = Icons.Outlined.Info),
                    text = "About",
                    onClick = { launchActivity<AboutActivity>() })
            }

            if (showThemeSheet.value) {
                ModalBottomSheet(
                    onDismissRequest = { showThemeSheet.value = false },
                    sheetState = themeSheetState,
                    containerColor = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                ) {
                    Column(
                        Modifier.Companion
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
                    sheetState = retentionSheetState
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Select Retention Period", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 16.dp))
                        val options = listOf(Constants.RetentionPeriod.DAYS_7, Constants.RetentionPeriod.DAYS_30, Constants.RetentionPeriod.UNLIMITED)
                        options.forEach { period ->
                            RetentionOption(
                                text = Constants.RetentionPeriod.getLabel(LocalContext.current, period),
                                isSelected = currentRetentionPeriod == period,
                                onClick = {
                                    // A smaller positive number is stricter. Unlimited (-1) is least strict.
                                    val isStricter = period != Constants.RetentionPeriod.UNLIMITED && (currentRetentionPeriod == Constants.RetentionPeriod.UNLIMITED || period < currentRetentionPeriod)

                                    if (isStricter) {
                                        pendingRetentionPeriod.value = period
                                        showConfirmationDialog.value = true
                                    } else {
                                        settingsViewModel.updateRetentionPeriod(period)
                                    }
                                    showRetentionSheet.value = false
                                }
                            )
                        }
                    }
                }
            }

            if (showConfirmationDialog.value) {
                val newPeriod = pendingRetentionPeriod.value
                if (newPeriod != null) {
                    AlertDialog(
                        onDismissRequest = { showConfirmationDialog.value = false },
                        title = { Text(getString(R.string.dialog_title_confirm_retention_change)) },
                        text = { Text(getString(R.string.dialog_message_confirm_retention_change, Constants.RetentionPeriod.getLabel(LocalContext.current, newPeriod))) },
                        confirmButton = {
                            TextButton(onClick = {
                                settingsViewModel.updateRetentionPeriod(newPeriod)
                                showConfirmationDialog.value = false
                            }) {
                                Text(getString(R.string.dialog_button_confirm))
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showConfirmationDialog.value = false }) {
                                Text(getString(R.string.dialog_button_cancel))
                            }
                        }
                    )
                }
            }
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
    fun SettingsCardWithLabel(
        label: String,
        value: String,
        onClick: () -> Unit
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .clickable { onClick() },
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = CardColor)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = label, style = MaterialTheme.typography.bodyLarge)
                Text(text = value, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
    fun ThemeOption(
        text: String,
        isSelected: Boolean,
        onClick: () -> Unit
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Spacer(modifier = Modifier.width(24.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = text, style = MaterialTheme.typography.bodyLarge)
        }
    }

    @Composable
    fun RetentionOption(
        text: String,
        isSelected: Boolean,
        onClick: () -> Unit
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Spacer(modifier = Modifier.width(24.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = text, style = MaterialTheme.typography.bodyLarge)
        }
    }

    @Composable
    @Preview(showSystemUi = true, showBackground = true)
    fun ShowSettingsScreen() {
        SettingsScreen(settingsViewModel) {}
    }
}