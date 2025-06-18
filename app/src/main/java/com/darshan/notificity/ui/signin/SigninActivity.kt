package com.darshan.notificity.ui.signin

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.darshan.notificity.R
import com.darshan.notificity.analytics.AnalyticsConstants
import com.darshan.notificity.components.AppTitle
import com.darshan.notificity.components.HeadlineWithDescription
import com.darshan.notificity.components.LottieCenteredAnimation
import com.darshan.notificity.components.PrimaryActionButton
import com.darshan.notificity.components.SecondaryTextButton
import com.darshan.notificity.extensions.getActivity
import com.darshan.notificity.extensions.launchActivity
import com.darshan.notificity.main.ui.MainActivity
import com.darshan.notificity.ui.BaseActivity
import com.darshan.notificity.ui.settings.SettingsViewModel
import com.darshan.notificity.ui.theme.NotificityTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignInActivity : BaseActivity() {

    private val authViewModel: AuthViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by viewModels()

    override val screenName: String
        get() = AnalyticsConstants.Screens.SIGNIN

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val themeMode by remember { settingsViewModel.themeMode }.collectAsStateWithLifecycle()
            NotificityTheme(themeMode = themeMode) {
                SignInScreen(
                    viewModel = authViewModel, onNavigateToMain = {
                        launchActivity<MainActivity>()
                        finish()
                    }
                )
            }
        }
    }

    @Composable
    fun SignInScreen(
        viewModel: AuthViewModel,
        onNavigateToMain: () -> Unit
    ) {
        val uiState by viewModel.uiState.collectAsState()
        val context = LocalContext.current

        LaunchedEffect(uiState.isAuthenticated) {
            if (uiState.isAuthenticated) {
                onNavigateToMain()
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                val infiniteTransition =
                    rememberInfiniteTransition(label = "Notification Animation")
                val rotation by infiniteTransition.animateFloat(
                    initialValue = -15f,
                    targetValue = 15f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(durationMillis = 400, easing = LinearEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "Notification Rotation"
                )

                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "App Title",
                    modifier = Modifier
                        .size(32.dp)
                        .graphicsLayer {
                            rotationZ = rotation
                            transformOrigin = TransformOrigin(0.5f, 0f)
                        },
                    tint = Color(0xFFEBAB00),
                )

                Spacer(modifier = Modifier.width(8.dp))
                AppTitle()
            }

            LottieCenteredAnimation(animationRes = R.raw.lottie_signin_page)

            Spacer(modifier = Modifier.height(24.dp))

            HeadlineWithDescription(
                title = "Never Miss A Notification",
                description = "Organize, categorize and track all your notifications in one place."
            )

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Google Sign In Button
                    PrimaryActionButton(
                        text = "Continue with Google",
                        iconPainter = painterResource(id = R.drawable.ic_google),
                        showLoader = uiState.isLoading,
                        onClick = {
                            viewModel.signInWithGoogle(context.getActivity() as SignInActivity)
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    SecondaryTextButton(
                        text = "Skip for now",
                        enabled = !uiState.isLoading,
                        onClick = {
                            viewModel.signInAnonymously()
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}