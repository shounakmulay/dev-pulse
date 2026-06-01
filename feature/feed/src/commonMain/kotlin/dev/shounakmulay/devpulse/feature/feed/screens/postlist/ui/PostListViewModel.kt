package dev.shounakmulay.devpulse.feature.feed.screens.postlist.ui

import androidx.lifecycle.viewModelScope
import dev.shounakmulay.devpulse.core.domain.feed.feed.SetPostBookmarkedUseCase
import dev.shounakmulay.devpulse.core.ui.event.EventHandler
import dev.shounakmulay.devpulse.core.ui.viewmodel.MviViewModel
import dev.shounakmulay.devpulse.feature.feed.interactor.FeedInteractor
import dev.shounakmulay.devpulse.feature.feed.screens.model.UIFeedPost
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class PostListViewModel(
    private val feedInteractor: FeedInteractor,
    private val setPostBookmarkedUseCase: SetPostBookmarkedUseCase
) : MviViewModel<PostListScreenState, PostListScreenEffect>(),
    EventHandler<PostListScreenEvent> {
    override fun createInitialState(): PostListScreenState = PostListScreenState()

    override fun createStateSerializer() = PostListScreenState.serializer()

    val recentPosts = feedInteractor.getRecentArticlesFlow()
        .onEach {
            setState {
                copy(isLoading = false)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = persistentListOf<UIFeedPost>()
        )

    override fun onEvent(event: PostListScreenEvent) {
        when (event) {
            is PostListScreenEvent.OnPostBookmarkChanged -> onPostBookmarkChanged(
                postId = event.postId,
                bookmarked = event.bookmarked
            )
        }
    }

    private fun onPostBookmarkChanged(postId: String, bookmarked: Boolean) {
        viewModelScope.launch {
            setPostBookmarkedUseCase(id = postId, bookmarked = bookmarked)
        }
    }
}
