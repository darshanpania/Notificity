package com.darshan.notificity.ui.activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.darshan.notificity.BuildConfig
import com.darshan.notificity.R
import com.darshan.notificity.analytics.AnalyticsConstants
import com.darshan.notificity.analytics.AnalyticsLogger
import com.darshan.notificity.components.BuyMeACoffee
import com.darshan.notificity.components.ClickableSection
import com.darshan.notificity.components.NotificityAppBar
import com.darshan.notificity.extensions.openUrl
import com.darshan.notificity.ui.theme.NotificityTheme
import com.darshan.notificity.utils.Constants
import com.darshan.notificity.viewmodel.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AboutActivity : BaseActivity() {

    private val settingsViewModel: SettingsViewModel by viewModels()

    override val screenName: String
        get() = AnalyticsConstants.Screens.ABOUT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val themeMode by remember { settingsViewModel.themeMode }.collectAsStateWithLifecycle()

            NotificityTheme(themeMode = themeMode) {
                AboutScreen(onBack = { finish() })
            }
        }
    }
}

@Composable
fun AboutScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val showButton by remember {
        derivedStateOf {
            scrollState.maxValue == 0 || scrollState.maxValue != scrollState.value
        }
    }
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
        Box(modifier = Modifier.Companion.padding(innerPadding)) {
            Column(
                modifier = Modifier.Companion
                    .padding(16.dp)
                    .fillMaxSize()
                    .verticalScroll(scrollState),
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
                        context.openUrl("https://github.com/darshanpania/Notificity/blob/main/PRIVACY.md")

                        AnalyticsLogger.onPrivacyPolicyClicked()
                    })

                HorizontalDivider()

                Text(
                    text = "Contributors",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Contributor("Darshan Pania", "i_m_Pania")
                Contributor("Shivam Sharma", "ShivamS707")
                Contributor("Shrinath Gupta", "gupta_shrinath")
                Contributor("William John", "goonerdroid11")
                Contributor("Jay Rathod", "zzjjaayy")
                Contributor("Avadhut", "mr_whoknows55")
                Contributor("Md Anas Shikoh", "ansiili_billi")
            }
            AnimatedVisibility(
                visible = showButton, enter = fadeIn(), exit = fadeOut(),
                modifier = Modifier.Companion.align(Alignment.Companion.BottomCenter)
            ) {
                BuyMeACoffee(onClick = {
                    context.openUrl(Constants.BUY_ME_A_COFFEE_LINK)

                    AnalyticsLogger.onBuyMeCoffeeClicked()
                })
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun Contributor(name: String, twitterUsername: String) {
    val context = LocalContext.current
    val twitterProfileUrl = "https://twitter.com/$twitterUsername"
    val profilePicUrl = "https://unavatar.io/twitter/$twitterUsername"

    Row(
        modifier = Modifier.Companion
            .fillMaxWidth()
            .clickable {
                context.openUrl(twitterProfileUrl)

                AnalyticsLogger.onContributorProfileClicked(name)
            }
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.Companion.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.Companion.CenterVertically) {
            GlideImage(
                model = profilePicUrl,
                contentDescription = "$name profile picture",
                modifier = Modifier.Companion
                    .size(36.dp)
                    .clip(CircleShape),
                loading = placeholder(R.drawable.iv_profile),
                failure = placeholder(R.drawable.iv_profile)
            )
            Spacer(modifier = Modifier.Companion.width(8.dp))
            Text(
                text = name,
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        Icon(
            painterResource(id = R.drawable.iv_next),
            modifier = Modifier.Companion.size(16.dp),
            contentDescription = "Go to Twitter profile",
            tint = MaterialTheme.colorScheme.outline
        )
    }
}

@Composable
@Preview(showSystemUi = true, showBackground = true)
fun ShowAboutScreen() {
    AboutScreen {}
}