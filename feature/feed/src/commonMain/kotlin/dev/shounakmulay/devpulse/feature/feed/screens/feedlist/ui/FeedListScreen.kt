package dev.shounakmulay.devpulse.feature.feed.screens.feedlist.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.AppBarWithSearch
import androidx.compose.material3.ExpandedFullScreenContainedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberContainedSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.window.core.layout.WindowSizeClass
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextView
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextViewVariant
import dev.shounakmulay.devpulse.core.designsystem.theme.LocalDPSpacing
import dev.shounakmulay.devpulse.core.navigation.Navigator
import dev.shounakmulay.devpulse.core.navigation.Screen
import dev.shounakmulay.devpulse.core.resources.stringRes
import dev.shounakmulay.devpulse.core.ui.button.DPBackNavigationIconButton
import dev.shounakmulay.devpulse.core.ui.image.DPFeedImage
import dev.shounakmulay.devpulse.core.ui.screen.Screen
import dev.shounakmulay.devpulse.core.ui.text.asAnnotatedString
import dev.shounakmulay.devpulse.core.ui.text.asString
import dev.shounakmulay.devpulse.feature.feed.model.UIFeed
import dev.shounakmulay.devpulse.feature.feed.screens.feedlist.ui.components.AppendErrorRow
import dev.shounakmulay.devpulse.feature.feed.screens.feedlist.ui.components.AppendLoadingRow
import dev.shounakmulay.devpulse.feature.feed.screens.feedlist.ui.components.EmptyFeedList
import dev.shounakmulay.devpulse.feature.feed.screens.feedlist.ui.components.FeedListError
import dev.shounakmulay.devpulse.feature.feed.screens.feedlist.ui.components.FeedListPlaceholderRow
import dev.shounakmulay.devpulse.feature.feed.screens.feedlist.ui.components.FeedListRow
import dev.shounakmulay.devpulse.feature.feed.screens.feedlist.ui.components.FilterTabs
import devpulse.core.resources.generated.resources.feed_list_load_error
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FeedListScreen(
    viewModel: FeedListViewModel,
    navigator: Navigator,
    modifier: Modifier = Modifier,
) {
    val feeds = viewModel.uiFeedsFlow.collectAsLazyPagingItems()
    val refreshState = feeds.loadState.refresh
    val fallbackLoadError = stringResource(stringRes.feed_list_load_error)
    val textFieldState = rememberTextFieldState()
    val searchBarState = rememberContainedSearchBarState()
    val scope = rememberCoroutineScope()
    val scrollBehavior = SearchBarDefaults.enterAlwaysSearchBarScrollBehavior()
    val appBarWithSearchColors =
        SearchBarDefaults.appBarWithSearchColors(
            searchBarColors = SearchBarDefaults.containedColors(state = searchBarState)
        )

    LaunchedEffect(Unit) {
        snapshotFlow { textFieldState.text }
            .debounce(300.milliseconds)
            .collect {
                viewModel.onEvent(FeedListScreenEvent.Search(query = it.toString()))
            }
    }

    val inputField =
        @Composable {
            SearchBarDefaults.InputField(
                textFieldState = textFieldState,
                searchBarState = searchBarState,
                colors = appBarWithSearchColors.searchBarColors.inputFieldColors,
                onSearch = { },
                placeholder = {
                    Text(modifier = Modifier.clearAndSetSemantics {}, text = "Search")
                },
                leadingIcon = {
                    when (searchBarState.currentValue) {
                        SearchBarValue.Collapsed -> {
                            DPBackNavigationIconButton {
                                navigator.navigateBack()
                            }
                        }
                        SearchBarValue.Expanded -> {
                            DPBackNavigationIconButton {
                                scope.launch { searchBarState.animateToCollapsed() }
                            }
                        }
                    }
                    DPBackNavigationIconButton {
                        if (searchBarState.currentValue == SearchBarValue.Expanded) {
                            scope.launch { searchBarState.animateToCollapsed() }
                        } else {
                            navigator.navigateBack()
                        }
                    }
                },
            )
        }
    Screen(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        viewModel = viewModel,
        topAppBar = {
            AppBarWithSearch(
                modifier = Modifier.fillMaxWidth(),
                scrollBehavior = scrollBehavior,
                state = searchBarState,
                colors = appBarWithSearchColors,
                inputField = inputField,
                navigationIcon = {
                    DPBackNavigationIconButton {
                        navigator.navigateBack()
                    }
                }
            )
        },
        onEffect = {
            when (it) {
                else -> viewModel.unhandledEffect(it)
            }
        }
    ) { state ->
        ExpandedFullScreenContainedSearchBar(
            state = searchBarState,
            inputField = inputField,
            colors = appBarWithSearchColors.searchBarColors,
        ) {
            LazyColumn {
                items(state.searchResults) { searchResult ->
                    Row(
                        modifier = Modifier.padding(LocalDPSpacing.current.md),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        DPFeedImage(
                            modifier = Modifier.size(24.dp).clip(CircleShape),
                            url = searchResult.websiteImageUrl,
                            initials = searchResult.initials,
                            feedTitle = searchResult.title.asString(),
                        )
                        Spacer(Modifier.width(LocalDPSpacing.current.md))
                        Column(
                            verticalArrangement = Arrangement.Center
                        ) {
                            DPTextView(
                                text = searchResult.title.asAnnotatedString(color = MaterialTheme.colorScheme.primary),
                                variant = DPTextViewVariant.TitleMedium
                            )
                            val description =
                                searchResult.description.asAnnotatedString(color = MaterialTheme.colorScheme.primary)
                            if (description.isNotBlank()) {
                                DPTextView(
                                    text = description,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    variant = DPTextViewVariant.BodySmall
                                )
                            }
                        }
                    }
                }
            }
        }
        Column(Modifier.padding(top = LocalDPSpacing.current.sm)) {
            FilterTabs(state.selectedTab) {
                viewModel.onEvent(FeedListScreenEvent.SelectTab(it))
            }

            when (refreshState) {
                is LoadState.Error -> FeedListError(
                    message = refreshState.error.message ?: fallbackLoadError,
                    onRetry = feeds::retry,
                )

                is LoadState.NotLoading if feeds.itemCount == 0 -> EmptyFeedList()
                else -> FeedsList(
                    feeds = feeds,
                    onTogglePinned = { feed, pinned ->
                        viewModel.onEvent(
                            FeedListScreenEvent.TogglePinned(
                                id = feed.id,
                                pinned = pinned,
                            )
                        )
                    },
                    onFeedItemClick = {
                        navigator.replaceOfSameType(
                            Screen.Tabs.Feed.FeedDetail(
                                it.id
                            )
                        )
                    }
                )
            }
        }
    }
}

@Composable
internal fun FeedsList(
    feeds: LazyPagingItems<UIFeed>,
    onTogglePinned: (UIFeed, Boolean) -> Unit,
    onFeedItemClick: (UIFeed) -> Unit
) {
    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        columns = GridCells.Adaptive((WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND / 2).dp),
        contentPadding = PaddingValues(LocalDPSpacing.current.md),
    ) {
        items(
            count = feeds.itemCount,
            key = { index -> feeds[index]?.id?.value ?: "feed-placeholder-$index" },
        ) { index ->
            val feed = feeds[index]
            if (feed == null) {
                FeedListPlaceholderRow()
            } else {
                FeedListRow(
                    feed = feed,
                    onTogglePinned = {
                        onTogglePinned(feed, it)
                    },
                    onClick = {
                        onFeedItemClick(feed)
                    }
                )
            }
        }

        when (feeds.loadState.append) {
            is LoadState.Loading -> item(key = "append-loading") {
                AppendLoadingRow()
            }

            is LoadState.Error -> item(key = "append-error") {
                AppendErrorRow(onRetry = feeds::retry)
            }

            is LoadState.NotLoading -> Unit
        }
    }
}
