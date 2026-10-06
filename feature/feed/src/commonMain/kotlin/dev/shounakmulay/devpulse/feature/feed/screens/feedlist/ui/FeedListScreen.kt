package dev.shounakmulay.devpulse.feature.feed.screens.feedlist.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import dev.shounakmulay.devpulse.core.designsystem.components.DPLinearProgressIndicator
import dev.shounakmulay.devpulse.core.designsystem.components.DPSearchTopAppBar
import dev.shounakmulay.devpulse.core.designsystem.theme.LocalDPSpacing
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.navigation.Navigator
import dev.shounakmulay.devpulse.core.navigation.Screen
import dev.shounakmulay.devpulse.core.navigation.callbacks.OnTabReselect
import dev.shounakmulay.devpulse.core.resources.stringRes
import dev.shounakmulay.devpulse.core.ui.button.DPBackNavigationIconButton
import dev.shounakmulay.devpulse.core.ui.grid.adaptiveColumnsCount
import dev.shounakmulay.devpulse.core.ui.list.ScrollToTopFAB
import dev.shounakmulay.devpulse.core.ui.feedback.LocalSnackbarController
import dev.shounakmulay.devpulse.core.ui.screen.Screen as MviScreen
import dev.shounakmulay.devpulse.core.ui.sharing.rememberSharingService
import dev.shounakmulay.devpulse.core.ui.text.resolve
import dev.shounakmulay.devpulse.core.ui.transition.sharedBounds
import dev.shounakmulay.devpulse.feature.feed.components.feedOptions.FeedDeleteConfirmation
import dev.shounakmulay.devpulse.feature.feed.components.feedOptions.FeedOptionsMenu
import dev.shounakmulay.devpulse.feature.feed.components.feedOptions.FeedOptionsMenuItem
import dev.shounakmulay.devpulse.feature.feed.components.feedOptions.FeedOptionsState
import dev.shounakmulay.devpulse.feature.feed.components.feedOptions.FeedOptionsTarget
import dev.shounakmulay.devpulse.feature.feed.model.UIFeed
import dev.shounakmulay.devpulse.feature.feed.screens.feedlist.ui.components.AppendErrorRow
import dev.shounakmulay.devpulse.feature.feed.screens.feedlist.ui.components.AppendLoadingRow
import dev.shounakmulay.devpulse.feature.feed.screens.feedlist.ui.components.EmptyFeedList
import dev.shounakmulay.devpulse.feature.feed.screens.feedlist.ui.components.FeedListError
import dev.shounakmulay.devpulse.feature.feed.screens.feedlist.ui.components.FeedListPlaceholderRow
import dev.shounakmulay.devpulse.feature.feed.screens.feedlist.ui.components.FeedListRow
import dev.shounakmulay.devpulse.feature.feed.screens.feedlist.ui.components.FilterTabs
import dev.shounakmulay.devpulse.feature.feed.screens.feedlist.ui.model.UISelectedTab
import devpulse.core.resources.generated.resources.feed_list_load_error
import devpulse.core.resources.generated.resources.feed_search
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FeedListScreen(
    viewModel: FeedListViewModel,
    navigator: Navigator,
    modifier: Modifier = Modifier,
) {
    val snackbarController = LocalSnackbarController.current
    val sharingService = rememberSharingService(onCopiedToClipboard = {})
    val lazyGridState = rememberLazyGridState()
    val appBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val feeds = viewModel.uiFeedsFlow.collectAsLazyPagingItems()

    OnTabReselect(navigator = navigator, tab = Screen.Tabs.Feed) {
        lazyGridState.animateScrollToItem(0)
    }

    MviScreen(
        modifier = modifier.nestedScroll(appBarScrollBehavior.nestedScrollConnection),
        viewModel = viewModel,
        topAppBar = {
            val placeholder = stringResource(stringRes.feed_search)
            val openSearch: () -> Unit = {
                navigator.navigate(Screen.Tabs.Feed.FeedSearch, onRootStack = true)
            }
            DPSearchTopAppBar(
                scrollBehavior = appBarScrollBehavior,
                navigationIcon = {
                    DPBackNavigationIconButton(modifier = it, onNavigateBack = navigator::navigateBack)
                },
                textValue = "",
                onTextValueChange = {},
                placeholder = placeholder,
                enabled = false,
                colors = TextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                    disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledIndicatorColor = Color.Transparent,
                ),
                inputModifier = Modifier
                    .sharedBounds(key = "feed-search-bar", clipShape = CircleShape)
                    .clip(MaterialTheme.shapes.extraExtraLarge)
                    .clickable(role = Role.Button, onClick = openSearch)
                    .clearAndSetSemantics {
                        role = Role.Button
                        contentDescription = placeholder
                        onClick {
                            openSearch()
                            true
                        }
                    },
            )
        },
        floatingActionButton = {
            ScrollToTopFAB(lazyGridState = lazyGridState, collapsedFraction = 0f) {
                appBarScrollBehavior.state.heightOffset = 0f
            }
        },
        onEffect = {
            when (it) {
                is FeedListScreenEffect.Share -> sharingService.share(text = it.text.resolve())
                is FeedListScreenEffect.ShowToast -> {
                    snackbarController.showSnackbar(it.message.resolve())
                }
                else -> viewModel.unhandledEffect(it)
            }
        },
    ) { state ->
        FeedDeleteConfirmation(
            confirmation = state.feedOptions as? FeedOptionsState.ConfirmingDelete,
            onConfirm = { viewModel.onEvent(FeedListScreenEvent.ConfirmDelete) },
            onDismissRequest = { viewModel.onEvent(FeedListScreenEvent.DismissDelete) },
        )
        FeedsList(
            feeds = feeds,
            lazyGridState = lazyGridState,
            selectedTab = state.selectedTab,
            selectedOptions = (state.feedOptions as? FeedOptionsState.Open)?.target,
            onShowOptions = { viewModel.onEvent(FeedListScreenEvent.OnShowFeedOptions(it)) },
            onDismissOptions = { viewModel.onEvent(FeedListScreenEvent.HideFeedOptions(it)) },
            onOptionSelected = { feedId, option ->
                viewModel.onEvent(FeedListScreenEvent.OnFeedOptionSelected(feedId, option))
            },
            onTabSelected = { tab ->
                if (tab != state.selectedTab) {
                    lazyGridState.requestScrollToItem(0)
                    viewModel.onEvent(FeedListScreenEvent.SelectTab(tab))
                }
            },
            onTogglePinned = { feed, pinned ->
                viewModel.onEvent(FeedListScreenEvent.TogglePinned(id = feed.id, pinned = pinned))
            },
            onFeedItemClick = {
                navigator.replaceOfSameType(Screen.Tabs.Feed.FeedDetail(it.id))
            },
        )
    }
}

@Composable
internal fun FeedsList(
    feeds: LazyPagingItems<UIFeed>,
    lazyGridState: LazyGridState,
    selectedTab: UISelectedTab,
    onTabSelected: (UISelectedTab) -> Unit,
    onTogglePinned: (UIFeed, Boolean) -> Unit,
    selectedOptions: FeedOptionsTarget?,
    onShowOptions: (UIFeed) -> Unit,
    onDismissOptions: (UUID) -> Unit,
    onOptionSelected: (UUID, FeedOptionsMenuItem) -> Unit,
    onFeedItemClick: (UIFeed) -> Unit
) {
    val fallbackLoadError = stringResource(stringRes.feed_list_load_error)
    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        state = lazyGridState,
        columns = adaptiveColumnsCount(),
        contentPadding = PaddingValues(bottom = LocalDPSpacing.current.listItemHeight),
        horizontalArrangement = Arrangement.spacedBy(LocalDPSpacing.current.md),
        verticalArrangement = Arrangement.spacedBy(LocalDPSpacing.current.md),
    ) {
        stickyHeader(key = "feed-filters") {
            Column(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background)) {
                if (feeds.loadState.refresh is LoadState.Loading) {
                    DPLinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                } else {
                    Spacer(Modifier.fillMaxWidth().height(4.dp))
                }
                FilterTabs(selectedTab = selectedTab, onClick = onTabSelected)
            }
        }

        when (val refreshState = feeds.loadState.refresh) {
            is LoadState.Error -> item(key = "refresh-error", span = { GridItemSpan(maxLineSpan) }) {
                FeedListError(
                    message = refreshState.error.message ?: fallbackLoadError,
                    onRetry = feeds::retry,
                )
            }

            is LoadState.NotLoading if feeds.itemCount == 0 ->
                item(key = "empty-feeds", span = { GridItemSpan(maxLineSpan) }) {
                    EmptyFeedList()
                }

            else -> Unit
        }

        items(
            count = feeds.itemCount,
            key = { index -> feeds.peek(index)?.id?.value ?: "feed-placeholder-$index" },
        ) { index ->
            val feed = feeds[index]
            if (feed == null) {
                FeedListPlaceholderRow()
            } else {
                Box(Modifier.fillMaxWidth().animateItem()) {
                    FeedListRow(
                        feed = feed,
                        onTogglePinned = { onTogglePinned(feed, it) },
                        onLongClick = { onShowOptions(feed) },
                        onClick = { onFeedItemClick(feed) },
                    )
                    if (selectedOptions?.feedId == feed.id) {
                        DisposableEffect(feed.id) {
                            onDispose { onDismissOptions(feed.id) }
                        }
                        FeedOptionsMenu(
                            expanded = true,
                            menuItems = selectedOptions.items,
                            onMenuItemSelected = { onOptionSelected(feed.id, it) },
                            onDismissRequest = { onDismissOptions(feed.id) },
                        )
                    }
                }
            }
        }

        when (feeds.loadState.append) {
            is LoadState.Loading -> item(key = "append-loading", span = { GridItemSpan(maxLineSpan) }) {
                AppendLoadingRow()
            }

            is LoadState.Error -> item(key = "append-error", span = { GridItemSpan(maxLineSpan) }) {
                AppendErrorRow(onRetry = feeds::retry)
            }

            is LoadState.NotLoading -> Unit
        }
    }
}
