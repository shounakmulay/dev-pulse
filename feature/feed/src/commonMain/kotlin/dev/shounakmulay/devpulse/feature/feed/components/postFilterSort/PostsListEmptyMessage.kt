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
import org.jetbrains.compose.resources.StringResource
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
        val (text, action) = when (entry) {
            PostListEntry.Empty.Search -> stringRes.post_search_no_results to EmptyAction(stringRes.feed_search_clear, onClearSearch)

            PostListEntry.Empty.Filtered -> stringRes.no_posts_for_filter to EmptyAction(stringRes.clear_filters, onClearFilters)

            is PostListEntry.Empty.ReadStatus -> {
                val message = if (entry.read) stringRes.post_no_read_matches else stringRes.post_no_unread_matches
                message to EmptyAction(stringRes.post_show_all, onShowAllPosts)
            }

            PostListEntry.Empty.NoPosts -> stringRes.post_no_articles to onAddFeed?.let { EmptyAction(stringRes.import_feeds, it) }
        }
        DPTextView(
            modifier = Modifier.fillMaxWidth(0.75f),
            textAlign = TextAlign.Center,
            text = stringResource(text),
            variant = DPTextViewVariant.HeadingSmallEmphasized,
        )
        if (action != null) {
            Spacer(Modifier.size(LocalDPSpacing.current.xl))
            DPButton(
                text = stringResource(action.label),
                onClick = action.onClick,
                style = DPButtonStyle.Outlined,
            )
        }
    }
}

private class EmptyAction(val label: StringResource, val onClick: () -> Unit)
