package com.darshan.notificity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldColors
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize ViewModel with factory
        val factory = NotificationViewModelFactory(this.application,AppDatabase.getInstance(this.application.applicationContext))
        val mainViewModel = ViewModelProvider(this,factory).get(MainViewModel::class.java)

        setContent {
            MaterialTheme{
                NotificityApp(mainViewModel)
            }
        }
    }

    private fun Context.startNotificationsActivity(appName: String) {
        val intent = Intent(this, NotificationsActivity::class.java).apply {
            putExtra("appName", appName)
        }
        startActivity(intent)
    }

    @Composable
    fun AppGridView(apps: List<AppInfo>, onAppSelected: (String) -> Unit) {

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),  // Adjust based on screen size or preference
            contentPadding = PaddingValues(8.dp)
        ) {
            items(apps) { app ->
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
            elevation = 4.dp,
            backgroundColor = MaterialTheme.colors.onPrimary
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(16.dp)
            ) {
                // Assume you have a way to load the app icon from packageName
                appInfo.icon?.let { Image(bitmap = it, contentDescription = "App Icon", modifier = Modifier.size(50.dp) ) }
                Text(text = appInfo.appName, style = MaterialTheme.typography.h6)
                Text(text = "${appInfo.notificationCount} Notifications")
            }
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
    fun MainContent(viewModel: MainViewModel) {
        val notifications by viewModel.notifications.observeAsState(listOf())//pass .value from top
        if(notifications.isEmpty()){
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
                    style = MaterialTheme.typography.body1,
                    color = MaterialTheme.colors.onPrimary
                )
            }
        }

        AppSearchScreen(viewModel = viewModel)

    }

    @Composable
    fun AppSearchScreen(viewModel: MainViewModel){
        var appSearchQuery by remember { mutableStateOf("") }

        val allApps = viewModel.appsInfo.observeAsState(initial = emptyList())

        Column {
            SearchBar("Search Apps... ",onSearchQueryChanged = { appSearchQuery = it })
            AppGridView(
                apps = allApps.value.filter {
                    it.appName.contains(appSearchQuery, ignoreCase = true)
                },
                onAppSelected = { appName -> startNotificationsActivity(appName)}
            )
        }
    }





    @Composable
    fun NotificationItem(notification: NotificationEntity) {
        Card(modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
            backgroundColor = MaterialTheme.colors.secondary) {
            Column(modifier = Modifier.padding(16.dp)) {
                //Text(text = "App: ${notification.appName}", style = MaterialTheme.typography.h6)
                Text(text = notification.title, style = MaterialTheme.typography.subtitle1)
                Text(text = notification.content, style = MaterialTheme.typography.body1)
                //Image(bitmap = notification.imageBitmap.asImageBitmap(), contentDescription = "PN Image" )
            }
        }
    }

    @Composable
    fun NotificityApp(viewModel: MainViewModel) {
        if (hasNotificationAccess()) {
            MainContent(viewModel)
        } else {
            RequestAccessScreen()
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
                text = "We need access to your notifications to manage and search them effectively.",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.body1,
                color = MaterialTheme.colors.onPrimary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { openNotificationAccessSettings() }) {
                Text("Grant Access")
            }
        }
    }

    fun MainActivity.openNotificationAccessSettings() {
        startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))
    }

    private fun hasNotificationAccess(): Boolean {
        val enabledListeners = Settings.Secure.getString(contentResolver, "enabled_notification_listeners")
        val packageName = packageName
        return enabledListeners != null && enabledListeners.contains(packageName)
    }

}