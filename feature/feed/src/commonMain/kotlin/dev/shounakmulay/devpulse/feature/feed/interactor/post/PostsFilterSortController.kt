package dev.shounakmulay.devpulse.feature.feed.interactor.post

import dev.shounakmulay.devpulse.core.domain.models.post.RssPostFilter
import dev.shounakmulay.devpulse.core.domain.models.post.RssPostSort
import dev.shounakmulay.devpulse.feature.feed.model.UIPostSort

interface PostsFilterSortController {
    fun PostFilterSortState.updateSort(selectedSort: UIPostSort): PostFilterAndSort
    fun PostFilterAndSort.updateSort(selectedSort: UIPostSort): PostFilterAndSort
    fun PostFilterSortState.updateFilter(updatedFilter: RssPostFilter): PostFilterAndSort
    fun PostFilterAndSort.updateFilter(updatedFilter: RssPostFilter): PostFilterAndSort

    fun PostFilterSortState.clearFilters(): PostFilterAndSort

    fun PostFilterAndSort.clearFilters(): PostFilterAndSort

    fun toFiltersAndSelectedState(state: PostFilterSortState): Pair<List<RssPostFilter>, RssPostSort?>
}