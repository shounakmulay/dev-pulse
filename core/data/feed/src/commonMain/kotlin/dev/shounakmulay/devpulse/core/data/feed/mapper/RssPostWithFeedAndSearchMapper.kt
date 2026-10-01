package dev.shounakmulay.devpulse.core.data.feed.mapper

import dev.shounakmulay.devpulse.core.data.db.model.feed.projection.LocalRssPostSearchHighlights
import dev.shounakmulay.devpulse.core.data.db.model.feed.projection.LocalRssPostWithFeedAndSearch
import dev.shounakmulay.devpulse.core.domain.models.post.RssPostSearchHighlights
import dev.shounakmulay.devpulse.core.domain.models.post.RssPostWithFeedIdentityAndSearch
import org.koin.core.annotation.Factory

@Factory
class RssPostWithFeedAndSearchMapper(
    private val rssPostMapper: RssPostMapper,
    private val rssFeedMapper: RssFeedMapper
) {

    fun toRssPostWithFeedAndSearch(localPost: LocalRssPostWithFeedAndSearch): RssPostWithFeedIdentityAndSearch {
        return RssPostWithFeedIdentityAndSearch(
            post = rssPostMapper.toRssFeedPost(localPost.post),
            feedIdentity = rssFeedMapper.toRssIdentity(localPost.feed),
            search = localPost.search?.let { toRssPostSearchHighlights(it) }
        )
    }

    fun toRssPostSearchHighlights(localSearch: LocalRssPostSearchHighlights) =
        RssPostSearchHighlights(
            highlightedTitle = localSearch.highlightedTitle,
            highlightedDescription = localSearch.highlightedDescription,
            highlightedContent = localSearch.highlightedContent
        )
}