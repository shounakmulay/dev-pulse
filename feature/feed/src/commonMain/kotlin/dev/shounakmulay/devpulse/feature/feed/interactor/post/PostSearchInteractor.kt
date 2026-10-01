package dev.shounakmulay.devpulse.feature.feed.interactor.post

import dev.shounakmulay.devpulse.core.common.coroutines.DispatcherProvider
import dev.shounakmulay.devpulse.core.common.coroutines.runCatchingOnDefault
import dev.shounakmulay.devpulse.core.domain.feed.posts.SearchPostsUseCase
import dev.shounakmulay.devpulse.core.domain.models.post.RssPostSearchHighlights
import dev.shounakmulay.devpulse.feature.feed.interactor.feed.FeedInteractor
import dev.shounakmulay.devpulse.feature.feed.model.UIFeedPostSearchHighlights
import dev.shounakmulay.devpulse.feature.feed.model.UIFeedPostSearchResult
import dev.shounakmulay.devpulse.feature.feed.text.parseFtsHighlightedText
import org.koin.core.annotation.Factory

@Factory
class PostSearchInteractor(
    private val feedInteractor: FeedInteractor,
    private val postInteractor: PostInteractor,
    private val searchPostsUseCase: SearchPostsUseCase,
    private val dispatcherProvider: DispatcherProvider
) {

    suspend fun searchPosts(query: String) =
        dispatcherProvider.runCatchingOnDefault {
            val searchResults = searchPostsUseCase(query).getOrElse { emptyList() }

            searchResults.map {
                UIFeedPostSearchResult(
                    post = postInteractor.toUIFeedPost(it),
                    feed = feedInteractor.toUIFeed(it.feedIdentity),
                    search = it.search?.let { searchHighlights ->
                        toUIFeedPostSearchHighlights(
                            searchHighlights
                        )
                    },
                )
            }
        }.getOrElse { emptyList() }

    private fun toUIFeedPostSearchHighlights(searchHighlights: RssPostSearchHighlights): UIFeedPostSearchHighlights {
        return UIFeedPostSearchHighlights(
            highlightedTitle = searchHighlights.highlightedTitle?.let { parseFtsHighlightedText(it) },
            highlightedDescription = searchHighlights.highlightedDescription?.let {
                parseFtsHighlightedText(
                    it
                )
            },
            highlightedContent = searchHighlights.highlightedContent?.let {
                parseFtsHighlightedText(
                    it
                )
            },
        )
    }
}
