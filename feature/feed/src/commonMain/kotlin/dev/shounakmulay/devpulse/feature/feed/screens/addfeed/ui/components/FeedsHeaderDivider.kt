package dev.shounakmulay.devpulse.feature.feed.screens.addfeed.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Expand
import androidx.compose.ui.Modifier
import dev.shounakmulay.devpulse.core.designsystem.components.DPIconButton
import dev.shounakmulay.devpulse.core.designsystem.components.DPIconButtonVariant
import dev.shounakmulay.devpulse.core.designsystem.components.DPSectionDivider
import dev.shounakmulay.devpulse.core.designsystem.theme.DPSize
import dev.shounakmulay.devpulse.core.designsystem.theme.LocalDPSpacing
import dev.shounakmulay.devpulse.core.resources.stringRes
import devpulse.core.resources.generated.resources.add_feed_action_expand_collapse_feeds
import devpulse.core.resources.generated.resources.add_feed_feeds
import org.jetbrains.compose.resources.stringResource

fun LazyGridScope.feedsHeaderDivider(
    toggleAllVisible: Boolean,
    onToggleCollapseAll: () -> Unit,
) {
    stickyHeader(
        key = "FeedsHeaderDivider",
    ) {
        DPSectionDivider(
            modifier = Modifier
                .animateItem()
                .heightIn(min = LocalDPSpacing.current.listItemHeight),
            title = stringResource(stringRes.add_feed_feeds),
        ) {
            AnimatedVisibility(
                visible = toggleAllVisible,
                enter = fadeIn() + slideInHorizontally { it },
                exit = fadeOut() + slideOutHorizontally { it }
            ) {
                DPIconButton(
                    icon = Icons.Default.Expand,
                    size = DPSize.Small,
                    variant = DPIconButtonVariant.Secondary,
                    contentDescription = stringResource(stringRes.add_feed_action_expand_collapse_feeds),
                    onClick = onToggleCollapseAll
                )
            }
        }
    }
}
