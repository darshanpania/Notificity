package com.darshan.notificity.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.darshan.notificity.R

@Composable
fun BuyMeACoffee(modifier: Modifier = Modifier, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }

    Image(
        painter = painterResource(R.drawable.buy_me_a_coffee),
        contentDescription = "Buy me a coffee",
        modifier =
            modifier.then(
                Modifier.width(width = 150.dp)
                    .clickable(
                        onClick = onClick, interactionSource = interactionSource, indication = null)
                    .padding(bottom = 20.dp)),
    )
}
