package com.darshan.notificity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darshan.notificity.analytics.AnalyticsConstants
import com.darshan.notificity.analytics.AnalyticsLogger
import com.darshan.notificity.ui.BaseActivity
import com.darshan.notificity.ui.settings.SettingsViewModel
import com.darshan.notificity.ui.theme.NotificityTheme
import com.darshan.notificity.utils.Util

class NotificationsActivity : BaseActivity() {
    private val repository: NotificationRepository by lazy { NotificationRepository(AppDatabase.getInstance(application).notificationDao()) }
    private val viewModel: MainViewModel by viewModels {
        NotificationViewModelFactory(application, repository)
    }
    private val settingsViewModel: SettingsViewModel by viewModels()

    override val screenName: String
        get() = AnalyticsConstants.Screens.NOTIFICATION_LIST

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appName:String = intent.getStringExtra("appName").toString()
        this.actionBar?.hide()
        setContent {
            val themeMode by settingsViewModel.themeMode.collectAsState()

            NotificityTheme(themeMode = themeMode) {
                NotificationSearchScreen(viewModel = viewModel, appName)
            }
        }
    }
}

@Composable
fun NotificationSearchScreen(viewModel: MainViewModel, appName: String?) {
    var notificationSearchQuery by remember { mutableStateOf("") }

    Column(modifier = Modifier.windowInsetsPadding(WindowInsets.systemBars)) {
            SearchBar("Search Notifications in $appName", onSearchQueryChanged = { notificationSearchQuery = it })
            NotificationList(viewModel, appName, notificationSearchQuery)
        }
}

@Composable
fun SearchBar(hint: String, onSearchQueryChanged: (String) -> Unit) {
    var searchQuery by remember { mutableStateOf("") }

    TextField(
        value = searchQuery,
        onValueChange = { searchQuery = it; onSearchQueryChanged(it) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        placeholder = { Text(hint) },
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search Icon") },
        singleLine = true
    )
}

    @Composable
    fun NotificationList(viewModel: MainViewModel, appName: String?, searchQuery: String) {
        // Safely handle the case where appName is null
        if (appName == null) {
            // Optionally, display a message or return if no app is selected
            Text("Select an app to view notifications")
            return
        }

        // Get the list of notifications for the specified app
        val notifications = viewModel.notificationsGroupedByAppFlow.collectAsState(mapOf()).value.get(appName) ?: listOf()

        // Filter notifications based on the search query
        val filteredNotifications = notifications.filter {
            it.content.contains(searchQuery, ignoreCase = true) || it.title.contains(searchQuery, ignoreCase = true)
                    || Util.convertEpochLongToString(it.timestamp).contains(searchQuery,ignoreCase = true)
        }


        AnimatedVisibility(
            filteredNotifications.isNotEmpty(),
            enter = fadeIn() + expandVertically()
        ) {
            LaunchedEffect(key1 = Unit) {
                // LaunchedEffect ensures the logging doesn't rerun on every recomposition but initial composition.
                if (filteredNotifications.isNotEmpty()) {
                    AnalyticsLogger.onNotificationListOpened(appName, filteredNotifications.size)
                }
            }

            // Display the notifications using a LazyColumn
            LazyColumn {
                items(filteredNotifications, key = { it.id }) { notification ->
                    NotificationItem(notification)
                }
            }
        }
    }

@Composable
fun NotificationItem(notification: NotificationEntity) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(8.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(text = notification.title, style = MaterialTheme.typography.titleMedium, letterSpacing = 0.08.sp)
            Text(text = notification.content, style = MaterialTheme.typography.bodyMedium, letterSpacing = 0.08.sp)
            Spacer(modifier = Modifier.size(2.dp))
            Text(text = Util.convertEpochLongToString(notification.timestamp),
                style = MaterialTheme.typography.labelSmall,
                letterSpacing = 0.08.sp,
                modifier = Modifier.align(Alignment.End))
        }
    }
}