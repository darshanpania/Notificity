package com.darshan.notificity.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Displays a titled section with a description.
 *
 * If [onClick] is provided, the section becomes clickable.
 *
 * @param title The section title.
 * @param description The section description.
 * @param onClick Optional click action; null means non-clickable.
 */
@Composable
fun ClickableSection(title: String, description: String, onClick: (() -> Unit)? = null) {

    val modifier =
        if (onClick != null) {
            Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 8.dp)
        } else {
            Modifier.fillMaxWidth().padding(vertical = 8.dp)
        }

    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground)
    }
}
