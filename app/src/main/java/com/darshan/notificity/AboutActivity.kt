package com.darshan.notificity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.darshan.notificity.components.ClickableSection
import com.darshan.notificity.components.NotificityAppBar
import com.darshan.notificity.extensions.getActivity
import com.darshan.notificity.extensions.openUrl
import com.darshan.notificity.ui.theme.NotificityTheme

class AboutActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            NotificityTheme {
                val context = LocalContext.current

                AboutScreen {
                    context.getActivity()?.finish()
                }
            }
        }
    }

    @Composable
    fun AboutScreen(onBack: () -> Unit) {
        val context = LocalContext.current

        Scaffold(
            topBar = {
                NotificityAppBar(
                    title = "About", navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    })
            }) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "An app designed to capture and categorizes your notifications, so you never miss or lose important messages.",
                    style = MaterialTheme.typography.bodyMedium
                )

                ClickableSection(
                    title = "App Version", description = BuildConfig.VERSION_NAME
                )

                HorizontalDivider()

                ClickableSection(
                    title = "Privacy Policy",
                    description = "Learn more about how the app manages your data",
                    onClick = {
                        openUrl("https://github.com/darshanpania/Notificity/blob/main/PRIVACY.md")
                    })

                HorizontalDivider()

                Text("Contributors", style = MaterialTheme.typography.titleMedium)
                Contributor("Darshan Pania", "https://twitter.com/darshanpania", context)
                Contributor("Shrinath Gupta", "https://twitter.com/shrinathgupta", context)
                Contributor("Shivam Sharma", "https://twitter.com/shivamsharma", context)
            }
        }
    }

    @Composable
    fun Contributor(name: String, twitterUrl: String, context: Context) {
        Text(text = "- $name", modifier = Modifier
            .clickable {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(twitterUrl))
                context.startActivity(intent)
            }
            .padding(vertical = 2.dp), style = MaterialTheme.typography.bodySmall)
    }

    @Composable
    @Preview(showSystemUi = true, showBackground = true)
    fun ShowAboutScreen() {
        AboutScreen {}
    }
}