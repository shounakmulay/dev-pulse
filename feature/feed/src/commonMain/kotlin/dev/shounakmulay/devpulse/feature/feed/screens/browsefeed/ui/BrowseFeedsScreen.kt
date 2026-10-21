package dev.shounakmulay.devpulse.feature.feed.screens.browsefeed.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.paging.compose.collectAsLazyPagingItems
import dev.shounakmulay.devpulse.core.designsystem.components.DPIconButton
import dev.shounakmulay.devpulse.core.designsystem.components.DPLinearProgressIndicator
import dev.shounakmulay.devpulse.core.designsystem.components.DPSearchTopAppBar
import dev.shounakmulay.devpulse.core.designsystem.icon.DPIcons
import dev.shounakmulay.devpulse.core.designsystem.theme.DPTheme
import dev.shounakmulay.devpulse.core.navigation.Navigator
import dev.shounakmulay.devpulse.core.navigation.Screen
import dev.shounakmulay.devpulse.core.navigation.Screen.Tabs.Feed
import dev.shounakmulay.devpulse.core.resources.stringRes
import dev.shounakmulay.devpulse.core.ui.button.DPBackNavigationIconButton
import dev.shounakmulay.devpulse.core.ui.feedback.LocalSnackbarController
import dev.shounakmulay.devpulse.core.ui.grid.adaptiveRowsCount
import dev.shounakmulay.devpulse.core.ui.text.resolve
import dev.shounakmulay.devpulse.feature.feed.components.discovery.DiscoveryCollectionShelf
import dev.shounakmulay.devpulse.feature.feed.components.discovery.DiscoveryFeedDetailsBottomSheet
import dev.shounakmulay.devpulse.feature.feed.components.discovery.DiscoveryFeedPagingList
import devpulse.core.resources.generated.resources.discovery_no_results
import devpulse.core.resources.generated.resources.discovery_search_all
import devpulse.core.resources.generated.resources.import_feeds
import devpulse.core.resources.generated.resources.post_search_minimum_length
import org.jetbrains.compose.resources.stringResource
import dev.shounakmulay.devpulse.core.ui.screen.Screen as MviScreen

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun BrowseFeedsScreen(
    viewModel: BrowseFeedsViewModel,
    navigator: Navigator,
    modifier: Modifier = Modifier
) {
    val results = viewModel.feeds.collectAsLazyPagingItems()
    val normalListState = rememberLazyListState()
    val searchListState = rememberLazyListState()
    val snackbarController = LocalSnackbarController.current
    val appBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    MviScreen(
        viewModel = viewModel,
        modifier = modifier.nestedScroll(appBarScrollBehavior.nestedScrollConnection),
        topAppBar = {
            DPSearchTopAppBar(
                scrollBehavior = appBarScrollBehavior,
                placeholder = stringResource(stringRes.discovery_search_all),
                textValue = searchQuery,
                onTextValueChange = {
                    viewModel.onEvent(
                        BrowseFeedsScreenEvent.OnSearchQueryChanged(
                            it
                        )
                    )
                },
                navigationIcon = { modifier ->
                    DPBackNavigationIconButton(modifier) { navigator.navigateBack() }
                },
                actions = {
                    DPIconButton(
                        icon = DPIcons.AddLink,
                        contentDescription = stringResource(stringRes.import_feeds),
                        onClick = { navigator.navigate(Feed.AddFeed) },
                    )
                }
            )
        },
        onEffect = {
            when (it) {
                is BrowseFeedsScreenEffect.OpenCollection -> navigator.navigate(
                    Screen.Tabs.Feed.BrowseFeedCollection(
                        it.collectionId
                    )
                )

                is BrowseFeedsScreenEffect.ShowToast -> snackbarController.showSnackbar(it.message.resolve())
                else -> viewModel.unhandledEffect(it)
            }
        },
    ) { state ->
        if (state.searchQuery.isNotBlank()) {
            DiscoveryFeedPagingList(
                feeds = results,
                listState = searchListState,
                pendingImportIds = state.pendingImportIds,
                emptyText = stringResource(
                    if (state.searchQuery.trim().length < 3) stringRes.post_search_minimum_length
                    else stringRes.discovery_no_results
                ),
                onAddFeed = { viewModel.onEvent(BrowseFeedsScreenEvent.OnAddFeed(it)) },
                onFeedClick = { viewModel.onEvent(BrowseFeedsScreenEvent.OnFeedClick(it)) },
            )
        } else {
            val rowsCount = adaptiveRowsCount()
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = normalListState,
                contentPadding = PaddingValues(vertical = DPTheme.spacing.lg),
                verticalArrangement = Arrangement.spacedBy(DPTheme.spacing.xl),
            ) {
                if (state.isLoading) {
                    item { DPLinearProgressIndicator(modifier = Modifier.fillMaxWidth()) }
                }
                state.collectionShelves.forEach { shelf ->
                    DiscoveryCollectionShelf(
                        shelf = shelf,
                        rowsCount = rowsCount,
                        pendingImportIds = state.pendingImportIds,
                        onViewAll = { viewModel.onEvent(BrowseFeedsScreenEvent.OnViewAll(it)) },
                        onAddFeed = { viewModel.onEvent(BrowseFeedsScreenEvent.OnAddFeed(it)) },
                        onFeedClick = { viewModel.onEvent(BrowseFeedsScreenEvent.OnFeedClick(it.id)) }
                    )
                }
            }

        }
        state.selectedFeedDetails?.let { feed ->
            DiscoveryFeedDetailsBottomSheet(
                feed = feed,
                pending = feed.id in state.pendingImportIds,
                onAddFeed = { viewModel.onEvent(BrowseFeedsScreenEvent.OnAddFeed(feed.id)) },
                onDismissRequest = { viewModel.onEvent(BrowseFeedsScreenEvent.OnClearSelectedFeed) },
            )
        }
    }
}
