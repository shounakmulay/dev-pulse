package dev.shounakmulay.devpulse.feature.feed.components.postFilterSort.filter.feedList

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.rememberViewModelStoreOwner
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import dev.shounakmulay.devpulse.core.designsystem.components.DPIconButton
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextField
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextFieldVariant
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextView
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextViewVariant
import dev.shounakmulay.devpulse.core.designsystem.icon.DPIcons
import dev.shounakmulay.devpulse.core.designsystem.theme.LocalDPSpacing
import dev.shounakmulay.devpulse.core.domain.models.post.RssPostFilter
import dev.shounakmulay.devpulse.core.resources.stringRes
import dev.shounakmulay.devpulse.core.ui.bottomsheet.DPModalBottomSheet
import dev.shounakmulay.devpulse.core.ui.bottomsheet.DPModalBottomSheetController
import dev.shounakmulay.devpulse.feature.feed.model.UIFeed
import devpulse.core.resources.generated.resources.feed_search
import devpulse.core.resources.generated.resources.feed_search_clear
import devpulse.core.resources.generated.resources.select_feeds
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import org.orbitmvi.orbit.compose.collectAsState

@Composable
internal fun FeedFilterFeedsBottomSheet(
    feedListBottomSheetController: DPModalBottomSheetController,
    filter: RssPostFilter.FeedIds,
    onFilterUpdated: (RssPostFilter) -> Unit
) {
    DPModalBottomSheet(controller = feedListBottomSheetController) {
        val scopedViewModelOwner = rememberViewModelStoreOwner()
        CompositionLocalProvider(LocalViewModelStoreOwner provides scopedViewModelOwner) {
            val viewModel = koinViewModel<FeedListBottomSheetViewModel> {
                parametersOf(filter.values)
            }
            val feeds = viewModel.feeds.collectAsLazyPagingItems()
            val state by viewModel.collectAsState()
            val listState = rememberLazyListState()
            val keyboardController = LocalSoftwareKeyboardController.current
            val onSearchTermChanged: (String) -> Unit = {
                listState.requestScrollToItem(0)
                viewModel.onEvent(FeedListBottomSheetEvent.OnSearchTermChanged(it))
            }
            BoxWithConstraints(Modifier.fillMaxWidth()) {
                Column(Modifier.fillMaxWidth().height(maxHeight * 0.75f)) {
                    DPTextView(
                        modifier = Modifier.padding(
                            horizontal = LocalDPSpacing.current.xl,
                            vertical = LocalDPSpacing.current.xs
                        ),
                        text = stringResource(stringRes.select_feeds),
                        variant = DPTextViewVariant.TitleMedium
                    )
                    DPTextField(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = LocalDPSpacing.current.xl),
                        value = state.searchTerm,
                        onValueChange = onSearchTermChanged,
                        placeholder = stringResource(stringRes.feed_search),
                        variant = DPTextFieldVariant.Filled,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = { keyboardController?.hide() }),
                        trailingIcon = {
                            if (state.searchTerm.isNotEmpty()) {
                                DPIconButton(
                                    icon = DPIcons.Close,
                                    contentDescription = stringResource(stringRes.feed_search_clear),
                                    onClick = { onSearchTermChanged("") },
                                )
                            }
                        },
                    )
                    FeedsList(
                        modifier = Modifier.weight(1f),
                        feeds = feeds,
                        listState = listState,
                        onEvent = viewModel::onEvent,
                    )
                    FilterApplyActions(
                        onFilterUpdated = onFilterUpdated,
                        filter = filter,
                        feedListBottomSheetController = feedListBottomSheetController,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

@Composable
private fun FeedsList(
    modifier: Modifier,
    feeds: LazyPagingItems<Pair<UIFeed, Boolean>>,
    listState: LazyListState,
    onEvent: (FeedListBottomSheetEvent) -> Unit,
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        state = listState,
    ) {
        items(feeds.itemCount, feeds.itemKey { it.first.id.value }) {
            val (feed, selected) = feeds[it] ?: return@items
            FeedListItem(onEvent, feed, selected)
        }
    }
}
