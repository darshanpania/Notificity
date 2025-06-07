package com.darshan.notificity

import android.content.Context
import android.content.Intent
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.darshan.notificity.analytics.AnalyticsConstants
import com.darshan.notificity.analytics.AnalyticsLogger
import com.darshan.notificity.components.ClickableSection
import com.darshan.notificity.components.NotificityAppBar
import com.darshan.notificity.extensions.getActivity
import com.darshan.notificity.extensions.openUrl
import com.darshan.notificity.ui.BaseActivity
import com.darshan.notificity.ui.settings.SettingsViewModel
import com.darshan.notificity.ui.theme.NotificityTheme

class AboutActivity : BaseActivity() {
    private val settingsViewModel: SettingsViewModel by viewModels()

    override val screenName: String
        get() = AnalyticsConstants.Screens.ABOUT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val themeMode by settingsViewModel.themeMode.collectAsState()

            NotificityTheme(themeMode = themeMode) {
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
                    title = "About",
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    })
            }) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
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

                        AnalyticsLogger.onPrivacyPolicyClicked()
                    })

                HorizontalDivider()

                Text("Contributors", style = MaterialTheme.typography.titleMedium)
                Contributor("Darshan Pania", "i_m_Pania", context)
                Contributor("Shivam Sharma", "ShivamS707", context)
                Contributor("Shrinath Gupta", "gupta_shrinath", context)
                Contributor("William John", "goonerdroid11", context)
                Contributor("Jay Rathod", "zzjjaayy", context)
                Contributor("Avadhut", "mr_whoknows55", context)
            }
        }
    }

    @OptIn(ExperimentalGlideComposeApi::class)
    @Composable
    fun Contributor(name: String, twitterUsername: String, context: Context) {
        val twitterProfileUrl = "https://twitter.com/$twitterUsername"
        val profilePicUrl = "https://unavatar.io/twitter/$twitterUsername"

        Row(modifier = Modifier
            .fillMaxWidth()
            .clickable {
                val intent = Intent(Intent.ACTION_VIEW, twitterProfileUrl.toUri())
                context.startActivity(intent)

                AnalyticsLogger.onContributorProfileClicked(name)
            }
            .padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
            GlideImage(
                model = profilePicUrl,
                contentDescription = "$name profile picture",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                loading = placeholder(R.drawable.iv_profile),
                failure = placeholder(R.drawable.iv_profile)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = name,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }

    @Composable
    @Preview(showSystemUi = true, showBackground = true)
    fun ShowAboutScreen() {
        AboutScreen {}
    }
}