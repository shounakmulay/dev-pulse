package dev.shounakmulay.devpulse.feature.feed.screens.postlist.ui

import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dev.shounakmulay.devpulse.core.domain.feed.feed.SetPostBookmarkedUseCase
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.domain.models.post.RssPostFilter
import dev.shounakmulay.devpulse.core.domain.models.post.RssPostSort
import dev.shounakmulay.devpulse.core.ui.event.EventHandler
import dev.shounakmulay.devpulse.core.ui.viewmodel.MviViewModel
import dev.shounakmulay.devpulse.feature.feed.interactor.post.PostInteractor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class PostListViewModel(
    postSortAndFiltersFlow: Flow<Pair<RssPostSort?, List<RssPostFilter>>>,
    private val postInteractor: PostInteractor,
    private val setPostBookmarkedUseCase: SetPostBookmarkedUseCase
) : MviViewModel<PostListScreenState, PostListScreenEffect>(
    PostListScreenState()
),
    EventHandler<PostListScreenEvent> {

    override fun createStateSerializer() = PostListScreenState.serializer()

    val posts = postInteractor.getPostsWithFilterAndSort(
        postSortAndFiltersFlow
    ).cachedIn(viewModelScope)

    override fun onEvent(event: PostListScreenEvent) {
        when (event) {
            is PostListScreenEvent.OnPostBookmarkChanged -> onPostBookmarkChanged(
                postId = event.postId,
                bookmarked = event.bookmarked
            )
        }
    }

    private fun onPostBookmarkChanged(postId: UUID, bookmarked: Boolean) {
        viewModelScope.launch {
            setPostBookmarkedUseCase(id = postId, bookmarked = bookmarked)
        }
    }
}
