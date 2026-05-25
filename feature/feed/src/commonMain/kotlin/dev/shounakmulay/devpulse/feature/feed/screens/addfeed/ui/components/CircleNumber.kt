package dev.shounakmulay.devpulse.feature.feed.screens.addfeed.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextView
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextViewVariant
import dev.shounakmulay.devpulse.core.designsystem.icon.DPIcons
import dev.shounakmulay.devpulse.core.designsystem.theme.LocalDPContextColors
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueStatus

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

@Composable
fun CircleStatus(
    status: RssFeedQueueStatus?,
    modifier: Modifier = Modifier
) {
    val contextColors = LocalDPContextColors.current
    val animatedContainerColor by animateColorAsState(
        targetValue = when (status) {
            RssFeedQueueStatus.PROCESSING -> contextColors.infoContainer
            RssFeedQueueStatus.COMPLETED -> contextColors.successContainer
            RssFeedQueueStatus.FAILED -> MaterialTheme.colorScheme.errorContainer
            RssFeedQueueStatus.QUEUED, null -> MaterialTheme.colorScheme.surfaceDim
        },
    )
    val animatedOnContainerColor by animateColorAsState(
        targetValue = when (status) {
            RssFeedQueueStatus.PROCESSING -> contextColors.onInfoContainer
            RssFeedQueueStatus.COMPLETED -> contextColors.onSuccessContainer
            RssFeedQueueStatus.FAILED -> MaterialTheme.colorScheme.onErrorContainer
            RssFeedQueueStatus.QUEUED, null -> MaterialTheme.colorScheme.onSurface
        },
    )
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(56.dp) // Size of the circle
            .background(
                color = animatedContainerColor,
                shape = CircleShape
            )
    ) {
        AnimatedVisibility(
            status == RssFeedQueueStatus.PROCESSING,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            ContainedLoadingIndicator(
                containerColor = animatedContainerColor,
                indicatorColor = contextColors.onInfoContainer
            )
        }

        AnimatedVisibility(
            status != RssFeedQueueStatus.PROCESSING,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                tint = animatedOnContainerColor,
                imageVector = when (status) {
                    RssFeedQueueStatus.COMPLETED -> DPIcons.Completed
                    RssFeedQueueStatus.FAILED -> DPIcons.Failed
                    else -> DPIcons.Queued
                },
                contentDescription = ""
            )
        }
    }
}