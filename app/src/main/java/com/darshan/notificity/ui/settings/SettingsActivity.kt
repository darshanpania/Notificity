package com.darshan.notificity.ui.settings

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.darshan.notificity.AboutActivity
import com.darshan.notificity.CardColor
import com.darshan.notificity.R
import com.darshan.notificity.analytics.AnalyticsConstants
import com.darshan.notificity.analytics.AnalyticsLogger
import com.darshan.notificity.components.NotificityAppBar
import com.darshan.notificity.components.dialogs.ConfirmationDialog
import com.darshan.notificity.extensions.launchActivity
import com.darshan.notificity.extensions.recommendApp
import com.darshan.notificity.ui.BaseActivity
import com.darshan.notificity.ui.signin.AuthViewModel
import com.darshan.notificity.ui.signin.SignInActivity
import com.darshan.notificity.ui.theme.LocalIsDarkTheme
import com.darshan.notificity.ui.theme.NotificityTheme
import com.darshan.notificity.ui.theme.ThemeMode
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsActivity : BaseActivity() {

    private val settingsViewModel: SettingsViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    override val screenName: String
        get() = AnalyticsConstants.Screens.SETTINGS

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val themeMode by remember { settingsViewModel.themeMode }.collectAsStateWithLifecycle()

            NotificityTheme(themeMode = themeMode) {
                SettingsScreen(
                    currentTheme = themeMode,
                    onBack = { finish() },
                    themeChange = settingsViewModel::updateTheme,
                    onLogout = {
                        authViewModel.signOut()

                        launchActivity<SignInActivity>()
                        finish()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    currentTheme: ThemeMode,
    themeChange: (ThemeMode) -> Unit,
    onBack: () -> Unit,
    onLogout: () -> Unit = {}
) {

    val sheetState = rememberModalBottomSheetState()
    val showSheet = remember { mutableStateOf(false) }
    val showLogoutDialog = remember { mutableStateOf(false) }
    val context = LocalContext.current

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
            modifier = Modifier.Companion
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SettingsCard(
                icon = painterResource(id = R.drawable.iv_theme),
                text = "Change Theme",
                onClick = { showSheet.value = true })
            SettingsCard(
                icon = rememberVectorPainter(image = Icons.Outlined.Info),
                text = "About",
                onClick = { context.launchActivity<AboutActivity>() })
            SettingsCard(
                icon = rememberVectorPainter(image = Icons.Outlined.Share),
                text = "Recommend this app",
                onClick = {
                    context.recommendApp()

                    AnalyticsLogger.onRecommendAppClicked()
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            LogoutSection(
                onLogoutClick = { showLogoutDialog.value = true }
            )
        }

        if (showSheet.value) {
            ModalBottomSheet(
                onDismissRequest = { showSheet.value = false },
                sheetState = sheetState,
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
                                themeChange(ThemeMode.SYSTEM)
                            }
                            showSheet.value = false
                        })
                    ThemeOptionItem(
                        icon = painterResource(id = R.drawable.iv_light_theme),
                        label = "Light Theme",
                        selected = currentTheme == ThemeMode.LIGHT,
                        onClick = {
                            if (currentTheme != ThemeMode.LIGHT) {
                                themeChange(ThemeMode.LIGHT)
                            }
                            showSheet.value = false
                        })
                    ThemeOptionItem(
                        icon = painterResource(id = R.drawable.iv_dark_theme),
                        label = "Dark Theme",
                        selected = currentTheme == ThemeMode.DARK,
                        onClick = {
                            if (currentTheme != ThemeMode.DARK) {
                                themeChange(ThemeMode.DARK)
                            }
                            showSheet.value = false
                        })
                }
            }
        }

        if (showLogoutDialog.value) {
            ConfirmationDialog(
                message = "Are you sure you want to logout?",
                confirmText = "Logout",
                isDestructive = true,
                onConfirm = {
                    showLogoutDialog.value = false
                    onLogout()
                },
                onDismiss = { showLogoutDialog.value = false }
            )
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
    val textColor =
        if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
    val iconTint =
        if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant

    Row(
        modifier = Modifier.Companion
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.Companion.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.Companion.CenterVertically) {
            Icon(
                painter = icon,
                contentDescription = label,
                modifier = Modifier.Companion.size(24.dp),
                tint = iconTint
            )
            Spacer(modifier = Modifier.Companion.width(16.dp))
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
        modifier = Modifier.Companion,
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) CardColor else Color.Companion.White,
        ),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.Companion
                .padding(18.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Companion.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.Companion.CenterVertically) {
                Icon(
                    icon,
                    modifier = Modifier.Companion.size(32.dp),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.Companion.width(24.dp))
                Text(
                    text,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Companion.Normal)
                )
            }
            Icon(
                painterResource(id = R.drawable.iv_next),
                modifier = Modifier.Companion.size(16.dp),
                contentDescription = "Navigate"
            )
        }
    }
}

@Composable
fun LogoutSection(
    onLogoutClick: () -> Unit
) {
    Column {
        HorizontalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        LogoutCard(onClick = onLogoutClick)
    }
}

@Composable
fun LogoutCard(
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(

        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.error.copy(alpha = 0.2f)
        )
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
                    painter = painterResource(id = R.drawable.ic_logout),
                    modifier = Modifier.size(32.dp),
                    contentDescription = "Logout",
                    tint = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.width(24.dp))
                Text(
                    text = "Logout",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.error
                    )
                )
            }
            Icon(
                painterResource(id = R.drawable.iv_next),
                modifier = Modifier.size(16.dp),
                contentDescription = "Navigate",
                tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
@Preview(showSystemUi = true, showBackground = true)
fun ShowSettingsScreen() {
    SettingsScreen(currentTheme = ThemeMode.LIGHT, themeChange = {}, onBack = {})
}