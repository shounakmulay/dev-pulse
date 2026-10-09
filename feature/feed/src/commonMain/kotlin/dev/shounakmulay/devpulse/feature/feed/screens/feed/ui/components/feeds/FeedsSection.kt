package dev.shounakmulay.devpulse.feature.feed.screens.feed.ui.components.feeds

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.ui.Modifier
import dev.shounakmulay.devpulse.core.designsystem.theme.LocalDPSpacing
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.feature.feed.components.feed.EmptyFeedsImportCTA
import dev.shounakmulay.devpulse.feature.feed.components.feedOptions.FeedOptionsMenuItem
import dev.shounakmulay.devpulse.feature.feed.components.feedOptions.FeedOptionsTarget
import dev.shounakmulay.devpulse.feature.feed.model.UIFeed
import kotlinx.collections.immutable.ImmutableList

fun LazyListScope.feedsSection(
    pinnedAndRecentFeeds: ImmutableList<UIFeed>,
    isFeedLoading: Boolean,
    onNavigateToAddFeed: () -> Unit,
    onNavigateToFeedList: () -> Unit,
    onFeedClick: (UIFeed) -> Unit,
    onFeedLongClick: (UIFeed) -> Unit,
    selectedOptions: FeedOptionsTarget?,
    onDismissOptions: (UUID) -> Unit,
    onOptionSelected: (UUID, FeedOptionsMenuItem) -> Unit,
) {
    stickyHeader(key = "FeedsSectionHeader") {
        FeedsSectionHeader(
            viewAllEnabled = pinnedAndRecentFeeds.isNotEmpty(),
            onNavigateToAddFeed = onNavigateToAddFeed,
            onNavigateToFeedList = onNavigateToFeedList
        )
    }
    item(key = "FeedsSection") {
        val enter = fadeIn()
        val exit = fadeOut()
        Box {
            AnimatedVisibility(visible = isFeedLoading, enter = enter, exit = exit) {
                PinnedAndRecentsGridLoading()
            }
            AnimatedVisibility(
                visible = !isFeedLoading && pinnedAndRecentFeeds.isEmpty(),
                enter = enter,
                exit = exit
            ) {
                EmptyFeedsImportCTA(
                    modifier = Modifier.fillMaxWidth().padding(LocalDPSpacing.current.xl),
                    onNavigateToAddFeed = onNavigateToAddFeed
                )
            }
            AnimatedVisibility(
                visible = !isFeedLoading && pinnedAndRecentFeeds.isNotEmpty(),
                enter = enter,
                exit = exit
            ) {
                PinnedAndRecentsGrid(
                    pinnedAndRecentFeeds = pinnedAndRecentFeeds,
                    onFeedClick = onFeedClick,
                    onFeedLongClick = onFeedLongClick,
                    selectedOptions = selectedOptions,
                    onDismissOptions = onDismissOptions,
                    onOptionSelected = onOptionSelected,
                )
            }
        }
    }
}
