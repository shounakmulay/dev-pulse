package dev.shounakmulay.devpulse.feature.feed.screens.addfeed.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Expand
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.shounakmulay.devpulse.core.designsystem.components.DPHorizontalDivider
import dev.shounakmulay.devpulse.core.designsystem.components.DPIconButton
import dev.shounakmulay.devpulse.core.designsystem.components.DPIconButtonVariant
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextView
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextViewVariant
import dev.shounakmulay.devpulse.core.designsystem.theme.LocalDPSpacing

fun LazyGridScope.feedsHeaderDivider(
    toggleAllVisible: Boolean,
    title: String,
    onToggleCollapseAll: () -> Unit,
) {
    item(
        key = "FeedsHeaderDivider",
        span = { GridItemSpan(maxLineSpan) }
    ) {
        Row(
            modifier = Modifier.padding(LocalDPSpacing.current.sm).animateItem(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            DPTextView(
                text = title,
                variant = DPTextViewVariant.TitleSmallEmphasized
            )
            DPHorizontalDivider(Modifier.padding(LocalDPSpacing.current.sm).weight(1f))
            AnimatedVisibility(
                visible = toggleAllVisible,
                enter = fadeIn() + slideInHorizontally { it },
                exit = fadeOut() + slideOutHorizontally { it }
            ) {
                DPIconButton(
                    icon = Icons.Default.Expand,
                    variant = DPIconButtonVariant.Secondary,
                    contentDescription = "",
                    onClick = onToggleCollapseAll
                )
            }

        }
    }
}