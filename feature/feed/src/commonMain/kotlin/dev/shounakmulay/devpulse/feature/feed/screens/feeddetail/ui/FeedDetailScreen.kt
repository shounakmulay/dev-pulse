package dev.shounakmulay.devpulse.feature.feed.screens.feeddetail.ui

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.paging.compose.collectAsLazyPagingItems
import dev.shounakmulay.devpulse.core.designsystem.components.DPLoadingIndicator
import dev.shounakmulay.devpulse.core.navigation.Navigator
import dev.shounakmulay.devpulse.core.navigation.Screen
import dev.shounakmulay.devpulse.core.navigation.Screen.Tabs.Feed.FeedDetail
import dev.shounakmulay.devpulse.core.navigation.callbacks.OnTabReselect
import dev.shounakmulay.devpulse.core.ui.list.ScrollToTopFAB
import dev.shounakmulay.devpulse.core.ui.feedback.LocalSnackbarController
import dev.shounakmulay.devpulse.core.ui.screen.Screen
import dev.shounakmulay.devpulse.core.ui.sharing.rememberSharingService
import dev.shounakmulay.devpulse.core.ui.text.resolve
import dev.shounakmulay.devpulse.feature.feed.components.feedOptions.FeedDeleteConfirmation
import dev.shounakmulay.devpulse.feature.feed.components.feedOptions.FeedOptionsState
import dev.shounakmulay.devpulse.feature.feed.components.postFilterSort.PostSortAndFilterEvent
import dev.shounakmulay.devpulse.feature.feed.components.postFilterSort.PostSortAndFilterViewModel
import dev.shounakmulay.devpulse.feature.feed.screens.feeddetail.ui.components.FeedDetailTopAppBar
import dev.shounakmulay.devpulse.feature.feed.screens.feeddetail.ui.components.FeedPostList

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FeedDetailScreen(
    route: FeedDetail,
    navigator: Navigator,
    postSortAndFiltersViewModel: PostSortAndFilterViewModel,
    viewModel: FeedDetailViewModel,
) {
    val snackbarController = LocalSnackbarController.current
    val posts = viewModel.posts.collectAsLazyPagingItems()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val lazyGridState = rememberLazyGridState()
    val sharingService = rememberSharingService(onCopiedToClipboard = {})

    OnTabReselect(navigator = navigator, tab = Screen.Tabs.Feed) {
        lazyGridState.animateScrollToItem(0)
    }

    Screen(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        viewModel = viewModel,
        topAppBar = {
            FeedDetailTopAppBar(
                scrollBehavior = scrollBehavior,
                lazyGridState = lazyGridState,
                navigator = navigator,
                onPinToggled = {
                    viewModel.onEvent(FeedDetailScreenEvent.OnPinToggled)
                },
                onShowOptions = {
                    uiFeed?.let { viewModel.onEvent(FeedDetailScreenEvent.OnShowFeedOptions(it)) }
                },
                onDismissOptions = { viewModel.onEvent(FeedDetailScreenEvent.HideFeedOptions(route.id)) },
                onMenuItemSelected = {
                    viewModel.onEvent(FeedDetailScreenEvent.OnFeedOptionSelected(it))
                },
            )
        },
        floatingActionButton = {
            ScrollToTopFAB(lazyGridState, scrollBehavior)
        },
        onEffect = {
            when (it) {
                is FeedDetailScreenEffect.NavigateBack -> navigator.navigateBack()
                is FeedDetailScreenEffect.ShowToast -> {
                    snackbarController.showSnackbar(it.message.resolve())
                }

                is FeedDetailScreenEffect.Share -> sharingService.share(text = it.text.resolve())
                else -> viewModel.unhandledEffect(it)
            }
        },
    ) { state ->
        FeedDeleteConfirmation(
            confirmation = state.feedOptions as? FeedOptionsState.ConfirmingDelete,
            onConfirm = { viewModel.onEvent(FeedDetailScreenEvent.ConfirmDelete) },
            onDismissRequest = { viewModel.onEvent(FeedDetailScreenEvent.DismissDelete) },
        )
        if (state.isLoading || state.feed == null || state.uiFeed == null)
            return@Screen DPLoadingIndicator(
                modifier = Modifier.align(
                    Alignment.Center
                )
            )

        val postSortAndFilterState by postSortAndFiltersViewModel.state.collectAsState()
        FeedPostList(
            lazyGridState = lazyGridState,
            uiFeed = state.uiFeed,
            feed = state.feed,
            posts = posts,
            postListItemVariant = state.feedPostListItemVariant,
            onBookmarkChanged = { selectedPost, bookmarked ->
                viewModel.onEvent(
                    FeedDetailScreenEvent.OnPostBookmarkChanged(
                        postId = selectedPost.id,
                        bookmarked = bookmarked
                    )
                )
            },
            onPostClick = {
                navigator.navigate(Screen.Tabs.Feed.PostDetail(it.id), onRootStack = true)
            },
            postSortAndFilterState = postSortAndFilterState,
            onFilterUpdated = {
                postSortAndFiltersViewModel.onEvent(PostSortAndFilterEvent.OnFilterUpdated(it))
                lazyGridState.requestScrollToItem(0)
            },
            onSortUpdated = {
                postSortAndFiltersViewModel.onEvent(PostSortAndFilterEvent.OnSortUpdated(it))
                lazyGridState.requestScrollToItem(0)
            },
            clearFilters = {
                postSortAndFiltersViewModel.onEvent(PostSortAndFilterEvent.OnClearFilters)
                lazyGridState.requestScrollToItem(0)
            },
        )
    }
}
