package dev.shounakmulay.devpulse.feature.feed.screens.feed.ui.components.feeds

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowSizeClass
import dev.shounakmulay.devpulse.core.designsystem.components.DPButton
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextView
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextViewVariant
import dev.shounakmulay.devpulse.core.designsystem.theme.LocalDPSpacing
import dev.shounakmulay.devpulse.core.resources.stringRes
import dev.shounakmulay.devpulse.feature.feed.screens.model.UIFeed
import devpulse.core.resources.generated.resources.add_feed_action_import
import devpulse.core.resources.generated.resources.feed_empty_imported
import kotlinx.coroutines.flow.StateFlow
import org.jetbrains.compose.resources.stringResource

fun LazyListScope.feedsSection(
    pinnedAndRecentFeeds: StateFlow<List<UIFeed>>,
    isFeedLoading: Boolean,
    onNavigateToAddFeed: () -> Unit,
    onNavigateToFeedList: () -> Unit,
    onFeedClick: (UIFeed) -> Unit,
    onFeedLongClick: (UIFeed) -> Unit
) {
    stickyHeader(key = "FeedsSectionHeader") {
        FeedsSectionHeader(
            onNavigateToAddFeed = onNavigateToAddFeed,
            onNavigateToFeedList = onNavigateToFeedList
        )
    }
    item(key = "FeedsSection") {
        val pinnedAndRecentFeeds by pinnedAndRecentFeeds
            .collectAsStateWithLifecycle()

        AnimatedVisibility(visible = isFeedLoading) {
            PinnedAndRecentsGridLoading()
        }
        AnimatedVisibility(visible = !isFeedLoading && pinnedAndRecentFeeds.isEmpty()) {
            Column(
                Modifier.widthIn(max = WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(
                    LocalDPSpacing.current.sm,
                    Alignment.CenterVertically
                )
            ) {
                DPTextView(
                    text = stringResource(stringRes.feed_empty_imported),
                    variant = DPTextViewVariant.TitleMedium
                )
                DPButton(
                    text = stringResource(stringRes.add_feed_action_import),
                    onClick = onNavigateToAddFeed
                )
            }
        }
        AnimatedVisibility(visible = !isFeedLoading && pinnedAndRecentFeeds.isNotEmpty()) {
            PinnedAndRecentsGrid(
                pinnedAndRecentFeeds = pinnedAndRecentFeeds,
                onFeedClick = onFeedClick,
                onFeedLongClick = onFeedLongClick
            )
        }
    }
}