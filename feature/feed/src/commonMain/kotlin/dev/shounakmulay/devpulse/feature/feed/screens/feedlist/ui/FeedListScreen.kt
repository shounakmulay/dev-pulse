package dev.shounakmulay.devpulse.feature.feed.screens.feedlist.ui

import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.paging.compose.collectAsLazyPagingItems
import dev.shounakmulay.devpulse.core.designsystem.components.DPSearchTopAppBar
import dev.shounakmulay.devpulse.core.navigation.Navigator
import dev.shounakmulay.devpulse.core.navigation.Screen
import dev.shounakmulay.devpulse.core.navigation.callbacks.OnTabReselect
import dev.shounakmulay.devpulse.core.resources.stringRes
import dev.shounakmulay.devpulse.core.ui.button.DPBackNavigationIconButton
import dev.shounakmulay.devpulse.core.ui.feedback.LocalSnackbarController
import dev.shounakmulay.devpulse.core.ui.list.ScrollToTopFAB
import dev.shounakmulay.devpulse.core.ui.sharing.rememberSharingService
import dev.shounakmulay.devpulse.core.ui.text.resolve
import dev.shounakmulay.devpulse.feature.feed.components.feedOptions.FeedDeleteConfirmation
import dev.shounakmulay.devpulse.feature.feed.components.feedOptions.FeedOptionsState
import dev.shounakmulay.devpulse.feature.feed.screens.feedlist.ui.components.FeedsList
import devpulse.core.resources.generated.resources.feed_search
import org.jetbrains.compose.resources.stringResource
import dev.shounakmulay.devpulse.core.ui.screen.Screen as MviScreen

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
            DPSearchTopAppBar(
                scrollBehavior = appBarScrollBehavior,
                navigationIcon = {
                    DPBackNavigationIconButton(
                        modifier = it,
                        onNavigateBack = navigator::navigateBack
                    )
                },
                textValue = searchQuery,
                onTextValueChange = {
                    lazyGridState.requestScrollToItem(0)
                    viewModel.onEvent(FeedListScreenEvent.OnSearchQueryChanged(it))
                },
                placeholder = placeholder,
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
                navigator.navigate(Screen.Tabs.Feed.FeedDetail(it.id))
            },
            onNavigationToImportFeeds = {
                navigator.navigate(Screen.Tabs.Feed.AddFeed)
            }
        )
    }
}
