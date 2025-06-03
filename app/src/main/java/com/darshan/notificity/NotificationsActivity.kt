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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darshan.notificity.ui.settings.SettingsViewModel
import com.darshan.notificity.ui.theme.NotificityTheme
import com.darshan.notificity.utils.Util

class NotificationsActivity : ComponentActivity() {
    private val repository: NotificationRepository by lazy {
        NotificationRepository(
            AppDatabase.getInstance(
                application
            ).notificationDao()
        )
    }
    private val viewModel: MainViewModel by viewModels {
        NotificationViewModelFactory(application, repository)
    }
    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appName: String = intent.getStringExtra("appName").toString()
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
fun NotificationSearchScreen(
    viewModel: MainViewModel,
    appName: String?
) {
    var notificationSearchQuery by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    val toggleDatePicker = remember { { showDatePicker = !showDatePicker } }
    var selectedDateRange by remember { mutableStateOf<Pair<Long?, Long?>>(null to null) }

    Column(modifier = Modifier.windowInsetsPadding(WindowInsets.systemBars)) {
        SearchBar(
            "Search Notifications in $appName",
            onSearchQueryChanged = { notificationSearchQuery = it },
            toggleDatePicker = toggleDatePicker
        )
        NotificationList(
            viewModel,
            appName,
            notificationSearchQuery,
        )
    }

    if (showDatePicker) {
        DateRangePickerModal(onDateRangeSelected = {
            selectedDateRange = it
            showDatePicker = false
        }, onDismiss = { showDatePicker = false }
        )
    }
}

@Composable
fun SearchBar(
    hint: String,
    onSearchQueryChanged: (String) -> Unit,
    toggleDatePicker: () -> Unit,
) {
    var searchQuery by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, top = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TextField(
            value = searchQuery,
            onValueChange = { searchQuery = it; onSearchQueryChanged(it) },
            placeholder = { Text(hint) },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search Icon") },
            singleLine = true,
        )
        Card(
            elevation = CardDefaults.cardElevation(4.dp),
            shape = CircleShape,
        ) {
            IconButton(onClick = toggleDatePicker) {
                Icon(Icons.Default.DateRange, contentDescription = "Date Picker")
            }
        }
    }
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
    val notifications =
        viewModel.notificationsGroupedByAppFlow.collectAsState(mapOf()).value[appName]
            ?: listOf()

    // Filter notifications based on the search query
    val filteredNotifications = notifications.filter {
        it.content.contains(searchQuery, ignoreCase = true) || it.title.contains(
            searchQuery,
            ignoreCase = true
        )
                || Util.convertEpochLongToString(it.timestamp)
            .contains(searchQuery, ignoreCase = true)
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = notification.title,
                style = MaterialTheme.typography.titleMedium,
                letterSpacing = 0.08.sp
            )
            Text(
                text = notification.content,
                style = MaterialTheme.typography.bodyMedium,
                letterSpacing = 0.08.sp
            )
            Spacer(modifier = Modifier.size(2.dp))
            Text(
                text = Util.convertEpochLongToString(notification.timestamp),
                style = MaterialTheme.typography.labelSmall,
                letterSpacing = 0.08.sp,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePickerModal(
    onDateRangeSelected: (Pair<Long?, Long?>) -> Unit,
    onDismiss: () -> Unit
) {
    val dateRangePickerState = rememberDateRangePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onDateRangeSelected(
                        Pair(
                            dateRangePickerState.selectedStartDateMillis,
                            dateRangePickerState.selectedEndDateMillis
                        )
                    )
                    onDismiss()
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DateRangePicker(
            state = dateRangePickerState,
            title = {
                Text(text = "Select date range")
            },
            showModeToggle = false,
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
                .padding(16.dp)
        )
    }
}