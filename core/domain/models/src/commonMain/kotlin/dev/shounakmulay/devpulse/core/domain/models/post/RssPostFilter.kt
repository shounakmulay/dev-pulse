package dev.shounakmulay.devpulse.core.domain.models.post

import kotlinx.datetime.LocalDateTime

sealed interface RssPostFilter {

    fun order(): Int = when (this) {
        is PublishedRange -> 0
        is FeedIds -> 1
        is Bookmarked -> 2
        is Category -> 3
        is TagIdsAny -> 4
        is PinnedFeed -> 5
        is HasAudio -> 6
        is HasVideo -> 7
        is Author -> 8
        is SearchText -> 9
    }

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
        val min: LocalDateTime? = null,
        val max: LocalDateTime? = null
    ) : RssPostFilter

    data class HasAudio(val value: Boolean?) : RssPostFilter
    data class HasVideo(val value: Boolean?) : RssPostFilter
    data class Author(val values: Set<String>) : RssPostFilter
    data class Category(val values: Set<String>) : RssPostFilter
    data class SearchText(val value: String) : RssPostFilter
}