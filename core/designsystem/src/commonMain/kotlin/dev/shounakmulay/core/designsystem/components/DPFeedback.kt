package dev.shounakmulay.core.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.shounakmulay.core.designsystem.compose.DPComponentPreview
import dev.shounakmulay.core.designsystem.compose.Preview
import dev.shounakmulay.core.designsystem.theme.*

enum class DPSnackbarVariant {
    Info,
    Success,
    Warning,
    Danger,
    Neutral,
}

@Composable
fun DPLinearProgressIndicator(
    modifier: Modifier = Modifier,
    progress: (() -> Float)? = null,
    intent: DPIntent = DPIntent.Primary,
    trackColor: Color? = null,
    strokeCap: StrokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
) {
    val c = intent.colors()
    val color = c.accent
    val resolvedTrack = trackColor ?: c.containerVariant
    if (progress == null) {
        LinearProgressIndicator(
            modifier = modifier,
            color = color,
            trackColor = resolvedTrack,
            strokeCap = strokeCap,
        )
    } else {
        LinearProgressIndicator(
            progress = progress,
            modifier = modifier,
            color = color,
            trackColor = resolvedTrack,
            strokeCap = strokeCap,
        )
    }
}

@Composable
fun DPCircularProgressIndicator(
    modifier: Modifier = Modifier,
    progress: (() -> Float)? = null,
    intent: DPIntent = DPIntent.Primary,
    strokeWidth: Dp = ProgressIndicatorDefaults.CircularStrokeWidth,
    trackColor: Color? = null,
    strokeCap: StrokeCap = ProgressIndicatorDefaults.CircularIndeterminateStrokeCap,
) {
    val c = intent.colors()
    val color = c.accent
    val resolvedTrack = trackColor ?: c.containerVariant
    if (progress == null) {
        CircularProgressIndicator(
            modifier = modifier,
            color = color,
            strokeWidth = strokeWidth,
            trackColor = resolvedTrack,
            strokeCap = strokeCap,
        )
    } else {
        CircularProgressIndicator(
            progress = progress,
            modifier = modifier,
            color = color,
            strokeWidth = strokeWidth,
            trackColor = resolvedTrack,
            strokeCap = ProgressIndicatorDefaults.CircularDeterminateStrokeCap,
        )
    }
}

@Composable
fun DPSnackbar(
    message: String,
    modifier: Modifier = Modifier,
    variant: DPSnackbarVariant = DPSnackbarVariant.Neutral,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    dismissLabel: String? = null,
    onDismiss: (() -> Unit)? = null,
    shape: Shape? = null,
) {
    val intent = when (variant) {
        DPSnackbarVariant.Info -> DPIntent.Info
        DPSnackbarVariant.Success -> DPIntent.Success
        DPSnackbarVariant.Warning -> DPIntent.Warning
        DPSnackbarVariant.Danger -> DPIntent.Danger
        DPSnackbarVariant.Neutral -> DPIntent.Neutral
    }
    val ic = intent.colors()
    val action: (@Composable () -> Unit)? = actionLabel?.let { label ->
        @Composable {
            TextButton(onClick = onAction ?: {}) {
                Text(label)
            }
        }
    }
    val dismissAction: (@Composable () -> Unit)? = dismissLabel?.let { label ->
        @Composable {
            TextButton(onClick = onDismiss ?: {}) {
                Text(label)
            }
        }
    }
    Snackbar(
        modifier = modifier,
        action = action,
        dismissAction = dismissAction,
        shape = shape ?: SnackbarDefaults.shape,
        containerColor = ic.containerVariant,
        contentColor = ic.onContainer,
        actionContentColor = ic.accent,
        dismissActionContentColor = ic.accent,
    ) {
        Text(message)
    }
}

@Composable
fun DPSnackbarHost(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    variant: DPSnackbarVariant = DPSnackbarVariant.Neutral,
    snackbar: @Composable (SnackbarData) -> Unit = { data ->
        DPSnackbar(
            message = data.visuals.message,
            variant = variant,
            actionLabel = data.visuals.actionLabel,
            onAction = { data.performAction() },
        )
    },
) {
    SnackbarHost(
        hostState = hostState,
        modifier = modifier,
        snackbar = snackbar,
    )
}

@DPComponentPreview
@Composable
private fun DPFeedbackPreview() {
    Preview {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            DPSnackbarVariant.entries.forEach { v ->
                DPSnackbar(message = "Variant: $v", variant = v)
            }
            DPLinearProgressIndicator()
            DPLinearProgressIndicator(progress = { 0.6f })
            DPLinearProgressIndicator(intent = DPIntent.Secondary)
            DPLinearProgressIndicator(
                progress = { 0.4f },
                intent = DPIntent.Secondary,
            )
            DPCircularProgressIndicator()
            DPCircularProgressIndicator(progress = { 0.7f })
            DPCircularProgressIndicator(intent = DPIntent.Secondary)
            DPCircularProgressIndicator(
                progress = { 0.5f },
                intent = DPIntent.Secondary,
            )
        }
    }
}
