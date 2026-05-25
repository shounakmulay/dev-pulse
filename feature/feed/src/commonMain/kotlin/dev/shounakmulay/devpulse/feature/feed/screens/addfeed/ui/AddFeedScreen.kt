package dev.shounakmulay.devpulse.feature.feed.screens.addfeed.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.shounakmulay.devpulse.core.designsystem.components.DPTopAppBar
import dev.shounakmulay.devpulse.core.navigation.Navigator
import dev.shounakmulay.devpulse.core.resources.stringRes
import dev.shounakmulay.devpulse.core.ui.button.DPBackNavigationIconButton
import dev.shounakmulay.devpulse.core.ui.screen.Screen
import dev.shounakmulay.devpulse.feature.feed.screens.addfeed.ui.components.ImportButton
import dev.shounakmulay.devpulse.feature.feed.screens.addfeed.ui.components.addNewCard
import dev.shounakmulay.devpulse.feature.feed.screens.addfeed.ui.components.feedsHeaderDivider
import dev.shounakmulay.devpulse.feature.feed.screens.addfeed.ui.components.importFeedItems
import dev.shounakmulay.devpulse.feature.feed.screens.addfeed.ui.components.statusHeader
import dev.shounakmulay.devpulse.feature.feed.screens.addfeed.ui.components.statusList
import devpulse.core.resources.generated.resources.add_feed_title
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AddFeedScreen(
    viewModel: AddFeedViewModel,
    navigator: Navigator,
    modifier: Modifier = Modifier,
) {
    Screen(
        viewModel = viewModel,
        topAppBar = {
            DPTopAppBar(
                title = stringResource(stringRes.add_feed_title),
                navigationIcon = {
                    DPBackNavigationIconButton {
                        navigator.navigateBack()
                    }
                }
            )
        },
        onEffect = {

        },
    ) { state ->
        val isList by remember(state.addFeedDataList.size) {
            derivedStateOf {
                state.addFeedDataList.size > 1
            }
        }
        LazyVerticalGrid(
            columns = GridCells.Adaptive(300.dp),
            contentPadding = PaddingValues(bottom = 64.dp),
            verticalArrangement = Arrangement.Center,
            horizontalArrangement = Arrangement.Center
        ) {
            statusHeader(
                visible = state.processingData.isNotEmpty(),
                title = "STATUS",
                onOpenQueue = {},
                onRetryFailedImports = {},
                onClearAll = {
                    viewModel.onEvent(AddFeedScreenEvent.ClearAllStatus)
                })
            statusList(processingData = state.processingData)
            feedsHeaderDivider(
                toggleAllVisible = state.addFeedDataList.size > 1,
                title = "FEEDS",
                onToggleCollapseAll = { viewModel.onEvent(AddFeedScreenEvent.ToggleCollapseAll) },
            )
            importFeedItems(
                addFeedDataList = state.addFeedDataList,
                isList = isList,
                viewModel = viewModel
            )
            addNewCard {
                viewModel.onEvent(
                    AddFeedScreenEvent.Add
                )
            }
        }

        ImportButton {
            viewModel.onEvent(AddFeedScreenEvent.ImportFeeds)
        }
    }
}
