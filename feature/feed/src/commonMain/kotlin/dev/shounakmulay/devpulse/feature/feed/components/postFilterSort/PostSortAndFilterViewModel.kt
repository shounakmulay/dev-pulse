package dev.shounakmulay.devpulse.feature.feed.components.postFilterSort

import dev.shounakmulay.devpulse.core.domain.models.post.RssPostFilter
import dev.shounakmulay.devpulse.core.navigation.Screen
import dev.shounakmulay.devpulse.core.ui.event.EventHandler
import dev.shounakmulay.devpulse.core.ui.viewmodel.MviViewModel
import dev.shounakmulay.devpulse.feature.feed.model.UIPostSort
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class PostSortAndFilterViewModel(
    val screen: Screen,
) : MviViewModel<PostSortAndFilterState, PostSortAndFilterEffect>(
    initialState = PostSortAndFilterState(screen = screen)
), EventHandler<PostSortAndFilterEvent> {
    val sortAndFiltersFlow = state.map {
        it.sortValues.firstOrNull { sort -> sort.selected }?.sort to it.filters
    }

    override fun createStateSerializer() = PostSortAndFilterState.serializer()
    override fun onEvent(event: PostSortAndFilterEvent) {
        when (event) {
            is PostSortAndFilterEvent.OnFilterUpdated -> onFilterUpdated(event.filter)
            is PostSortAndFilterEvent.OnSortUpdated -> onSortUpdated(event.sort)
            PostSortAndFilterEvent.OnClearFilters -> clearFilters()
        }
    }

    private fun onFilterUpdated(filter: RssPostFilter) {
        setState {
            copy(
                filters = filters
                    .map {
                        if (it::class == filter::class) {
                            filter
                        } else it
                    }
                    .sortedBy { it.order() }
                    .toPersistentList()
            )
        }
    }

    private fun onSortUpdated(sort: UIPostSort) {
        setState {
            copy(
                sortValues = sortValues.map {
                    it.copy(
                        selected = it == sort
                    )
                }.toPersistentList()
            )
        }
    }

    private fun clearFilters() {
        setState {
            copy(
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
    }
}