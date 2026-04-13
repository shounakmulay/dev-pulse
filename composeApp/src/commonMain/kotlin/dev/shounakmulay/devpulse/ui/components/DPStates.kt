package dev.shounakmulay.devpulse.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.shounakmulay.devpulse.ui.theme.DPTheme

// ─────────────────────────────────────────────────────────────────────────────
// DPEMPTYSTATE
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun DPEmptyState(
    title: String,
    modifier: Modifier = Modifier,
    body: String? = null,
    icon: ImageVector? = null,
    action: (@Composable () -> Unit)? = null,
) {
    val colours = DPTheme.colours
    val spacing = DPTheme.spacing

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = spacing.hero),
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = colours.textTertiary.copy(alpha = 0.4f),
                    modifier = Modifier
                        .size(48.dp)
                        .padding(bottom = spacing.md),
                )
            }
            DPHeadline(
                text = title,
                size = DPTextSize.Small,
                color = colours.textPrimary,
            )
            if (body != null) {
                Spacer(Modifier.height(spacing.sm))
                DPBody(
                    text = body,
                    size = DPTextSize.Small,
                    color = colours.textTertiary,
                    modifier = Modifier.then(Modifier),
                )
            }
            if (action != null) {
                Spacer(Modifier.height(spacing.xl))
                action()
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// DPLOADINGSTATE
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun DPLoadingState(
    modifier: Modifier = Modifier,
    label: String? = null,
) {
    val colours = DPTheme.colours
    val spacing = DPTheme.spacing

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            CircularProgressIndicator(
                color = colours.accentPrimary,
                strokeWidth = 2.dp,
                modifier = Modifier.size(32.dp),
            )
            if (label != null) {
                Spacer(Modifier.height(spacing.md))
                DPMono(
                    text = label,
                    size = DPTextSize.Small,
                    color = colours.textTertiary,
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// DPERRORSTATE
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun DPErrorState(
    title: String,
    modifier: Modifier = Modifier,
    body: String? = null,
    icon: ImageVector? = null,
    onRetry: (() -> Unit)? = null,
) {
    val colours = DPTheme.colours
    val spacing = DPTheme.spacing

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = spacing.hero),
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = colours.signalErrorText.copy(alpha = 0.6f),
                    modifier = Modifier
                        .size(48.dp)
                        .padding(bottom = spacing.md),
                )
            }
            DPHeadline(
                text = title,
                size = DPTextSize.Small,
                color = colours.textPrimary,
            )
            if (body != null) {
                Spacer(Modifier.height(spacing.sm))
                DPBody(
                    text = body,
                    size = DPTextSize.Small,
                    color = colours.textTertiary,
                )
            }
            if (onRetry != null) {
                Spacer(Modifier.height(spacing.xl))
                DPButton(
                    label = "Try again",
                    variant = DPButtonVariant.Secondary,
                    size = DPButtonSize.Small,
                    onClick = onRetry,
                )
            }
        }
    }
}
