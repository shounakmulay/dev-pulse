package dev.shounakmulay.devpulse.core.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.shounakmulay.devpulse.core.designsystem.compose.DPComponentPreview
import dev.shounakmulay.devpulse.core.designsystem.compose.Preview
import dev.shounakmulay.devpulse.core.designsystem.theme.DPVariantColors
import dev.shounakmulay.devpulse.core.designsystem.theme.dpPrimaryVariantColors
import dev.shounakmulay.devpulse.core.designsystem.theme.dpSecondaryVariantColors
import dev.shounakmulay.devpulse.core.designsystem.theme.dpTertiaryVariantColors

enum class DPProgressVariant { Primary, Secondary, Tertiary }

enum class DPSnackbarVariant { Info, Success, Warning, Danger, Neutral }

@Composable
@ReadOnlyComposable
private fun DPProgressVariant.colors(): DPVariantColors = when (this) {
    DPProgressVariant.Primary -> dpPrimaryVariantColors()
    DPProgressVariant.Secondary -> dpSecondaryVariantColors()
    DPProgressVariant.Tertiary -> dpTertiaryVariantColors()
}

private data class SnackbarColors(
    val container: Color,
    val onContainer: Color,
    val action: Color,
)

@Composable
@ReadOnlyComposable
private fun DPSnackbarVariant.resolveColors(): SnackbarColors {
    val cs = MaterialTheme.colorScheme
    return when (this) {
        DPSnackbarVariant.Info -> SnackbarColors(cs.secondaryContainer, cs.onSecondaryContainer, cs.secondary)
        DPSnackbarVariant.Success -> SnackbarColors(cs.tertiaryContainer, cs.onTertiaryContainer, cs.tertiary)
        DPSnackbarVariant.Warning -> SnackbarColors(cs.primaryContainer, cs.onPrimaryContainer, cs.primary)
        DPSnackbarVariant.Danger -> SnackbarColors(cs.errorContainer, cs.onErrorContainer, cs.error)
        DPSnackbarVariant.Neutral -> SnackbarColors(cs.surfaceContainerHigh, cs.onSurface, cs.primary)
    }
}

@Composable
fun DPLinearProgressIndicator(
    modifier: Modifier = Modifier,
    progress: (() -> Float)? = null,
    variant: DPProgressVariant = DPProgressVariant.Primary,
    color: Color? = null,
    trackColor: Color? = null,
    strokeCap: StrokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
) {
    val c = variant.colors()
    val resolvedColor = color ?: c.accent
    val resolvedTrack = trackColor ?: c.container
    if (progress == null) {
        LinearProgressIndicator(
            modifier = modifier,
            color = resolvedColor,
            trackColor = resolvedTrack,
            strokeCap = strokeCap,
        )
    } else {
        LinearProgressIndicator(
            progress = progress,
            modifier = modifier,
            color = resolvedColor,
            trackColor = resolvedTrack,
            strokeCap = strokeCap,
        )
    }
}

@Composable
fun DPCircularProgressIndicator(
    modifier: Modifier = Modifier,
    progress: (() -> Float)? = null,
    variant: DPProgressVariant = DPProgressVariant.Primary,
    color: Color? = null,
    strokeWidth: Dp = ProgressIndicatorDefaults.CircularStrokeWidth,
    trackColor: Color? = null,
    strokeCap: StrokeCap = ProgressIndicatorDefaults.CircularIndeterminateStrokeCap,
) {
    val c = variant.colors()
    val resolvedColor = color ?: c.accent
    val resolvedTrack = trackColor ?: c.container
    if (progress == null) {
        CircularProgressIndicator(
            modifier = modifier,
            color = resolvedColor,
            strokeWidth = strokeWidth,
            trackColor = resolvedTrack,
            strokeCap = strokeCap,
        )
    } else {
        CircularProgressIndicator(
            progress = progress,
            modifier = modifier,
            color = resolvedColor,
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
    val sc = variant.resolveColors()
    val action: (@Composable () -> Unit)? = actionLabel?.let { label ->
        @Composable {
            TextButton(onClick = onAction ?: {}) { Text(label) }
        }
    }
    val dismissAction: (@Composable () -> Unit)? = dismissLabel?.let { label ->
        @Composable {
            TextButton(onClick = onDismiss ?: {}) { Text(label) }
        }
    }
    Snackbar(
        modifier = modifier,
        action = action,
        dismissAction = dismissAction,
        shape = shape ?: SnackbarDefaults.shape,
        containerColor = sc.container,
        contentColor = sc.onContainer,
        actionContentColor = sc.action,
        dismissActionContentColor = sc.action,
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
            DPLinearProgressIndicator(variant = DPProgressVariant.Secondary)
            DPLinearProgressIndicator(progress = { 0.4f }, variant = DPProgressVariant.Secondary)
            DPCircularProgressIndicator()
            DPCircularProgressIndicator(progress = { 0.7f })
            DPCircularProgressIndicator(variant = DPProgressVariant.Secondary)
            DPCircularProgressIndicator(progress = { 0.5f }, variant = DPProgressVariant.Secondary)
        }
    }
}
