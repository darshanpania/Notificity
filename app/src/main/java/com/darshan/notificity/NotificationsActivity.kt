package com.darshan.notificity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider

class NotificationsActivity : AppCompatActivity() {
    private val repository: NotificationRepository by lazy { NotificationRepository(AppDatabase.getInstance(application).notificationDao()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val factory = NotificationViewModelFactory(this.application,repository)
        val viewModel = ViewModelProvider(this,factory).get(MainViewModel::class.java)
        val appName:String = intent.getStringExtra("appName").toString()
        setContent {
            NotificationSearchScreen(viewModel = viewModel, appName)
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
        colors = TextFieldDefaults.textFieldColors(textColor = MaterialTheme.colors.onPrimary),
        value = searchQuery,
        onValueChange = { searchQuery = it; onSearchQueryChanged(it) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        placeholder = { Text(hint, color = MaterialTheme.colors.onPrimary) },
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search Icon",tint = MaterialTheme.colors.onPrimary) },
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

        // Display the notifications using a LazyColumn
        LazyColumn {
            items(filteredNotifications) { notification ->
                NotificationItem(notification)
            }
        }

    }

@Composable
fun NotificationItem(notification: NotificationEntity) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp),
        backgroundColor = MaterialTheme.colors.onPrimary) {
        Column(modifier = Modifier.padding(16.dp)) {
            //Text(text = "App: ${notification.appName}", style = MaterialTheme.typography.h6)
            Text(text = notification.title, style = MaterialTheme.typography.subtitle1)
            Text(text = notification.content, style = MaterialTheme.typography.body1)
            //Image(bitmap = notification.imageBitmap.asImageBitmap(), contentDescription = "PN Image" )
        }
    }
}
