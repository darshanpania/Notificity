package com.darshan.notificity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.darshan.notificity.analytics.AnalyticsConstants
import com.darshan.notificity.analytics.AnalyticsLogger
import com.darshan.notificity.components.NotificityAppBar
import com.darshan.notificity.extensions.isLaunchedFromLauncher
import com.darshan.notificity.extensions.launchActivity
import com.darshan.notificity.extensions.openAppSettings
import com.darshan.notificity.ui.BaseActivity
import com.darshan.notificity.ui.settings.SettingsActivity
import com.darshan.notificity.ui.settings.SettingsViewModel
import com.darshan.notificity.ui.theme.NotificityTheme

class MainActivity : BaseActivity() {
    private val repository: NotificationRepository by lazy {
        NotificationRepository(AppDatabase.getInstance(application).notificationDao())
    }

    private val mainViewModel: MainViewModel by
    viewModels<MainViewModel> {
        NotificationViewModelFactory(this.application, repository = repository)
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {}

    private val settingsViewModel: SettingsViewModel by viewModels()

    override val screenName: String
        get() = AnalyticsConstants.Screens.MAIN

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleAppLaunchAnalytics(savedInstanceState)

        setContent {
            val themeMode by settingsViewModel.themeMode.collectAsState()

            NotificityTheme(themeMode = themeMode) {
                NotificityApp(mainViewModel)
            }
        }
    }

    private fun handleAppLaunchAnalytics(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            // This block will NOT run during orientation change
            // It will ONLY run during a fresh launch (cold start)
            val source = if (intent.isLaunchedFromLauncher()) "launcher" else "external_or_notification"
            AnalyticsLogger.onAppLaunch(source)
        }
    }

    private fun Context.startNotificationsActivity(appName: String) {
        launchActivity<NotificationsActivity> {
            putExtra("appName", appName)
        }
    }

    @Composable
    fun AppGridView(apps: List<AppInfo>, onAppSelected: (String) -> Unit) {

        LazyVerticalGrid(
            columns = GridCells.Fixed(2), // Adjust based on screen size or preference
            contentPadding = PaddingValues(8.dp)
        ) {
            items(apps, key = { it.packageName }) { app ->
                AppGridItem(appInfo = app, onClick = { onAppSelected(app.appName) })
            }
        }
    }

    @Composable
    fun AppGridItem(appInfo: AppInfo, onClick: () -> Unit) {
        Card(
            modifier = Modifier
                .padding(8.dp)
                .clickable(onClick = onClick),
            elevation = CardDefaults.cardElevation(4.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .aspectRatio(1f)
            ) {
                // Assume you have a way to load the app icon from packageName
                appInfo.icon?.let {
                    Image(
                        bitmap = it,
                        contentDescription = "App Icon",
                        modifier = Modifier.size(50.dp)
                    )
                } ?: kotlin.run { Box(Modifier.size(50.dp)) }
                Spacer(modifier = Modifier.size(2.dp))
                Text(
                    text = appInfo.appName,
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    letterSpacing = 0.04.sp
                )
                Spacer(modifier = Modifier.size(2.dp))
                Text(
                    text = "${appInfo.notificationCount} Notifications",
                    textAlign = TextAlign.Center,
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            placeholder = { Text(hint) },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search Icon") },
            singleLine = true
        )
    }

    @Composable
    fun AppSearchScreen(viewModel: MainViewModel) {
        val notifications by viewModel.notificationsFlow.collectAsState(initial = emptyList())
        var appSearchQuery by remember { mutableStateOf("") }
        val allApps = viewModel.appInfoFromFlow.collectAsState(initial = emptyList()).value
        Column {
            SearchBar("Search Apps... ", onSearchQueryChanged = { appSearchQuery = it })
            AnimatedContent(notifications, label = "app_list") { list ->
                if (list.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "No Notifications yet",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.displaySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                } else {
                    AppGridView(
                        apps =
                        allApps.filter {
                            it.appName.contains(appSearchQuery, ignoreCase = true)
                        },
                        onAppSelected = { appName -> startNotificationsActivity(appName) })
                }
            }
        }
    }

    @Composable
    fun NotificityApp(viewModel: MainViewModel) {
        val isPermissionGranted by viewModel.isNotificationPermissionGranted.collectAsState()
        val showNotificationPermissionBlockedDialog by viewModel.showNotificationPermissionBlockedDialog.collectAsState()

        Scaffold(
            topBar = {
                NotificityAppBar(
                    title = "Notificity",
                    actions = {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Open settings screen",
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .clickable {
                                    launchActivity<SettingsActivity>()
                                }
                        )
                    }
                )
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                if (isPermissionGranted) {
                    AppSearchScreen(viewModel)
                    AskNotificationPermission()
                } else {
                    RequestAccessScreen()
                }
            }
        }

        if (showNotificationPermissionBlockedDialog) {
            PermissionBlockedDialog(
                onDismiss = { viewModel.showNotificationPermissionBlockedDialog(false) },
                onGoToSettings = {
                    viewModel.showNotificationPermissionBlockedDialog(false)
                    openAppSettings()
                }
            )
        }
    }

    @Composable
    fun RequestAccessScreen() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text =
                "We need access to your notifications to manage and search them effectively.",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.displaySmall,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { openNotificationAccessSettings() }) { Text("Grant Access") }
        }
    }

    // refresh notification permission state as soon as user comes back from setting screen
    private val activityForResultLauncher =
        registerForActivityResult(StartActivityForResult()) {
            mainViewModel.refreshNotificationPermission()
        }

    private fun openNotificationAccessSettings() {
        activityForResultLauncher.launch(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
    }

    @Composable
    private fun AskNotificationPermission() {
        val alreadyAsked = rememberSaveable { mutableStateOf(false) }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !alreadyAsked.value) {
            val permissionState = ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            )

            alreadyAsked.value = true

            if (permissionState != PackageManager.PERMISSION_GRANTED) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                    mainViewModel.showNotificationPermissionBlockedDialog(true)
                } else {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
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
}
