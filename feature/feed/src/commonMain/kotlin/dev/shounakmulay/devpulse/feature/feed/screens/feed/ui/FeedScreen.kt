package dev.shounakmulay.devpulse.feature.feed.screens.feed.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AppBarWithSearch
import androidx.compose.material3.ExpandedFullScreenContainedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.rememberContainedSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.shounakmulay.devpulse.core.designsystem.components.DPClickableRow
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextView
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextViewVariant
import dev.shounakmulay.devpulse.core.navigation.Navigator
import dev.shounakmulay.devpulse.core.navigation.Screen.Tabs
import dev.shounakmulay.devpulse.core.resources.stringRes
import dev.shounakmulay.devpulse.core.ui.screen.Screen
import dev.shounakmulay.devpulse.feature.feed.screens.feed.ui.components.content.articlesContentSection
import dev.shounakmulay.devpulse.feature.feed.screens.feed.ui.components.feeds.feedsSection
import devpulse.core.resources.generated.resources.feed_detail_title
import devpulse.core.resources.generated.resources.feed_search
import devpulse.core.resources.generated.resources.feed_search_back
import devpulse.core.resources.generated.resources.feed_search_result
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@OptIn(
    ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class
)
@Composable
fun FeedScreen(
    navigator: Navigator,
    viewModel: FeedViewModel,
) {
    val textFieldState = rememberTextFieldState()
    val searchBarState = rememberContainedSearchBarState()
    val scope = rememberCoroutineScope()
    val searchText = stringResource(stringRes.feed_search)
    val searchBackContentDescription = stringResource(stringRes.feed_search_back)
    val scrollBehavior = SearchBarDefaults.enterAlwaysSearchBarScrollBehavior(
    )
    val appBarWithSearchColors =
        SearchBarDefaults.appBarWithSearchColors(
            searchBarColors = SearchBarDefaults.containedColors(state = searchBarState)
        )
    val inputField =
        @Composable {
            SearchBarDefaults.InputField(
                textFieldState = textFieldState,
                searchBarState = searchBarState,
                colors = appBarWithSearchColors.searchBarColors.inputFieldColors,
                onSearch = { scope.launch { searchBarState.animateToCollapsed() } },
                placeholder = {
                    Text(modifier = Modifier.clearAndSetSemantics {}, text = searchText)
                },
                leadingIcon = {
                    val scope = rememberCoroutineScope()
                    AnimatedVisibility(
                        visible = searchBarState.targetValue == SearchBarValue.Expanded,
                        enter = slideInHorizontally() + fadeIn(),
                        exit = slideOutHorizontally() + fadeOut()
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = searchBackContentDescription,
                            modifier = Modifier.clickable {
                                scope.launch {
                                    searchBarState.animateToCollapsed()
                                }
                            }
                        )
                    }
                },
            )
        }
    Screen(
        viewModel = viewModel,
        topAppBar = {
            AppBarWithSearch(
                scrollBehavior = scrollBehavior,
                state = searchBarState,
                colors = appBarWithSearchColors,
                inputField = inputField,
            )
            ExpandedFullScreenContainedSearchBar(
                state = searchBarState,
                inputField = inputField,
                colors = appBarWithSearchColors.searchBarColors,
            ) {
                LazyColumn {
                    items(10) {
                        DPClickableRow(
                            onClick = { },
                            content = {
                                DPTextView(
                                    text = stringResource(stringRes.feed_search_result, it),
                                    variant = DPTextViewVariant.BodyMedium
                                )
                            },
                        )
                    }
                }
            }
        },
        onEffect = {
            when (it) {
                else -> viewModel.unhandledEffect(it)
            }
        },
    ) { state ->
        val pinnedAndRecentFeeds by viewModel.pinnedAndRecentFeeds.collectAsStateWithLifecycle()
        val recentArticles by viewModel.recentArticles.collectAsStateWithLifecycle()
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            feedsSection(
                pinnedAndRecentFeeds = pinnedAndRecentFeeds,
                isFeedLoading = state.isFeedLoading,
                onNavigateToAddFeed = { navigator.navigate(Tabs.Feed.AddFeed) },
                onNavigateToFeedList = { navigator.navigate(Tabs.Feed.FeedList) },
                onFeedClick = {},
                onFeedLongClick = { },
            )
            articlesContentSection(
                articles = recentArticles,
                isLoading = state.isArticlesLoading,
            )
        }
    }
}


@Composable
fun FeedDetailScreen(route: Tabs.Feed.FeedDetail) {
    Column {
        DPTextView(
            text = stringResource(stringRes.feed_detail_title, route.id),
            variant = DPTextViewVariant.DisplayLargeEmphasized
        )
    }
}
