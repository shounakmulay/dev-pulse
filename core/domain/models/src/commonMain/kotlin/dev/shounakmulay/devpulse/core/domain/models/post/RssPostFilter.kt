package dev.shounakmulay.devpulse.core.domain.models.post

import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.serializers.LocalDateTimeIso8601Serializer
import kotlinx.serialization.Serializable

@Serializable
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

    @Serializable
    data class FeedIds(val values: Set<UUID>) : RssPostFilter

    @Serializable
    data class TagIdsAny(val values: Set<Int>) : RssPostFilter

    @Serializable
    data class Bookmarked(val value: Boolean?) : RssPostFilter

    @Serializable
    data class PinnedFeed(val value: Boolean?) : RssPostFilter

    @Serializable
    data class PublishedRange(
        val min: @Serializable(with = LocalDateTimeIso8601Serializer::class) LocalDateTime? = null,
        val max: @Serializable(with = LocalDateTimeIso8601Serializer::class) LocalDateTime? = null
    ) : RssPostFilter

    @Serializable
    data class HasAudio(val value: Boolean?) : RssPostFilter

    @Serializable
    data class HasVideo(val value: Boolean?) : RssPostFilter

    @Serializable
    data class Author(val values: Set<String>) : RssPostFilter

    @Serializable
    data class Category(val values: Set<String>) : RssPostFilter

    @Serializable
    data class SearchText(
        val value: String,
        val snippetLength: Int = 30,
        val highlightStart: String = "\uE000",
        val highlightEnd: String = "\uE001"
    ) : RssPostFilter
}
