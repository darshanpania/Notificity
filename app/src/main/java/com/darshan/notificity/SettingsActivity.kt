package com.darshan.notificity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import com.darshan.notificity.components.NotificityAppBar
import com.darshan.notificity.extensions.getActivity
import com.darshan.notificity.extensions.launchActivity
import com.darshan.notificity.extensions.recommendApp
import com.darshan.notificity.ui.theme.NotificityTheme

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            NotificityTheme {
                val context = LocalContext.current

                SettingsScreen {
                    context.getActivity()?.finish()
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun SettingsScreen(onBack: () -> Unit) {
        val sheetState = rememberModalBottomSheetState()
        val showSheet = remember { mutableStateOf(false) }

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
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SettingsCard(
                    icon = painterResource(id = R.drawable.iv_theme),
                    text = "Change Theme",
                    onClick = { showSheet.value = true })
                SettingsCard(
                    icon = rememberVectorPainter(image = Icons.Outlined.Info),
                    text = "About",
                    onClick = { launchActivity<AboutActivity>() })
                SettingsCard(
                    icon = rememberVectorPainter(image = Icons.Outlined.Share),
                    text = "Recommend this app",
                    onClick = { recommendApp() })
            }

            if (showSheet.value) {
                ModalBottomSheet(
                    onDismissRequest = { showSheet.value = false },
                    sheetState = sheetState,
                    containerColor = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                ) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ThemeOptionItem(
                            icon = painterResource(id = R.drawable.iv_settings),
                            label = "System Default",
                            onClick = {
                                // Apply system theme
                                showSheet.value = false
                            }
                        )
                        ThemeOptionItem(
                            icon = painterResource(id = R.drawable.iv_light_theme),
                            label = "Light Theme",
                            onClick = {
                                // Apply light theme
                                showSheet.value = false
                            }
                        )
                        ThemeOptionItem(
                            icon = painterResource(id = R.drawable.iv_dark_theme),
                            label = "Dark Theme",
                            onClick = {
                                // Apply dark theme
                                showSheet.value = false
                            }
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun ThemeOptionItem(icon: Painter, label: String, onClick: () -> Unit) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(vertical = 12.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = icon,
                contentDescription = label,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(label, style = MaterialTheme.typography.bodyLarge)
        }
    }


    @Composable
    fun SettingsCard(
        icon: Painter, text: String, onClick: () -> Unit
    ) {
        val isDark = isSystemInDarkTheme()
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
        SettingsScreen {}
    }
}