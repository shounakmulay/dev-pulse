package dev.shounakmulay.devpulse.core.domain.models.post

sealed interface RssPostFilter {
    data class FeedIds(val values: Set<String>) : RssPostFilter
    data class TagIdsAny(val values: Set<Int>) : RssPostFilter
    data class Bookmarked(val value: Boolean?) : RssPostFilter
    data class PinnedFeed(val value: Boolean?) : RssPostFilter
    data class PublishedRange(
        val min: Long? = null,
        val max: Long? = null
    ) : RssPostFilter
    data class HasAudio(val value: Boolean?) : RssPostFilter
    data class HasVideo(val value: Boolean?) : RssPostFilter
    data class Author(val values: Set<String>) : RssPostFilter
    data class Category(val values: Set<String>) : RssPostFilter
    data class SearchText(val value: String) : RssPostFilter
}