package com.darshan.notificity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

    @Composable
    fun SettingsScreen(onBack: () -> Unit) {
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
                    onClick = { /* Navigate to Theme screen */ })
                SettingsCard(
                    icon = rememberVectorPainter(image = Icons.Default.Info),
                    text = "About",
                    onClick = { /* Navigate to About screen */ })
                SettingsCard(
                    icon = rememberVectorPainter(image = Icons.Default.Share),
                    text = "Recommend this app",
                    onClick = { recommendApp() })
            }
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
                        icon, modifier = Modifier.size(32.dp), contentDescription = null
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