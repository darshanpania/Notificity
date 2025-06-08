package com.darshan.notificity

import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.runtime.collectAsState
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
            Box(modifier = Modifier.padding(innerPadding)) {
                Column(
                    modifier = Modifier
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
                            openUrl("https://github.com/darshanpania/Notificity/blob/main/PRIVACY.md")

                            AnalyticsLogger.onPrivacyPolicyClicked()
                        })

                    HorizontalDivider()

                    Text(
                        text = "Contributors",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Contributor("Darshan Pania", "i_m_Pania", context)
                    Contributor("Shivam Sharma", "ShivamS707", context)
                    Contributor("Shrinath Gupta", "gupta_shrinath", context)
                    Contributor("William John", "goonerdroid11", context)
                    Contributor("Jay Rathod", "zzjjaayy", context)
                    Contributor("Avadhut", "mr_whoknows55", context)
                    Contributor("Md Anas Shikoh", "ansiili_billi", context)
                }
                AnimatedVisibility(
                    visible = showButton, enter = fadeIn(), exit = fadeOut(),
                    modifier = Modifier.align(Alignment.BottomCenter)
                ) {
                    BuyMeACoffee(onClick = {
                        openUrl(Constants.BUY_ME_A_COFFEE_LINK)
                    })
                }
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
                openUrl(twitterProfileUrl)

                AnalyticsLogger.onContributorProfileClicked(name)
            }
            .padding(vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                GlideImage(
                    model = profilePicUrl,
                    contentDescription = "$name profile picture",
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape),
                    loading = placeholder(R.drawable.iv_profile),
                    failure = placeholder(R.drawable.iv_profile)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            Icon(
                painterResource(id = R.drawable.iv_next),
                modifier = Modifier.size(16.dp),
                contentDescription = "Go to Twitter profile",
                tint = MaterialTheme.colorScheme.outline
            )
        }
    }

    @Composable
    fun BuyMeACoffee(modifier: Modifier = Modifier, onClick: () -> Unit) {
        val interactionSource  = remember { MutableInteractionSource() }
        Image(
            modifier = modifier.then(
                Modifier
                    .width(width = 150.dp)
                    .clickable(onClick = onClick, interactionSource = interactionSource, indication = null)
                    .padding(bottom = 20.dp)
            ),
            painter = painterResource(R.drawable.buy_me_a_coffee),
            contentDescription = "Buy me a coffee"
        )
    }

    @Composable
    @Preview(showSystemUi = true, showBackground = true)
    fun ShowAboutScreen() {
        AboutScreen {}
    }
}