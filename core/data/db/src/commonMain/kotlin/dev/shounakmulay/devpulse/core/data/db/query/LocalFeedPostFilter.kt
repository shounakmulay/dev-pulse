package dev.shounakmulay.devpulse.core.data.db.query

import dev.shounakmulay.devpulse.core.common.text.HL_END
import dev.shounakmulay.devpulse.core.common.text.HL_START

sealed interface LocalFeedPostFilter {
    data class FeedIds(val values: Set<String>) : LocalFeedPostFilter
    data class TagIdsAny(val values: Set<Int>) : LocalFeedPostFilter
    data class Bookmarked(val value: Boolean?) : LocalFeedPostFilter
    data class PinnedFeed(val value: Boolean?) : LocalFeedPostFilter
    data class PublishedRange(val range: FeedPostLongRange) : LocalFeedPostFilter
    data class UpdatedRange(val range: FeedPostLongRange) : LocalFeedPostFilter
    data class CreatedRange(val range: FeedPostLongRange) : LocalFeedPostFilter
    data class HasImage(val value: Boolean?) : LocalFeedPostFilter
    data class HasAudio(val value: Boolean?) : LocalFeedPostFilter
    data class HasVideo(val value: Boolean?) : LocalFeedPostFilter
    data class HasYouTubeData(val value: Boolean?) : LocalFeedPostFilter
    data class HasEnclosure(val value: Boolean?) : LocalFeedPostFilter
    data class Author(val values: Set<String>) : LocalFeedPostFilter
    data class SourceFeed(val values: Set<String>) : LocalFeedPostFilter
    data class Category(val values: Set<String>) : LocalFeedPostFilter
    data class SearchText(
        val value: String,
        val snippetLength: Int = 30,
        val highlightStart: String = HL_START,
        val highlightEnd: String = HL_END
    ) : LocalFeedPostFilter
}
