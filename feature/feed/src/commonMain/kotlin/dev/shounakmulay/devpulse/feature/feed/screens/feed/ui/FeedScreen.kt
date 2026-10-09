package dev.shounakmulay.devpulse.feature.feed.screens.feed.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.shounakmulay.devpulse.core.navigation.Navigator
import dev.shounakmulay.devpulse.core.navigation.Screen
import dev.shounakmulay.devpulse.core.navigation.Screen.Tabs
import dev.shounakmulay.devpulse.core.navigation.Screen.Tabs.Feed.PostList
import dev.shounakmulay.devpulse.core.navigation.callbacks.OnTabReselect
import dev.shounakmulay.devpulse.core.ui.feedback.LocalSnackbarController
import dev.shounakmulay.devpulse.core.ui.screen.Screen
import dev.shounakmulay.devpulse.core.ui.sharing.rememberSharingService
import dev.shounakmulay.devpulse.core.ui.text.resolve
import dev.shounakmulay.devpulse.feature.feed.components.feedOptions.FeedDeleteConfirmation
import dev.shounakmulay.devpulse.feature.feed.components.feedOptions.FeedOptionsState
import dev.shounakmulay.devpulse.feature.feed.screens.feed.ui.components.feeds.feedsSection
import dev.shounakmulay.devpulse.feature.feed.screens.feed.ui.components.post.postsSection

@OptIn(
    ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class
)
@Composable
fun FeedScreen(
    navigator: Navigator,
    viewModel: FeedViewModel,
) {
    val snackbarController = LocalSnackbarController.current
    val sharingService = rememberSharingService(onCopiedToClipboard = {})
    Screen(
        viewModel = viewModel,
        onEffect = {
            when (it) {
                is FeedScreenEffect.Share -> sharingService.share(text = it.text.resolve())
                is FeedScreenEffect.ShowToast -> {
                    snackbarController.showSnackbar(it.message.resolve())
                }
                else -> viewModel.unhandledEffect(it)
            }
        },
    ) { state ->
        FeedDeleteConfirmation(
            confirmation = state.feedOptions as? FeedOptionsState.ConfirmingDelete,
            onConfirm = { viewModel.onEvent(FeedScreenEvent.ConfirmDelete) },
            onDismissRequest = { viewModel.onEvent(FeedScreenEvent.DismissDelete) },
        )
        val pinnedAndRecentFeeds by viewModel.pinnedAndRecentFeeds.collectAsStateWithLifecycle()
        val recentArticles by viewModel.recentArticles.collectAsStateWithLifecycle()
        val listState = rememberLazyListState()

        OnTabReselect(navigator = navigator, tab = Tabs.Feed) {
            if (listState.firstVisibleItemIndex != 0 && listState.firstVisibleItemScrollOffset != 0) {
                listState.animateScrollToItem(0)
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            state = listState,
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            feedsSection(
                pinnedAndRecentFeeds = pinnedAndRecentFeeds,
                isFeedLoading = state.isFeedLoading,
                selectedOptions = (state.feedOptions as? FeedOptionsState.Open)?.target,
                onDismissOptions = { viewModel.onEvent(FeedScreenEvent.HideFeedOptions(it)) },
                onOptionSelected = { feedId, option ->
                    viewModel.onEvent(FeedScreenEvent.OnFeedOptionSelected(feedId, option))
                },
                onNavigateToAddFeed = { navigator.navigate(Tabs.Feed.AddFeed) },
                onNavigateToFeedList = { navigator.navigate(Tabs.Feed.FeedList) },
                onFeedClick = { navigator.navigate(Tabs.Feed.FeedDetail(it.id)) },
                onFeedLongClick = {
                    viewModel.onEvent(FeedScreenEvent.OnShowFeedOptions(it))
                },
            )

            postsSection(
                articles = recentArticles,
                isLoading = state.isArticlesLoading,
                onPostClick = {
                    navigator.navigate(Screen.Tabs.Feed.PostDetail(it.id), onRootStack = true)
                },
                onBookmarkChanged = { post, bookmarked ->
                    viewModel.onEvent(
                        FeedScreenEvent.OnPostBookmarkChanged(
                            postId = post.id,
                            bookmarked = bookmarked
                        )
                    )
                },
                onViewAll = {
                    navigator.navigate(PostList(PostList.PostListLaunchData.All))
                }
            )
//            stickyHeader {
//                DPSectionDivider(
//                    title = stringResource(stringRes.folders)
//                ) {
//                    DPButton(
//                        text = stringResource(stringRes.feed_view_all),
//                        onClick = {},
//                        variant = DPButtonVariant.Secondary,
//                        style = DPButtonStyle.Text,
//                        size = DPSize.Small
//                    )
//                }
//            }
        }
    }
}
