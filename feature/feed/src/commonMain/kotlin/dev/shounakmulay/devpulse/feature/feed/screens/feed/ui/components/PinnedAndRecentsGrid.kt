package dev.shounakmulay.devpulse.feature.feed.screens.feed.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import dev.shounakmulay.devpulse.core.resources.stringRes
import dev.shounakmulay.devpulse.core.designsystem.theme.LocalDPSpacing
import dev.shounakmulay.devpulse.feature.feed.screens.model.UIFeed
import devpulse.core.resources.generated.resources.feed_grid_loading_content_description
import org.jetbrains.compose.resources.stringResource

@Composable
fun PinnedAndRecentsGrid(
    pinnedAndRecentFeeds: List<UIFeed>,
    onFeedClick: (UIFeed) -> Unit,
    onFeedLongClick: (UIFeed) -> Unit,
) {
    val rowsCount = pinnedAndRecentsGridRowsCount()
    val spacing = LocalDPSpacing.current
    LazyHorizontalGrid(
        modifier = Modifier.heightIn(max = (rowsCount * 100).dp)
            .padding(horizontal = spacing.md, vertical = spacing.sm),
        rows = GridCells.Fixed(rowsCount),
        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
        verticalArrangement = Arrangement.spacedBy(spacing.sm)
    ) {
        items(pinnedAndRecentFeeds, key = { it.id }) { item ->
            FeedsGridItem(
                imageUrl = item.websiteImageUrl,
                title = item.title,
                initials = item.initials,
                pinned = item.pinned,
                sourceUrl = item.sourceUrl,
                onClick = { onFeedClick(item) },
                onLongClick = { onFeedLongClick(item) }
            )
        }
    }
}

@Composable
fun PinnedAndRecentsGridLoading() {
    val rowsCount = pinnedAndRecentsGridRowsCount()
    val spacing = LocalDPSpacing.current
    val loadingContentDescription = stringResource(stringRes.feed_grid_loading_content_description)
    LazyHorizontalGrid(
        modifier = Modifier
            .heightIn(max = (rowsCount * 100).dp)
            .padding(horizontal = spacing.md, vertical = spacing.sm)
            .semantics {
                contentDescription = loadingContentDescription
            },
        rows = GridCells.Fixed(rowsCount),
        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
        verticalArrangement = Arrangement.spacedBy(spacing.sm)
    ) {
        feedGridPlaceholders(count = rowsCount * 4)
    }
}

@Composable
private fun pinnedAndRecentsGridRowsCount(): Int {
    val windowAdaptiveInfo = currentWindowAdaptiveInfoV2()
    val rowsCount by remember(windowAdaptiveInfo) {
        derivedStateOf {
            val isMedium = windowAdaptiveInfo
                .windowSizeClass
                .isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)
            if (isMedium) 2 else 1
        }
    }
    return rowsCount
}

private fun LazyGridScope.feedGridPlaceholders(count: Int) {
    items(count, key = { "feed-grid-placeholder-$it" }) {
        FeedGridPlaceholderItem()
    }
}

@Composable
private fun FeedGridPlaceholderItem() {
    val spacing = LocalDPSpacing.current
    Surface(
        modifier = Modifier.width(90.dp),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
    ) {
        Surface(
            modifier = Modifier
                .padding(spacing.sm)
                .size(spacing.listItemHeight),
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            content = {},
        )
    }
}
