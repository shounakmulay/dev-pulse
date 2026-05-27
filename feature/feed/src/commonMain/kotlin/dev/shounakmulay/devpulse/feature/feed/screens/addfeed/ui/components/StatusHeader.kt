package dev.shounakmulay.devpulse.feature.feed.screens.addfeed.ui.components

import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.ui.Modifier
import dev.shounakmulay.devpulse.core.designsystem.components.DPButton
import dev.shounakmulay.devpulse.core.designsystem.components.DPButtonStyle
import dev.shounakmulay.devpulse.core.designsystem.components.DPButtonVariant
import dev.shounakmulay.devpulse.core.designsystem.components.DPIconButton
import dev.shounakmulay.devpulse.core.designsystem.components.DPIconButtonVariant
import dev.shounakmulay.devpulse.core.designsystem.components.DPSectionDivider
import dev.shounakmulay.devpulse.core.designsystem.icon.DPIcons
import dev.shounakmulay.devpulse.core.resources.stringRes
import devpulse.core.resources.generated.resources.add_feed_action_clear_all
import devpulse.core.resources.generated.resources.add_feed_action_queue
import devpulse.core.resources.generated.resources.add_feed_action_retry_failed_imports
import devpulse.core.resources.generated.resources.add_feed_status
import org.jetbrains.compose.resources.stringResource

fun LazyGridScope.statusHeader(
    visible: Boolean,
    onClearAll: () -> Unit,
    onRetryFailedImports: () -> Unit,
    onOpenQueue: () -> Unit
) {
    if (visible) {
        stickyHeader(
            key = "ProcessedActions",
        ) {
            DPSectionDivider(
                modifier = Modifier.animateItem(),
                title =stringResource(stringRes.add_feed_status) ,
            ) {
                DPButton(
                    variant = DPButtonVariant.Secondary,
                    text = stringResource(stringRes.add_feed_action_queue),
                    onClick = onOpenQueue,
                    style = DPButtonStyle.Text,
                    leadingIcon = Icons.AutoMirrored.Default.List
                )
                DPIconButton(
                    icon = DPIcons.Refresh,
                    variant = DPIconButtonVariant.Secondary,
                    contentDescription = stringResource(stringRes.add_feed_action_retry_failed_imports),
                    onClick = onRetryFailedImports
                )
                DPIconButton(
                    icon = DPIcons.ClearAll,
                    variant = DPIconButtonVariant.Secondary,
                    contentDescription = stringResource(stringRes.add_feed_action_clear_all),
                    onClick = onClearAll
                )
            }
        }
    }
}
