package com.darshan.notificity.main.ui

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.darshan.notificity.AppInfo
import com.darshan.notificity.NotificationEntity
import com.darshan.notificity.NotificationsActivity
import com.darshan.notificity.analytics.AnalyticsConstants
import com.darshan.notificity.analytics.AnalyticsLogger
import com.darshan.notificity.components.EmptyContentState
import com.darshan.notificity.components.LoadingScreen
import com.darshan.notificity.components.NotificityAppBar
import com.darshan.notificity.enums.NotificationPermissionStatus
import com.darshan.notificity.extensions.getNotificationPermissionStatus
import com.darshan.notificity.extensions.isLaunchedFromLauncher
import com.darshan.notificity.extensions.launchActivity
import com.darshan.notificity.extensions.openAppSettings
import com.darshan.notificity.extensions.toTitleCase
import com.darshan.notificity.main.viewmodel.MainViewModel
import com.darshan.notificity.ui.BaseActivity
import com.darshan.notificity.ui.settings.SettingsActivity
import com.darshan.notificity.ui.settings.SettingsViewModel
import com.darshan.notificity.ui.signin.AuthViewModel
import com.darshan.notificity.ui.signin.SignInActivity
import com.darshan.notificity.ui.theme.NotificityTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    private val mainViewModel: MainViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        val status = if (isGranted) {
            NotificationPermissionStatus.GRANTED
        } else {
            NotificationPermissionStatus.DENIED
        }
        logNotificationPermissionStatus(status)
    }

    private val appSettingsLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            // Re-check permission after returning from Settings
            val updatedStatus = getNotificationPermissionStatus()
            logNotificationPermissionStatus(updatedStatus)
        }

    override val screenName: String
        get() = AnalyticsConstants.Screens.MAIN

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            LoadingScreen()
        }

        handleAppLaunchAnalytics(savedInstanceState)
        checkAuthenticationAndRenderUI()
    }

    private fun checkAuthenticationAndRenderUI() {
        authViewModel.checkAuthState()

        lifecycleScope.launch {
            authViewModel.uiState
                .filter { it.isAuthChecked }
                .first()
                .let { uiState ->
                    if (!uiState.isAuthenticated) {
                        finish()
                        launchActivity<SignInActivity>()
                    } else {
                        renderMainContent(uiState.currentUser?.name)
                    }

                    uiState.error?.let { error ->
                        Toast.makeText(this@MainActivity, error, Toast.LENGTH_LONG).show()
                        authViewModel.clearError()
                    }
                }
        }
    }

    private fun renderMainContent(userName: String?) {
        setContent {
            val themeMode by remember { settingsViewModel.themeMode }.collectAsStateWithLifecycle()
            val isPermissionGranted by remember { mainViewModel.isNotificationPermissionGranted }.collectAsStateWithLifecycle()
            val showNotificationPermissionBlockedDialog by remember { mainViewModel.showNotificationPermissionBlockedDialog }.collectAsStateWithLifecycle()
            val notifications by remember { mainViewModel.notificationsFlow }.collectAsStateWithLifecycle()
            val apps by remember { mainViewModel.appInfoFromFlow }.collectAsStateWithLifecycle(initialValue = emptyList())

            NotificityTheme(themeMode = themeMode) {
                MainScreen(
                    userName = userName,
                    isPermissionGranted = isPermissionGranted,
                    showNotificationPermissionBlockedDialog = showNotificationPermissionBlockedDialog,
                    notifications = notifications,
                    apps = apps,
                    appSettingsLauncher = appSettingsLauncher,
                    openNotificationAccessSettings = { openNotificationAccessSettings() },
                    requestPermissionLauncher = { requestPermissionLauncher.launch(it) },
                    toggleNotificationPermissionDialog = mainViewModel::showNotificationPermissionBlockedDialog
                )
            }
        }
    }

    private fun handleAppLaunchAnalytics(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            // This block will NOT run during orientation change
            // It will ONLY run during a fresh launch (cold start)
            val source =
                if (intent.isLaunchedFromLauncher()) "launcher" else "external_or_notification"
            AnalyticsLogger.onAppLaunch(source)

            // Log notification permission status at app launch
            val status = getNotificationPermissionStatus()
            logNotificationPermissionStatus(status)
        }
    }

    // refresh notification permission state as soon as user comes back from setting screen
    private val activityForResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            mainViewModel.refreshNotificationPermission()
        }

    private fun openNotificationAccessSettings() {
        activityForResultLauncher.launch(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
    }

    fun logNotificationPermissionStatus(status: NotificationPermissionStatus) {
        AnalyticsLogger.onNotificationPermissionChanged(status)
        AnalyticsLogger.setNotificationPermissionProperty(status)
    }
}

@Composable
fun MainScreen(
    userName: String?,
    isPermissionGranted: Boolean,
    showNotificationPermissionBlockedDialog: Boolean,
    notifications: List<NotificationEntity>,
    apps: List<AppInfo>,
    appSettingsLauncher: ActivityResultLauncher<Intent>,
    toggleNotificationPermissionDialog: (Boolean) -> Unit,
    openNotificationAccessSettings: () -> Unit,
    requestPermissionLauncher: (String) -> Unit,
) {
    val context = LocalContext.current
    Scaffold(
        topBar = {
            NotificityAppBar(
                title = (userName?.let { "Hi, ${userName.toTitleCase()}" } ?:  "Notification"),
                actions = {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Open settings screen",
                        modifier = Modifier.Companion
                            .padding(end = 16.dp)
                            .clickable {
                                context.launchActivity<SettingsActivity>()
                            }
                    )
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.Companion.padding(innerPadding)) {
            if (isPermissionGranted) {
                AppSearchScreen(notifications = notifications, allApps = apps)
                AskNotificationPermission(
                    requestPermissionLauncher = requestPermissionLauncher,
                    toggleNotificationPermissionDialog = toggleNotificationPermissionDialog
                )
            } else {
                RequestAccessScreen(openNotificationAccessSettings = openNotificationAccessSettings)
            }
        }
    }

    if (showNotificationPermissionBlockedDialog) {
        PermissionBlockedDialog(
            onDismiss = { toggleNotificationPermissionDialog(false) },
            onGoToSettings = {
                toggleNotificationPermissionDialog(false)
                context.openAppSettings(appSettingsLauncher)
            }
        )
    }
}

@Composable
fun AppGridView(apps: List<AppInfo>, onAppSelected: (String) -> Unit) {

    LazyVerticalGrid(
        columns = GridCells.Fixed(2), // Adjust based on screen size or preference
        contentPadding = PaddingValues(8.dp)
    ) {
        items(items = apps, key = { it.packageName }) { app ->
            AppGridItem(appInfo = app, onClick = { onAppSelected(app.appName) })
        }
    }
}

@Composable
fun AppGridItem(appInfo: AppInfo, onClick: () -> Unit) {
    Card(
        modifier = Modifier.Companion
            .padding(8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.Companion.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.Companion
                .padding(16.dp)
                .fillMaxWidth()
                .aspectRatio(1f)
        ) {
            // Assume you have a way to load the app icon from packageName
            appInfo.icon?.let {
                Image(
                    bitmap = it,
                    contentDescription = "App Icon",
                    modifier = Modifier.Companion.size(50.dp)
                )
            } ?: run { Box(Modifier.Companion.size(50.dp)) }
            Spacer(modifier = Modifier.Companion.size(2.dp))
            Text(
                text = appInfo.appName,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Companion.Center,
                maxLines = 2,
                overflow = TextOverflow.Companion.Ellipsis,
                letterSpacing = 0.04.sp
            )
            Spacer(modifier = Modifier.Companion.size(2.dp))
            Text(
                text = "${appInfo.notificationCount} Notifications",
                textAlign = TextAlign.Companion.Center,
                letterSpacing = 0.04.sp
            )
        }
    }
}

@Composable
fun SearchBar(hint: String, onSearchQueryChanged: (String) -> Unit) {
    var searchQuery by remember { mutableStateOf("") }

    TextField(
        value = searchQuery,
        onValueChange = {
            searchQuery = it
            onSearchQueryChanged(it)
        },
        modifier = Modifier.Companion
            .fillMaxWidth()
            .padding(16.dp),
        placeholder = { Text(hint) },
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search Icon") },
        singleLine = true
    )
}

@Composable
fun AppSearchScreen(notifications: List<NotificationEntity>, allApps: List<AppInfo>) {
    val context = LocalContext.current
    var appSearchQuery by remember { mutableStateOf("") }
    val filteredApps = allApps.filter {
        it.appName.contains(appSearchQuery, ignoreCase = true)
    }

    Column {
        SearchBar("Search Apps... ", onSearchQueryChanged = { appSearchQuery = it })
        AnimatedContent(notifications, label = "app_list") { list ->
            when {
                list.isEmpty() -> {
                    // No notifications collected at all
                    EmptyContentState(
                        text = "No Notifications yet"
                    )
                }

                filteredApps.isEmpty() && appSearchQuery.isNotBlank() -> {
                    // Search active, but no matching app
                    EmptyContentState(
                        text = "No apps found matching \"$appSearchQuery\"",
                    )
                }

                else -> {
                    AppGridView(
                        apps = filteredApps,
                        onAppSelected = { appName ->
                            context.launchActivity<NotificationsActivity> {
                                putExtra("appName", appName)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun RequestAccessScreen(openNotificationAccessSettings: () -> Unit) {
    Column(
        modifier = Modifier.Companion
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.Companion.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text =
                "We need access to your notifications to manage and search them effectively.",
            textAlign = TextAlign.Companion.Center,
            style = MaterialTheme.typography.displaySmall,
        )
        Spacer(modifier = Modifier.Companion.height(16.dp))
        Button(onClick = openNotificationAccessSettings) { Text("Grant Access") }
    }
}

@Composable
private fun AskNotificationPermission(
    requestPermissionLauncher: (String) -> Unit,
    toggleNotificationPermissionDialog: (Boolean) -> Unit
) {
    var alreadyAsked by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    val activity = LocalActivity.current

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !alreadyAsked) {
        val permissionStatus = context.getNotificationPermissionStatus()

        alreadyAsked = true

        if (permissionStatus == NotificationPermissionStatus.DENIED) {
            if (activity?.shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) == true) {
                toggleNotificationPermissionDialog(true)
            } else {
                AnalyticsLogger.onNotificationPermissionRequested()
                requestPermissionLauncher(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}

@Composable
fun PermissionBlockedDialog(
    onDismiss: () -> Unit,
    onGoToSettings: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Permission needed") },
        text = { Text("Notification permission is blocked. Please enable it in app settings.") },
        confirmButton = {
            TextButton(onClick = { onGoToSettings() }) {
                Text("Go to Settings")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Later")
            }
        }
    )
}