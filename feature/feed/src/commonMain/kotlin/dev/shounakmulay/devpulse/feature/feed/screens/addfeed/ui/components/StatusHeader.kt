package dev.shounakmulay.devpulse.feature.feed.screens.addfeed.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.shounakmulay.devpulse.core.designsystem.components.DPButton
import dev.shounakmulay.devpulse.core.designsystem.components.DPButtonStyle
import dev.shounakmulay.devpulse.core.designsystem.components.DPButtonVariant
import dev.shounakmulay.devpulse.core.designsystem.components.DPHorizontalDivider
import dev.shounakmulay.devpulse.core.designsystem.components.DPIconButton
import dev.shounakmulay.devpulse.core.designsystem.components.DPIconButtonVariant
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextView
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextViewVariant
import dev.shounakmulay.devpulse.core.designsystem.icon.DPIcons
import dev.shounakmulay.devpulse.core.designsystem.theme.LocalDPSpacing

fun LazyGridScope.statusHeader(
    visible: Boolean,
    title: String,
    onClearAll: () -> Unit,
    onRetryFailedImports: () -> Unit,
    onOpenQueue: () -> Unit
) {
    if (visible) {
        item(
            key = "ProcessedActions",
            span = { GridItemSpan(maxLineSpan) }
        ) {
            Row(
                modifier = Modifier.padding(LocalDPSpacing.current.sm),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                DPTextView(
                    text = title,
                    variant = DPTextViewVariant.TitleSmallEmphasized
                )
                DPHorizontalDivider(Modifier.padding(LocalDPSpacing.current.sm).weight(1f))
                DPButton(
                    variant = DPButtonVariant.Secondary,
                    text = "Queue",
                    onClick = onOpenQueue,
                    style = DPButtonStyle.Text,
                    leadingIcon = Icons.AutoMirrored.Default.List
                )
                DPIconButton(
                    icon = DPIcons.Refresh,
                    variant = DPIconButtonVariant.Secondary,
                    contentDescription = "",
                    onClick = onRetryFailedImports
                )
                DPIconButton(
                    icon = DPIcons.ClearAll,
                    variant = DPIconButtonVariant.Secondary,
                    contentDescription = "Clear All",
                    onClick = onClearAll
                )
            }
        }
    }
}