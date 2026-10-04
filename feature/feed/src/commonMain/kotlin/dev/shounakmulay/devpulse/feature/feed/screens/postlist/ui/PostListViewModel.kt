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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@KoinViewModel
class PostListViewModel(
    postSortAndFiltersFlow: Flow<Pair<RssPostSort?, List<RssPostFilter>>>,
    private val postInteractor: PostInteractor,
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
        filerAndSortFlow = combine(
            postSortAndFiltersFlow.distinctUntilChanged(),
            state.map {
                it.searchQuery
                    .trim()
                    .takeIf { query -> query.length >= 3 }
            }
                .distinctUntilChanged()
                .debounce(300.milliseconds)
        ) { (sort, filters), searchQuery ->
            sort to buildList {
                addAll(filters.filterNot { it is RssPostFilter.SearchText })
                searchQuery?.let { add(RssPostFilter.SearchText(it)) }
            }
        }
    ).cachedIn(viewModelScope)

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

}
