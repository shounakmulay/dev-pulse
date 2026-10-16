package dev.shounakmulay.devpulse.feature.feed.components.discovery

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import dev.shounakmulay.devpulse.core.designsystem.components.DPButton
import dev.shounakmulay.devpulse.core.designsystem.components.DPLinearProgressIndicator
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextView
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextViewVariant
import dev.shounakmulay.devpulse.core.designsystem.theme.DPTheme
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.resources.stringRes
import dev.shounakmulay.devpulse.feature.feed.model.UIDiscoveryFeed
import devpulse.core.resources.generated.resources.discovery_load_failed
import devpulse.core.resources.generated.resources.feed_action_retry
import org.jetbrains.compose.resources.stringResource

@Composable
fun DiscoveryFeedPagingList(
    feeds: LazyPagingItems<UIDiscoveryFeed>,
    pendingImportIds: Set<UUID>,
    emptyText: String,
    listState: LazyListState,
    onAddFeed: (UUID) -> Unit,
    onFeedClick: (UUID) -> Unit,
    modifier: Modifier = Modifier,
    header: (LazyListScope.() -> Unit)? = null,
) {
    LazyColumn(modifier = modifier.fillMaxSize(), state = listState) {
        header?.invoke(this)
        items(count = feeds.itemCount, key = feeds.itemKey { it.id.value }) { index ->
            feeds[index]?.let { feed ->
                DiscoveryFeedRow(
                    feed = feed,
                    pending = feed.id in pendingImportIds,
                    onAdd = { onAddFeed(feed.id) },
                    onClick = { onFeedClick(feed.id) },
                )
            }
        }
        if (feeds.loadState.refresh is LoadState.Loading || feeds.loadState.append is LoadState.Loading) {
            item { DPLinearProgressIndicator(modifier = Modifier.fillMaxWidth()) }
        }
        if (feeds.loadState.refresh is LoadState.Error || feeds.loadState.append is LoadState.Error) {
            item {
                DiscoveryMessage(stringResource(stringRes.discovery_load_failed))
                DPButton(text = stringResource(stringRes.feed_action_retry), onClick = feeds::retry)
            }
        } else if (feeds.loadState.refresh is LoadState.NotLoading && feeds.itemCount == 0) {
            item { DiscoveryMessage(emptyText) }
        }
    }
}

@Composable
fun DiscoveryMessage(text: String) {
    DPTextView(
        text = text,
        variant = DPTextViewVariant.BodyLarge,
        modifier = Modifier.padding(DPTheme.spacing.lg),
    )
}
