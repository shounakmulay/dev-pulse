package dev.shounakmulay.devpulse.feature.feed.screens.addfeed.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextView
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextViewVariant

@Composable
fun CircleNumber(
    number: Int,
    error: Boolean,
    modifier: Modifier = Modifier
) {
    val animatedColor by animateColorAsState(
        targetValue = if (error) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surfaceDim,
    )
    val animatedTextColor by animateColorAsState(
        targetValue = if (error) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSurface
    )
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(56.dp) // Size of the circle
            .background(
                color = animatedColor,
                shape = CircleShape
            )
    ) {
        DPTextView(
            text = number.toString(),
            color = animatedTextColor,
            variant = DPTextViewVariant.BodyLarge,
        )
    }
}