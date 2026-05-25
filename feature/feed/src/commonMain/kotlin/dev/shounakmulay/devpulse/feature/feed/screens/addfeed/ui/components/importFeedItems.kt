package dev.shounakmulay.devpulse.feature.feed.screens.addfeed.ui.components

import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.itemsIndexed
import dev.shounakmulay.devpulse.feature.feed.screens.addfeed.ui.AddFeedScreenEvent
import dev.shounakmulay.devpulse.feature.feed.screens.addfeed.ui.AddFeedViewModel
import dev.shounakmulay.devpulse.feature.feed.screens.addfeed.ui.model.UIAddFeedData

fun LazyGridScope.importFeedItems(
    isList: Boolean,
    addFeedDataList: List<UIAddFeedData>,
    viewModel: AddFeedViewModel
) {
    itemsIndexed(
        addFeedDataList,
        key = { _, item ->
            item.id
        }
    ) { index, addFeedData ->
        AddFeedInputCard(
            index = index,
            data = addFeedData,
            showDelete = isList,
            onUrlChanged = { string ->
                viewModel.onEvent(
                    AddFeedScreenEvent.UpdateUrl(
                        id = addFeedData.id,
                        url = string
                    )
                )
            },
            onTitleChanged = { string ->
                viewModel.onEvent(
                    AddFeedScreenEvent.UpdateName(
                        id = addFeedData.id,
                        name = string
                    )
                )
            },
            onDelete = {
                viewModel.onEvent(
                    AddFeedScreenEvent.Delete(addFeedData.id)
                )
            },
            canToggleExpanded = isList,
            onToggleExpanded = {
                viewModel.onEvent(AddFeedScreenEvent.ToggleExpanded(addFeedData.id))
            },
            onSourceInputFocusLost = {
                viewModel.onEvent(AddFeedScreenEvent.ValidateSourceUrl(addFeedData.id))
            }
        )
    }
}