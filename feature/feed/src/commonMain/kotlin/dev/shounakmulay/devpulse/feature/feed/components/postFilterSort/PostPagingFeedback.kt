package dev.shounakmulay.devpulse.feature.feed.components.postFilterSort

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.ui.Modifier
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextView
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextViewVariant
import dev.shounakmulay.devpulse.core.designsystem.theme.LocalDPSpacing
import dev.shounakmulay.devpulse.core.resources.stringRes
import dev.shounakmulay.devpulse.feature.feed.screens.feedlist.ui.components.AppendErrorRow
import dev.shounakmulay.devpulse.feature.feed.screens.feedlist.ui.components.AppendLoadingRow
import dev.shounakmulay.devpulse.feature.feed.screens.feedlist.ui.components.FeedListError
import devpulse.core.resources.generated.resources.feed_articles_loading_content_description
import devpulse.core.resources.generated.resources.post_list_load_error
import devpulse.core.resources.generated.resources.post_search_minimum_length
import org.jetbrains.compose.resources.stringResource

internal fun LazyGridScope.postSearchHint(query: String) {
    if (query.isNotBlank() && query.trim().length < 3) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            DPTextView(
                modifier = Modifier.padding(LocalDPSpacing.current.md),
                text = stringResource(stringRes.post_search_minimum_length),
                variant = DPTextViewVariant.BodyMedium,
            )
        }
    }
}

internal fun LazyGridScope.postRefreshFeedback(posts: LazyPagingItems<PostListEntry>) {
    when (posts.loadState.refresh) {
        is LoadState.Loading -> item(span = { GridItemSpan(maxLineSpan) }) { AppendLoadingRow(stringResource(stringRes.feed_articles_loading_content_description)) }

        is LoadState.Error -> item(span = { GridItemSpan(maxLineSpan) }) {
            FeedListError(message = stringResource(stringRes.post_list_load_error), onRetry = posts::retry)
        }

        is LoadState.NotLoading -> Unit
    }
}

internal fun LazyGridScope.postAppendFeedback(posts: LazyPagingItems<PostListEntry>) {
    when (posts.loadState.append) {
        is LoadState.Loading -> item(span = { GridItemSpan(maxLineSpan) }) { AppendLoadingRow(stringResource(stringRes.feed_articles_loading_content_description)) }
        is LoadState.Error -> item(span = { GridItemSpan(maxLineSpan) }) { AppendErrorRow(onRetry = posts::retry) }
        is LoadState.NotLoading -> Unit
    }
}
