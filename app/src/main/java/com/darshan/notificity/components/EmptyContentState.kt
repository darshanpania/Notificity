package com.darshan.notificity.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.darshan.notificity.R

/**
 * A reusable composable that displays a centered empty state with a Lottie animation and a message.
 *
 * This component is useful for showing empty UI states such as no search results, no data
 * available, or error states.
 *
 * @param text The message to display below the animation.
 * @param lottieRes The optional Lottie animation resource to show. Defaults to
 *   [R.raw.lottie_no_data].
 * @param modifier Optional [Modifier] for styling the layout.
 */
@Composable
fun EmptyContentState(
    text: String,
    modifier: Modifier = Modifier,
    lottieRes: Int = R.raw.lottie_no_data,
) {
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {
            val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(lottieRes))
            val progress by
                animateLottieCompositionAsState(
                    composition = composition, iterations = LottieConstants.IterateForever)

            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier.size(200.dp))

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = text,
                maxLines = 2,
                fontSize = 20.sp,
                style = MaterialTheme.typography.titleMedium)
        }
}
