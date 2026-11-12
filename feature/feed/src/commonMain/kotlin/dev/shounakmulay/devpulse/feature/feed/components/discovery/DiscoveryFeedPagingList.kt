package dev.shounakmulay.devpulse.feature.feed.components.discovery

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import dev.shounakmulay.devpulse.core.designsystem.components.DPButton
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextView
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextViewVariant
import dev.shounakmulay.devpulse.core.designsystem.compose.DPComponentPreview
import dev.shounakmulay.devpulse.core.designsystem.compose.DPScreenPreview
import dev.shounakmulay.devpulse.core.designsystem.compose.Preview
import dev.shounakmulay.devpulse.core.designsystem.theme.DPTheme
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.resources.stringRes
import dev.shounakmulay.devpulse.feature.feed.preview.FeedPreviewData
import dev.shounakmulay.devpulse.feature.feed.screens.feedlist.ui.components.AppendLoadingRow
import devpulse.core.resources.generated.resources.discovery_collection_empty
import devpulse.core.resources.generated.resources.discovery_load_failed
import devpulse.core.resources.generated.resources.discovery_no_results
import devpulse.core.resources.generated.resources.feed_action_retry
import kotlinx.coroutines.flow.MutableStateFlow
import org.jetbrains.compose.resources.stringResource

@Composable
fun DiscoveryFeedPagingList(
    feeds: LazyPagingItems<DiscoveryListItem>,
    pendingImportIds: Set<UUID>,
    listState: LazyListState,
    onAddFeed: (UUID) -> Unit,
    onFeedClick: (UUID) -> Unit,
    modifier: Modifier = Modifier,
    header: (LazyListScope.() -> Unit)? = null,
) {
    LazyColumn(modifier = modifier.fillMaxSize(), state = listState) {
        header?.invoke(this)
        items(
            count = feeds.itemCount,
            key = feeds.itemKey { it.key },
        ) { index ->
            when (val entry = feeds[index]) {
                is DiscoveryListItem.Feed -> {
                    val feed = entry.feed
                    DiscoveryFeedRow(
                        feed = feed,
                        pending = feed.id in pendingImportIds,
                        onAdd = { onAddFeed(feed.id) },
                        onClick = { onFeedClick(feed.id) },
                    )
                }

                is DiscoveryListItem.Empty -> if (feeds.showEmptyEntry) {
                    val text = when (entry) {
                        DiscoveryListItem.Empty.Search -> stringRes.discovery_no_results
                        DiscoveryListItem.Empty.Collection -> stringRes.discovery_collection_empty
                    }
                    DiscoveryMessage(stringResource(text))
                }

                null -> Unit
            }
        }
        if (feeds.loadState.refresh is LoadState.Loading || feeds.loadState.append is LoadState.Loading) {
            item { AppendLoadingRow() }
        }
        if (feeds.loadState.refresh is LoadState.Error || feeds.loadState.append is LoadState.Error) {
            item {
                DiscoveryMessage(stringResource(stringRes.discovery_load_failed))
                DPButton(text = stringResource(stringRes.feed_action_retry), onClick = feeds::retry)
            }
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

@DPComponentPreview
@Composable
private fun DiscoveryFeedPagingListPreview() {
    Preview {
        DiscoveryMessage(text = "No feeds match your search")
    }
}

@DPScreenPreview
@Composable
private fun DiscoveryFeedPagingListContentPreview() {
    Preview {
        val feeds = remember {
            MutableStateFlow(PagingData.from<DiscoveryListItem>(listOf(DiscoveryListItem.Feed(FeedPreviewData.discoveryFeed))))
        }.collectAsLazyPagingItems()
        DiscoveryFeedPagingList(
            feeds = feeds,
            pendingImportIds = emptySet(),
            listState = rememberLazyListState(),
            onAddFeed = {},
            onFeedClick = {},
        )
    }
}
