package dev.shounakmulay.devpulse.feature.feed.screens.addfeed.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.shounakmulay.devpulse.core.designsystem.components.DPTopAppBar
import dev.shounakmulay.devpulse.core.designsystem.theme.LocalDPSpacing
import dev.shounakmulay.devpulse.core.navigation.Navigator
import dev.shounakmulay.devpulse.core.resources.stringRes
import dev.shounakmulay.devpulse.core.ui.button.DPBackNavigationIconButton
import dev.shounakmulay.devpulse.core.ui.screen.Screen
import dev.shounakmulay.devpulse.feature.feed.screens.addfeed.ui.components.ImportButton
import dev.shounakmulay.devpulse.feature.feed.screens.addfeed.ui.components.ImportOpmlBottomSheet
import dev.shounakmulay.devpulse.feature.feed.screens.addfeed.ui.components.addNewCard
import dev.shounakmulay.devpulse.feature.feed.screens.addfeed.ui.components.feedsHeaderDivider
import dev.shounakmulay.devpulse.feature.feed.screens.addfeed.ui.components.importFeedItems
import dev.shounakmulay.devpulse.feature.feed.screens.addfeed.ui.components.statusHeader
import dev.shounakmulay.devpulse.feature.feed.screens.addfeed.ui.components.statusList
import devpulse.core.resources.generated.resources.add_feed_title
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AddFeedScreen(
    viewModel: AddFeedViewModel,
    navigator: Navigator,
) {
    var opmlBottomSheetVisible by remember {
        mutableStateOf(false)
    }
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
            when (it) {
                is AddFeedScreenEffect.SetAddOpmlBottomSheetVisible -> {
                    opmlBottomSheetVisible = it.visible
                }

                else -> viewModel.unhandledEffect(it)
            }
        },
    ) { state ->
        val isList by remember(state.addFeedDataList.size) {
            derivedStateOf {
                state.addFeedDataList.size > 1
            }
        }
        LazyVerticalGrid(
            modifier = Modifier
                .widthIn(min = 300.dp, max = 950.dp)
                .padding(LocalDPSpacing.current.sm)
                .align(Alignment.TopCenter),
            columns = GridCells.Adaptive(300.dp),
            contentPadding = PaddingValues(bottom = 64.dp),
            verticalArrangement = Arrangement.Center,
            horizontalArrangement = Arrangement.Center
        ) {
            statusHeader(
                visible = state.processingData.isNotEmpty(),
                onOpenQueue = {},
                onRetryFailedImports = {
                    viewModel.onEvent(AddFeedScreenEvent.RetryFailedImports)
                },
                onClearAll = {
                    viewModel.onEvent(AddFeedScreenEvent.ClearAllStatus)
                }
            )
            statusList(
                processingData = state.processingData,
                onMoveFailedImportToEdit = {
                    viewModel.onEvent(AddFeedScreenEvent.MoveFailedImportToEdit(it))
                }
            )
            feedsHeaderDivider(
                toggleAllVisible = state.addFeedDataList.size > 1,
                onToggleCollapseAll = { viewModel.onEvent(AddFeedScreenEvent.ToggleCollapseAll) },
                onOpenImportOpmlView = { viewModel.onEvent(AddFeedScreenEvent.OpenImportOpmlBottomSheet) }
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

        if (opmlBottomSheetVisible) {
            ImportOpmlBottomSheet(
                opmlUrl = state.opmlUrl,
                opmlImportError = state.opmlImportError,
                isOpmlLoading = state.isOpmlLoading,
                onExtractOpml = { viewModel.onEvent(AddFeedScreenEvent.ExtractOpmlFeedUrl) },
                onUrlChanged = { viewModel.onEvent(AddFeedScreenEvent.UpdateOpmlUrl(it)) },
                onDismissRequest = { opmlBottomSheetVisible = false }
            )
        }
    }
}


