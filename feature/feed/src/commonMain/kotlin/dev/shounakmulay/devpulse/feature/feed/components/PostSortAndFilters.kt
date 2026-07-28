package dev.shounakmulay.devpulse.feature.feed.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import dev.shounakmulay.devpulse.core.designsystem.theme.LocalDPSpacing
import dev.shounakmulay.devpulse.core.domain.models.post.RssPostFilter
import dev.shounakmulay.devpulse.feature.feed.components.postFilterSort.filter.filterItems
import dev.shounakmulay.devpulse.feature.feed.components.postFilterSort.sort.sortItem
import dev.shounakmulay.devpulse.feature.feed.model.UIPostSort
import org.orbitmvi.orbit.compose.collectAsState

@Composable
internal fun PostSortAndFilters(
    viewModel: PostSortAndFilterViewModel,
    onSortAndFilterDataChanged: () -> Unit
) {
    val state by viewModel.collectAsState()

    PostSortAndFilters(
        state = state,
        onSortUpdated = {
            viewModel.onEvent(PostSortAndFilterEvent.OnSortUpdated(it))
            onSortAndFilterDataChanged()
        },
        onFilterUpdated = {
            viewModel.onEvent(PostSortAndFilterEvent.OnFilterUpdated(it))
            onSortAndFilterDataChanged()
        },
        clearFilters = {
            viewModel.onEvent(PostSortAndFilterEvent.OnClearFilters)
            onSortAndFilterDataChanged()
        }
    )
}

@Composable
internal fun PostSortAndFilters(
    state: PostSortAndFilterState,
    onSortUpdated: (UIPostSort) -> Unit,
    onFilterUpdated: (RssPostFilter) -> Unit,
    clearFilters: () -> Unit
) {
    val lazyRowState = rememberLazyListState()
    LazyRow(
        state = lazyRowState,
        horizontalArrangement = Arrangement.spacedBy(LocalDPSpacing.current.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        sortItem(state.sortValues, onSortUpdated)
        filterItems(
            filters = state.filters,
            onFilterUpdated = {
                onFilterUpdated(it)
            },
            clearFilters = clearFilters
        )
    }
}
