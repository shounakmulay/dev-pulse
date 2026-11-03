package dev.shounakmulay.devpulse.feature.feed.components.postFilterSort

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import dev.shounakmulay.devpulse.core.designsystem.components.DPButton
import dev.shounakmulay.devpulse.core.designsystem.components.DPButtonStyle
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextView
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextViewVariant
import dev.shounakmulay.devpulse.core.designsystem.icon.DPIcons
import dev.shounakmulay.devpulse.core.designsystem.theme.LocalDPSpacing
import dev.shounakmulay.devpulse.core.resources.stringRes
import dev.shounakmulay.devpulse.core.ui.content.EmptyContentPlaceholder
import devpulse.core.resources.generated.resources.clear_filters
import devpulse.core.resources.generated.resources.feed_search_clear
import devpulse.core.resources.generated.resources.import_feeds
import devpulse.core.resources.generated.resources.no_posts_for_filter
import devpulse.core.resources.generated.resources.post_no_articles
import devpulse.core.resources.generated.resources.post_no_read_matches
import devpulse.core.resources.generated.resources.post_no_unread_matches
import devpulse.core.resources.generated.resources.post_search_no_results
import devpulse.core.resources.generated.resources.post_show_all
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun PostsListEmptyMessage(
    entry: PostListEntry.Empty,
    onClearSearch: () -> Unit,
    onShowAllPosts: () -> Unit,
    onClearFilters: () -> Unit,
    onAddFeed: (() -> Unit)? = null,
) {
    EmptyContentPlaceholder(
        modifier = Modifier.padding(top = LocalDPSpacing.current.xxl),
        icon = DPIcons.EmptyList,
    ) {
        val text = when {
            entry.search -> stringRes.post_search_no_results
            entry.filtered -> stringRes.no_posts_for_filter
            entry.readStatus == false -> stringRes.post_no_unread_matches
            entry.readStatus == true -> stringRes.post_no_read_matches
            else -> stringRes.post_no_articles
        }
        DPTextView(
            modifier = Modifier.fillMaxWidth(0.75f),
            textAlign = TextAlign.Center,
            text = stringResource(text),
            variant = DPTextViewVariant.HeadingSmallEmphasized,
        )
        if (entry.search || entry.filtered || entry.readStatus != null || onAddFeed != null) {
            Spacer(Modifier.size(LocalDPSpacing.current.xl))
            DPButton(
                text = stringResource(
                    when {
                        entry.search -> stringRes.feed_search_clear
                        entry.filtered -> stringRes.clear_filters
                        entry.readStatus != null -> stringRes.post_show_all
                        else -> stringRes.import_feeds
                    },
                ),
                onClick = {
                    when {
                        entry.search -> onClearSearch()
                        entry.filtered -> onClearFilters()
                        entry.readStatus != null -> onShowAllPosts()
                        else -> onAddFeed?.invoke()
                    }
                },
                style = DPButtonStyle.Outlined,
            )
        }
    }
}
