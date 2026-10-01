package dev.shounakmulay.devpulse.feature.feed.screens.postlist.ui

import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dev.shounakmulay.devpulse.core.common.extensions.onEachSuccess
import dev.shounakmulay.devpulse.core.domain.feed.posts.SetPostBookmarkedUseCase
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.domain.models.post.RssPostFilter
import dev.shounakmulay.devpulse.core.domain.models.post.RssPostSort
import dev.shounakmulay.devpulse.core.domain.settings.feed.ObserveFeedPostListItemVariantUseCase
import dev.shounakmulay.devpulse.core.ui.event.EventHandler
import dev.shounakmulay.devpulse.core.ui.viewmodel.MviViewModel
import dev.shounakmulay.devpulse.feature.feed.interactor.post.PostInteractor
import dev.shounakmulay.devpulse.feature.feed.interactor.post.PostSearchInteractor
import dev.shounakmulay.devpulse.feature.feed.model.UIFeedPostSearchResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel

@OptIn(ExperimentalCoroutinesApi::class)
@KoinViewModel
class PostListViewModel(
    postSortAndFiltersFlow: Flow<Pair<RssPostSort?, List<RssPostFilter>>>,
    private val postInteractor: PostInteractor,
    private val postSearchInteractor: PostSearchInteractor,
    private val setPostBookmarkedUseCase: SetPostBookmarkedUseCase,
    private val observeFeedPostListItemVariantUseCase: ObserveFeedPostListItemVariantUseCase
) : MviViewModel<PostListScreenState, PostListScreenEffect>(
    PostListScreenState()
),
    EventHandler<PostListScreenEvent> {

    override fun createStateSerializer() = PostListScreenState.serializer()

    init {
        observeFeedPostListItemVariantUseCase()
            .onEachSuccess { variant ->
                if (variant != null) {
                    setState { copy(feedPostListItemVariant = variant) }
                }
            }
            .launchIn(viewModelScope)
    }

    val posts = postInteractor.getPostsWithFilterAndSort(
        postSortAndFiltersFlow
    ).cachedIn(viewModelScope)

    val postSearchResults = state.map { it.searchQuery }
        .distinctUntilChanged()
        .mapLatest(::search)
        .shareIn(viewModelScope, started = SharingStarted.WhileSubscribed(5_000), replay = 1)


    override fun onEvent(event: PostListScreenEvent) {
        when (event) {
            is PostListScreenEvent.OnSearchQueryChanged -> onSearchQueryChanged(event)

            is PostListScreenEvent.OnPostBookmarkChanged -> onPostBookmarkChanged(
                postId = event.postId,
                bookmarked = event.bookmarked
            )
        }
    }

    private fun onSearchQueryChanged(event: PostListScreenEvent.OnSearchQueryChanged) {
        setState {
            copy(searchQuery = event.query)
        }
    }

    private fun onPostBookmarkChanged(postId: UUID, bookmarked: Boolean) {
        viewModelScope.launch {
            setPostBookmarkedUseCase(id = postId, bookmarked = bookmarked)
        }
    }

    private suspend fun search(query: String): List<UIFeedPostSearchResult> {
        if (query.trim().length < 3) {
            setState {
                copy(searchLoading = false)
            }
            return emptyList()
        }

        setState { copy(searchLoading = true, searchError = null) }

        val results = postSearchInteractor.searchPosts(query)
        setState { copy(searchLoading = false) }
        return results
    }
}
