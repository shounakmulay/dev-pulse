package dev.shounakmulay.devpulse.feature.feed.screens.postlist.ui

import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dev.shounakmulay.devpulse.core.domain.feed.feed.SetPostBookmarkedUseCase
import dev.shounakmulay.devpulse.core.domain.models.post.RssPostFilter
import dev.shounakmulay.devpulse.core.ui.event.EventHandler
import dev.shounakmulay.devpulse.core.ui.viewmodel.MviViewModel
import dev.shounakmulay.devpulse.feature.feed.interactor.post.PostFilterAndSort
import dev.shounakmulay.devpulse.feature.feed.interactor.post.PostInteractor
import dev.shounakmulay.devpulse.feature.feed.interactor.post.PostsFilterSortController
import dev.shounakmulay.devpulse.feature.feed.interactor.post.PostsFilterSortControllerDelegate
import dev.shounakmulay.devpulse.feature.feed.model.UIPostSort
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class PostListViewModel(
    private val postInteractor: PostInteractor,
    private val setPostBookmarkedUseCase: SetPostBookmarkedUseCase
) : MviViewModel<PostListScreenState, PostListScreenEffect>(
    PostListScreenState(
        postFilterSortState = PostFilterAndSort(
            filters = persistentListOf(
                RssPostFilter.FeedIds(emptySet()),
                RssPostFilter.Bookmarked(null),
                RssPostFilter.PublishedRange(),
                RssPostFilter.Category(emptySet()),
                RssPostFilter.TagIdsAny(emptySet()),
            ),
            sortValues = postInteractor.getDefaultUIPostSortValues()
        )
    )
),
    EventHandler<PostListScreenEvent>,
    PostsFilterSortController by PostsFilterSortControllerDelegate() {

    override fun createStateSerializer() = PostListScreenState.serializer()

    val posts = postInteractor.getPostsWithFilterAndSort(
        state.map(::toFiltersAndSelectedState)
    ).cachedIn(viewModelScope)

    override fun onEvent(event: PostListScreenEvent) {
        when (event) {
            is PostListScreenEvent.OnPostBookmarkChanged -> onPostBookmarkChanged(
                postId = event.postId,
                bookmarked = event.bookmarked
            )

            is PostListScreenEvent.OnFilterUpdated -> onFilterUpdated(event.filter)
            is PostListScreenEvent.OnSortUpdated -> onSortUpdated(event.sort)
            PostListScreenEvent.ClearFilters -> clearFilters()
        }
    }

    private fun clearFilters() {
        setState {
            copy(
                postFilterSortState = clearFilters()
            )
        }
    }

    private fun onSortUpdated(selectedSort: UIPostSort) {
        setState {
            copy(
                postFilterSortState = updateSort(selectedSort)
            )
        }
    }

    private fun onFilterUpdated(filter: RssPostFilter) {
        setState {
            copy(
                postFilterSortState = updateFilter(filter)
            )
        }
    }

    private fun onPostBookmarkChanged(postId: String, bookmarked: Boolean) {
        viewModelScope.launch {
            setPostBookmarkedUseCase(id = postId, bookmarked = bookmarked)
        }
    }
}
