package dev.shounakmulay.devpulse.feature.feed.screens.feedlist.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.shounakmulay.devpulse.core.designsystem.components.DPButton
import dev.shounakmulay.devpulse.core.designsystem.components.DPButtonStyle
import dev.shounakmulay.devpulse.core.designsystem.theme.DPSize
import dev.shounakmulay.devpulse.core.designsystem.theme.LocalDPSpacing
import dev.shounakmulay.devpulse.core.resources.stringRes
import devpulse.core.resources.generated.resources.feed_action_retry
import org.jetbrains.compose.resources.stringResource

@Composable
fun AppendErrorRow(onRetry: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth().height(LocalDPSpacing.current.listItemHeight),
        contentAlignment = Alignment.Center,
    ) {
        DPButton(
            text = stringResource(stringRes.feed_action_retry),
            onClick = onRetry,
            style = DPButtonStyle.Text,
            size = DPSize.Small,
        )
    }
}
