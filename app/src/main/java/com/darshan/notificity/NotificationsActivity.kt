package com.darshan.notificity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darshan.notificity.ui.theme.NotificityTheme

class NotificationsActivity : ComponentActivity() {
    private val repository: NotificationRepository by lazy { NotificationRepository(AppDatabase.getInstance(application).notificationDao()) }
    private val viewModel: MainViewModel by viewModels {
        NotificationViewModelFactory(application, repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appName:String = intent.getStringExtra("appName").toString()
        this.actionBar?.hide()
        setContent {
            NotificityTheme {
                NotificationSearchScreen(viewModel = viewModel, appName)
            }
        }
    }
}

@Composable
fun NotificationSearchScreen(viewModel: MainViewModel, appName: String?) {
    var notificationSearchQuery by remember { mutableStateOf("") }

    Column {
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
        }


        AnimatedVisibility(
            filteredNotifications.isNotEmpty(),
            enter = fadeIn() + expandVertically()
        ) {
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
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(text = notification.title, style = MaterialTheme.typography.titleMedium, letterSpacing = 0.08.sp)
            Text(text = notification.content, style = MaterialTheme.typography.bodyMedium, letterSpacing = 0.08.sp)
        }
    }
}
