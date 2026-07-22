package dev.shounakmulay.devpulse.core.data.db.query

sealed interface FeedPostFilter {
    data class FeedIds(val values: Set<String>) : FeedPostFilter
    data class TagIdsAny(val values: Set<Int>) : FeedPostFilter
    data class Bookmarked(val value: Boolean) : FeedPostFilter
    data class PinnedFeed(val value: Boolean) : FeedPostFilter
    data class PublishedRange(val range: FeedPostLongRange) : FeedPostFilter
    data class UpdatedRange(val range: FeedPostLongRange) : FeedPostFilter
    data class CreatedRange(val range: FeedPostLongRange) : FeedPostFilter
    data class HasImage(val value: Boolean) : FeedPostFilter
    data class HasAudio(val value: Boolean) : FeedPostFilter
    data class HasVideo(val value: Boolean) : FeedPostFilter
    data class HasYouTubeData(val value: Boolean) : FeedPostFilter
    data class HasEnclosure(val value: Boolean) : FeedPostFilter
    data class Author(val values: Set<String>) : FeedPostFilter
    data class SourceFeed(val values: Set<String>) : FeedPostFilter
    data class Category(val values: Set<String>) : FeedPostFilter
    data class SearchText(val value: String) : FeedPostFilter
}