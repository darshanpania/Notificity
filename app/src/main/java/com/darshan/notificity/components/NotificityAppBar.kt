package com.darshan.notificity.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.darshan.notificity.DarkGrey

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificityAppBar(
    title: String,
    actions: @Composable (RowScope.() -> Unit)? = null,
    navigationIcon: @Composable (() -> Unit)? = null
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    val isDark = isSystemInDarkTheme()

    val topBarColors = TopAppBarDefaults.topAppBarColors(
        containerColor = if (isDark) DarkGrey else Color.White,
        titleContentColor = if (isDark) Color.White else DarkGrey,
    )

    Surface(
        tonalElevation = 4.dp,
        shadowElevation = 4.dp,
    ) {
        TopAppBar(
            title = { Text(text = title) },
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


