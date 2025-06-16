package com.darshan.notificity.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.darshan.notificity.DarkGrey
import com.darshan.notificity.ui.theme.LocalIsDarkTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificityAppBar(
    title: String,
    actions: @Composable (RowScope.() -> Unit)? = null,
    navigationIcon: @Composable (() -> Unit)? = null
) {
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    val isDark = LocalIsDarkTheme.current

    val topBarColors = TopAppBarDefaults.topAppBarColors(
        containerColor = if (isDark) DarkGrey else Color.White,
        titleContentColor = if (isDark) Color.White else DarkGrey,
    )

    Surface(
        tonalElevation = 4.dp,
        shadowElevation = 4.dp,
    ) {
        TopAppBar(
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (navigationIcon != null) {
                        Spacer(modifier = Modifier.width(16.dp))
                    }
                    Text(text = title)
                }
            },
            navigationIcon = {
                navigationIcon?.invoke()
            },
            actions = {
                actions?.invoke(this)
            },
            scrollBehavior = scrollBehavior,
            colors = topBarColors
        )
    }
}


