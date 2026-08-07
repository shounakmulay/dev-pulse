package dev.shounakmulay.devpulse.feature.feed.components

import dev.shounakmulay.devpulse.core.domain.models.post.RssPostFilter
import dev.shounakmulay.devpulse.core.navigation.Screen
import dev.shounakmulay.devpulse.core.ui.effect.Effect
import dev.shounakmulay.devpulse.core.ui.event.EventHandler
import dev.shounakmulay.devpulse.core.ui.event.ScreenEvent
import dev.shounakmulay.devpulse.core.ui.screen.ScreenState
import dev.shounakmulay.devpulse.core.ui.viewmodel.MviViewModel
import dev.shounakmulay.devpulse.feature.feed.interactor.post.PostInteractor
import dev.shounakmulay.devpulse.feature.feed.model.UIPostSort
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.koin.core.annotation.KoinViewModel

@Serializable
data class PostSortAndFilterState(
    val screen: Screen,
    @Transient
    val filters: ImmutableList<RssPostFilter> = PostInteractor.getDefaultFiltersFor(screen),
    @Transient
    val sortValues: ImmutableList<UIPostSort> = PostInteractor.DEFAULT_SORT_OPTIONS
) : ScreenState

sealed interface PostSortAndFilterEffect : Effect {

}

sealed interface PostSortAndFilterEvent : ScreenEvent {
    data class OnFilterUpdated(val filter: RssPostFilter) : PostSortAndFilterEvent
    data class OnSortUpdated(val sort: UIPostSort) : PostSortAndFilterEvent
    data object OnClearFilters : PostSortAndFilterEvent
}

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
                    .sortedWith(compareBy<RssPostFilter> {
                        it.isEmpty()
                    }.thenBy {
                        it.order()
                    })
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