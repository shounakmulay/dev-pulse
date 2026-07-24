package dev.shounakmulay.devpulse.feature.feed.interactor.post

import dev.shounakmulay.devpulse.core.domain.models.post.RssPostFilter
import dev.shounakmulay.devpulse.core.domain.models.post.RssPostSort
import dev.shounakmulay.devpulse.feature.feed.model.UIPostSort
import kotlinx.collections.immutable.toPersistentList

class PostsFilterSortControllerDelegate : PostsFilterSortController {
    override fun PostFilterSortState.updateSort(
        selectedSort: UIPostSort
    ): PostFilterAndSort {
        return postFilterSortState.updateSort(selectedSort)
    }

    override fun PostFilterAndSort.updateSort(selectedSort: UIPostSort): PostFilterAndSort {
        return copy(
            sortValues = sortValues.map {
                it.copy(
                    selected = it == selectedSort
                )
            }.toPersistentList()
        )
    }

    override fun PostFilterSortState.updateFilter(
        updatedFilter: RssPostFilter
    ): PostFilterAndSort {
        return postFilterSortState.updateFilter(updatedFilter)

    }

    override fun PostFilterAndSort.updateFilter(
        updatedFilter: RssPostFilter
    ): PostFilterAndSort {
        return copy(
            filters = filters.map {
                if (it::class == updatedFilter::class) {
                    updatedFilter
                } else it
            }.toPersistentList()
        )
    }

    override fun PostFilterSortState.clearFilters(): PostFilterAndSort {
        return postFilterSortState.clearFilters()
    }

    override fun PostFilterAndSort.clearFilters(): PostFilterAndSort {
        return copy(
            filters = filters.map {
                when (it) {
                    is RssPostFilter.Author -> it.copy(values = emptySet())
                    is RssPostFilter.Bookmarked -> it.copy(value = null)
                    is RssPostFilter.Category -> it.copy(values = emptySet())
                    is RssPostFilter.FeedIds -> it.copy(values = emptySet())
                    is RssPostFilter.HasAudio -> it.copy(value = null)
                    is RssPostFilter.HasVideo -> it.copy(value = null)
                    is RssPostFilter.PinnedFeed -> it.copy(value = null)
                    is RssPostFilter.PublishedRange -> it.copy(min = null, max = null)
                    is RssPostFilter.SearchText -> it.copy(value = "")
                    is RssPostFilter.TagIdsAny -> it.copy(values = emptySet())
                }
            }.toPersistentList()
        )
    }

    override fun toFiltersAndSelectedState(state: PostFilterSortState): Pair<List<RssPostFilter>, RssPostSort?> {
        return with(state) {
            Pair(
                first = postFilterSortState.filters,
                second = postFilterSortState.sortValues.firstOrNull { it.selected }?.sort
            )
        }
    }

}