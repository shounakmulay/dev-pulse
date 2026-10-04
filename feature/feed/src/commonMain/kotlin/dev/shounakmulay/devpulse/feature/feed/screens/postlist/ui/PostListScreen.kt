package dev.shounakmulay.devpulse.feature.feed.screens.postlist.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.paging.compose.collectAsLazyPagingItems
import dev.shounakmulay.devpulse.core.designsystem.components.DPSearchTopAppBar
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.navigation.Navigator
import dev.shounakmulay.devpulse.core.navigation.Screen
import dev.shounakmulay.devpulse.core.navigation.callbacks.OnTabReselect
import dev.shounakmulay.devpulse.core.resources.stringRes
import dev.shounakmulay.devpulse.core.ui.button.DPBackNavigationIconButton
import dev.shounakmulay.devpulse.core.ui.list.ScrollToTopFAB
import dev.shounakmulay.devpulse.core.ui.screen.Screen
import dev.shounakmulay.devpulse.feature.feed.components.postFilterSort.PostSortAndFilterEvent
import dev.shounakmulay.devpulse.feature.feed.components.postFilterSort.PostSortAndFilterViewModel
import dev.shounakmulay.devpulse.feature.feed.model.UIFeedPost
import dev.shounakmulay.devpulse.feature.feed.screens.postlist.ui.components.PostsList
import devpulse.core.resources.generated.resources.post_search
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun PostListScreen(
    screen: Screen.Tabs.Feed.PostList,
    navigator: Navigator,
    postSortAndFilterViewModel: PostSortAndFilterViewModel = koinViewModel { parametersOf(screen) },
    viewModel: PostListViewModel = koinViewModel {
        parametersOf(postSortAndFilterViewModel.sortAndFiltersFlow)
    },
) {
    val lazyGridState = rememberLazyGridState()
    val appBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val keyboardController = LocalSoftwareKeyboardController.current

    OnTabReselect(navigator = navigator, tab = Screen.Tabs.Feed) {
        lazyGridState.animateScrollToItem(0)
    }

    LaunchedEffect(lazyGridState) {
        snapshotFlow { lazyGridState.isScrollInProgress }
            .collect { isScrolling ->
                if (isScrolling) {
                    keyboardController?.hide()
                }
            }
    }

    Screen(
        modifier = Modifier.nestedScroll(appBarScrollBehavior.nestedScrollConnection),
        viewModel = viewModel,
        topAppBar = {
            DPSearchTopAppBar(
                scrollBehavior = appBarScrollBehavior,
                placeholder = stringResource(stringRes.post_search),
                textValue = searchQuery,
                navigationIcon = {
                    DPBackNavigationIconButton(it) {
                        navigator.navigateBack()
                    }
                },
                onTextValueChange = {
                    viewModel.onEvent(PostListScreenEvent.OnSearchQueryChanged(it))
                },
            )
        },
        floatingActionButton = {
            ScrollToTopFAB(lazyGridState = lazyGridState, collapsedFraction = 0f) {
                appBarScrollBehavior.state.heightOffset = 0f
            }
        },
        onEffect = {},
    ) { state ->
        Column {
            val onBookmarkChanged: (UIFeedPost, Boolean) -> Unit = { post, bookmarked ->
                viewModel.onEvent(PostListScreenEvent.OnPostBookmarkChanged(post.id, bookmarked))
            }
            val onPostClick: (UUID) -> Unit = {
                navigator.navigate(Screen.Tabs.Feed.PostDetail(it), onRootStack = true)
            }

            val posts = viewModel.posts.collectAsLazyPagingItems()

            PostsList(
                postSortAndFilterViewModel = postSortAndFilterViewModel,
                lazyGridState = lazyGridState,
                posts = posts,
                onClearFilters = {
                    postSortAndFilterViewModel.onEvent(PostSortAndFilterEvent.OnClearFilters)
                },
                state = state,
                onBookmarkChanged = onBookmarkChanged,
                onPostClick = onPostClick,
            )
        }
    }
}
