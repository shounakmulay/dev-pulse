package dev.shounakmulay.devpulse.feature.feed.screens.browsefeedcollection.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.paging.compose.collectAsLazyPagingItems
import dev.shounakmulay.devpulse.core.designsystem.components.DPLinearProgressIndicator
import dev.shounakmulay.devpulse.core.designsystem.components.DPSearchTopAppBar
import dev.shounakmulay.devpulse.core.designsystem.components.DPSectionDivider
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextView
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextViewVariant
import dev.shounakmulay.devpulse.core.designsystem.theme.LocalDPSpacing
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.navigation.Navigator
import dev.shounakmulay.devpulse.core.resources.stringRes
import dev.shounakmulay.devpulse.core.ui.button.DPBackNavigationIconButton
import dev.shounakmulay.devpulse.core.ui.feedback.LocalSnackbarController
import dev.shounakmulay.devpulse.core.ui.screen.Screen as MviScreen
import dev.shounakmulay.devpulse.core.ui.text.resolve
import dev.shounakmulay.devpulse.feature.feed.components.discovery.DiscoveryFeedDetailsBottomSheet
import dev.shounakmulay.devpulse.feature.feed.components.discovery.DiscoveryFeedPagingList
import dev.shounakmulay.devpulse.feature.feed.components.discovery.DiscoveryMessage
import devpulse.core.resources.generated.resources.discovery_collection_empty
import devpulse.core.resources.generated.resources.discovery_collection_removed
import devpulse.core.resources.generated.resources.discovery_no_results
import devpulse.core.resources.generated.resources.discovery_search_collection
import devpulse.core.resources.generated.resources.post_search_minimum_length
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun BrowseFeedCollectionScreen(
    viewModel: BrowseFeedCollectionViewModel,
    navigator: Navigator,
    modifier: Modifier = Modifier
) {
    val feeds = viewModel.feeds.collectAsLazyPagingItems()
    val listState = rememberLazyListState()
    val snackbarController = LocalSnackbarController.current
    val onAddFeed: (UUID) -> Unit =
        { viewModel.onEvent(BrowseFeedCollectionScreenEvent.OnAddFeed(it)) }
    val appBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    MviScreen(
        viewModel = viewModel,
        modifier = modifier.nestedScroll(appBarScrollBehavior.nestedScrollConnection),
        topAppBar = {
            DPSearchTopAppBar(
                scrollBehavior = appBarScrollBehavior,
                placeholder = stringResource(stringRes.discovery_search_collection),
                textValue = searchQuery,
                onTextValueChange = {
                    viewModel.onEvent(
                        BrowseFeedCollectionScreenEvent.OnSearchQueryChanged(
                            it
                        )
                    )
                },
                navigationIcon = { modifier ->
                    DPBackNavigationIconButton(modifier) { navigator.navigateBack() }
                },
            )
        },
        onEffect = {
            when (it) {
                is BrowseFeedCollectionScreenEffect.ShowToast -> snackbarController.showSnackbar(it.message.resolve())
                else -> viewModel.unhandledEffect(it)
            }
        },
    ) { state ->
        Column(Modifier.fillMaxSize()) {
            when (val content = state.content) {
                BrowseFeedCollectionContent.Loading -> DPLinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                BrowseFeedCollectionContent.Missing -> DiscoveryMessage(stringResource(stringRes.discovery_collection_removed))
                is BrowseFeedCollectionContent.Ready -> {
                    val collection = content.collection
                    val searching = state.searchQuery.trim().length >= 3
                    DiscoveryFeedPagingList(
                        feeds = feeds,
                        listState = listState,
                        pendingImportIds = state.pendingImportIds,
                        emptyText = stringResource(
                            if (searching) stringRes.discovery_no_results
                            else stringRes.discovery_collection_empty
                        ),
                        onAddFeed = onAddFeed,
                        onFeedClick = { viewModel.onEvent(BrowseFeedCollectionScreenEvent.OnFeedClick(it)) },
                        header = {
                            if (!searching) {
                                stickyHeader {
                                    DPSectionDivider {
                                        DPTextView(
                                            modifier = Modifier.fillMaxWidth().padding(
                                                horizontal = LocalDPSpacing.current.lg,
                                                vertical = LocalDPSpacing.current.sm,
                                            ),
                                            text = collection.title,
                                            variant = DPTextViewVariant.TitleLargeEmphasized,
                                        )
                                    }
                                }
                                item(collection.id.value) {
                                    collection.description?.takeIf(String::isNotBlank)
                                        ?.let { DiscoveryMessage(it) }
                                    if (state.searchQuery.isNotBlank()) {
                                        DiscoveryMessage(stringResource(stringRes.post_search_minimum_length))
                                    }
                                }
                            }
                        },
                    )
                }
            }
        }
        state.selectedFeedDetails?.let { feed ->
            DiscoveryFeedDetailsBottomSheet(
                feed = feed,
                pending = feed.id in state.pendingImportIds,
                onAddFeed = { viewModel.onEvent(BrowseFeedCollectionScreenEvent.OnAddFeed(feed.id)) },
                onDismissRequest = { viewModel.onEvent(BrowseFeedCollectionScreenEvent.OnClearSelectedFeed) },
            )
        }
    }
}
