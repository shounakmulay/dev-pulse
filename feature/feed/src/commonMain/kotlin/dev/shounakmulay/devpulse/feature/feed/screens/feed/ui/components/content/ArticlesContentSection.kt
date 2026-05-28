package dev.shounakmulay.devpulse.feature.feed.screens.feed.ui.components.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyHorizontalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import dev.shounakmulay.devpulse.core.designsystem.components.DPButton
import dev.shounakmulay.devpulse.core.designsystem.components.DPButtonStyle
import dev.shounakmulay.devpulse.core.designsystem.components.DPButtonVariant
import dev.shounakmulay.devpulse.core.designsystem.components.DPSectionDivider
import dev.shounakmulay.devpulse.core.designsystem.theme.DPSize
import dev.shounakmulay.devpulse.core.designsystem.theme.DPTheme
import dev.shounakmulay.devpulse.core.resources.stringRes
import dev.shounakmulay.devpulse.feature.feed.screens.model.UIFeedPost
import devpulse.core.resources.generated.resources.feed_articles
import devpulse.core.resources.generated.resources.feed_view_all
import org.jetbrains.compose.resources.stringResource

fun LazyListScope.articlesContentSection(
    articles: List<UIFeedPost>,
    isLoading: Boolean,
) {
    stickyHeader {
        DPSectionDivider(
            title = stringResource(stringRes.feed_articles)
        ) {
            DPButton(
                text = stringResource(stringRes.feed_view_all),
                onClick = {},
                variant = DPButtonVariant.Secondary,
                style = DPButtonStyle.Text,
                size = DPSize.Small
            )
        }
    }
    item(key = "ArticlesContentSection") {
        val spacing = DPTheme.spacing

        val windowSizeClass = currentWindowAdaptiveInfoV2().windowSizeClass
        val gridHeight by remember(windowSizeClass) {
            derivedStateOf {
                val isLarge =
                    windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_LARGE_LOWER_BOUND)
                if (isLarge) return@derivedStateOf 3 * 172

                return@derivedStateOf 2 * 172
            }
        }
        LazyHorizontalStaggeredGrid(
            rows = StaggeredGridCells.Adaptive(160.dp),
            modifier = Modifier.height(gridHeight.dp),
            verticalArrangement = Arrangement.spacedBy(spacing.sm, Alignment.CenterVertically),
            horizontalItemSpacing = spacing.sm,
            contentPadding = PaddingValues(horizontal = spacing.md, vertical = spacing.sm),
        ) {
            itemsIndexed(
                items = articles,
                key = { index, article ->
                    article.id
                }
            ) { index, article ->
                FeedPostGridCard(
                    index = index,
                    article = article,
                    modifier = Modifier.animateItem()
                )
            }
        }
    }
}
