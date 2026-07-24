package dev.shounakmulay.devpulse.core.domain.models.post

sealed interface RssPostFilter {

    fun isEmpty(): Boolean = when (this) {
        is FeedIds -> values.isEmpty()
        is TagIdsAny -> values.isEmpty()
        is Bookmarked -> value == null
        is PinnedFeed -> value == null
        is PublishedRange -> min == null && max == null
        is HasAudio -> value == null
        is HasVideo -> value == null
        is Author -> values.isEmpty()
        is Category -> values.isEmpty()
        is SearchText -> value.isEmpty()
    }
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