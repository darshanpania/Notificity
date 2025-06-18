package com.darshan.notificity.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.darshan.notificity.R

@Composable
fun AppTitle(
    title: String = "Notificity",
    font: FontFamily = FontFamily(Font(R.font.playfair_display)),
    titleColor: Color = MaterialTheme.colorScheme.primary
) {
    Text(
        text = title,
        style = MaterialTheme.typography.headlineLarge.copy(
            fontFamily = font,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.3.sp
        ),
        color = titleColor,
    )
}
