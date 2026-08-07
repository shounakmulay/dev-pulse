package dev.shounakmulay.devpulse.feature.feed.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import dev.shounakmulay.devpulse.core.designsystem.theme.LocalDPSpacing
import dev.shounakmulay.devpulse.core.domain.models.post.RssPostFilter
import dev.shounakmulay.devpulse.feature.feed.components.postFilterSort.filter.filterItems
import dev.shounakmulay.devpulse.feature.feed.components.postFilterSort.sort.sortItem
import dev.shounakmulay.devpulse.feature.feed.model.UIPostSort
import org.orbitmvi.orbit.compose.collectAsState

@Composable
internal fun PostSortAndFilters(
    viewModel: PostSortAndFilterViewModel,
    modifier: Modifier = Modifier,
    onSortAndFilterDataChanged: () -> Unit,
) {
    val state by viewModel.collectAsState()

    PostSortAndFilters(
        modifier = modifier,
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
    modifier: Modifier = Modifier,
    state: PostSortAndFilterState,
    onSortUpdated: (UIPostSort) -> Unit,
    onFilterUpdated: (RssPostFilter) -> Unit,
    clearFilters: () -> Unit
) {
    val lazyRowState = rememberLazyListState()
    val surfaceColor = MaterialTheme.colorScheme.surface

    LazyRow(
        modifier = modifier.background(
            Brush.verticalGradient(
                0.0f to surfaceColor.copy(alpha = 1f),
                0.50f to surfaceColor.copy(alpha = 0.75f),
                0.75f to surfaceColor.copy(alpha = 0.5f),
                1f to surfaceColor.copy(alpha = 0.25f),
            )
        ),
        state = lazyRowState,
        contentPadding = PaddingValues(
            horizontal = LocalDPSpacing.current.md,
        ),
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
