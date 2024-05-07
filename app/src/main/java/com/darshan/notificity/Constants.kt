package com.darshan.notificity

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

object Constants {
    const val dbName = "notification-db"
}

//    @Composable
//    fun AppCategoryList(viewModel: NotificationViewModel, searchQuery: String, onAppSelected: (String) -> Unit) {
//        val notificationsByApp = viewModel.notificationsGroupedByApp.observeAsState().value ?: mapOf()
//
//        LazyColumn {
//            notificationsByApp.filterKeys { it.contains(searchQuery, ignoreCase = true) }.forEach { (appName, notifications) ->
//                item {
//                    Text(
//                        text = appName,
//                        style = MaterialTheme.typography.h6,
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .clickable { onAppSelected(appName) }  // Clickable modifier to handle app selection
//                            .padding(8.dp)
//                    )
//                    notifications.forEach { notification ->
//                        NotificationItem(notification)
//                    }
//                }
//            }
//        }
//    }


//Working code
//-------------------
//@Composable
//fun AppCategoryList(viewModel: NotificationViewModel) {
//    val notificationsByApp = viewModel.notificationsGroupedByApp.observeAsState(mapOf())
//
//    LazyColumn {
//        notificationsByApp.value.forEach { (appName, notifications) ->
//            item {
//                Text(text = appName + "("+notifications.size+")",
//                    modifier = Modifier.padding(10.dp,0.dp),
//                    style = MaterialTheme.typography.h6,
//                    color = MaterialTheme.colors.primary)
//                notifications.forEach { notification ->
//                    NotificationItem(notification)  // Use your existing NotificationItem Composable
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun AppCategoryItem(appName: String, notifications: List<NotificationEntity>) {
//    Text(text = appName, style = MaterialTheme.typography.h6)
//    notifications.forEach { notification ->
//        NotificationItem(notification)  // Reuse the NotificationItem Composable
//    }
//}



//    @Composable
//    fun NotificationList(viewModel: NotificationViewModel, appName: String?, searchQuery: String) {
//        // Safely handle the case where appName is null
//        if (appName == null) {
//            // Optionally, display a message or return if no app is selected
//            Text("Select an app to view notifications")
//            return
//        }
//
//        // Get the list of notifications for the specified app
//        val notifications = viewModel.notificationsGroupedByApp.observeAsState().value?.get(appName) ?: listOf()
//
//        // Filter notifications based on the search query
//        val filteredNotifications = notifications.filter {
//            it.content!!.contains(searchQuery, ignoreCase = true) || it.title!!.contains(searchQuery, ignoreCase = true)
//        }
//
//        // Display the notifications using a LazyColumn
//        LazyColumn {
//            items(filteredNotifications) { notification ->
//                NotificationItem(notification)
//            }
//        }
//    }


//Working code
//@Composable
//fun NotificationList(notifications: List<NotificationEntity>) {
//
//    LazyColumn(modifier = Modifier.padding(16.dp)) {
//        items(notifications) { notification ->
//            NotificationItem(notification)
//        }
//    }
//
//}


//@Composable
//fun NotificationSearchScreen(viewModel: NotificationViewModel) {
//    var appSearchQuery by remember { mutableStateOf("") }
//    var notificationSearchQuery by remember { mutableStateOf("") }
//    var selectedApp by remember { mutableStateOf<String?>(null) }
//
//    Column {
//        if (selectedApp == null) {
//            SearchBar("Search Apps...", onSearchQueryChanged = { appSearchQuery = it })
////                AppCategoryList(viewModel, appSearchQuery) {
////                    selectedApp = it
////                }
//        } else {
//            SearchBar("Search Notifications in $selectedApp", onSearchQueryChanged = { notificationSearchQuery = it })
//            //NotificationList(viewModel, selectedApp, notificationSearchQuery)
//            Button(onClick = { selectedApp = null }) {
//                Text("Back to App List")
//            }
//        }
//    }
//}