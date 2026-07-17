package dev.shounakmulay.devpulse.core.data.db.query

data class FeedPostQuery(
    val filters: Set<FeedPostFilter> = emptySet(),
    val sort: FeedPostSort = FeedPostSort.PublishedNewest,
    val cursor: FeedPostCursor? = null,
    val pageSize: FeedPostPageSize = FeedPostPageSize(),
    val coverageProfile: FeedPostQueryCoverageProfile? = null
)

data class FeedPostPageSize(
    val requested: Int = Default
) {
    init {
        require(requested in Min..Max) {
            "Feed post page size must be between $Min and $Max. requested=$requested"
        }
    }

    companion object {
        const val Min = 1
        const val Default = 30
        const val Max = 100
    }
}

data class FeedPostLongRange(
    val min: Long? = null,
    val max: Long? = null
)

data class FeedPostCursor(
    val sort: FeedPostSort,
    val values: List<FeedPostCursorValue>
)

sealed interface FeedPostCursorValue {
    val type: FeedPostCursorValueType

    data class LongValue(
        val value: Long
    ) : FeedPostCursorValue {
        override val type = FeedPostCursorValueType.Long
    }

    data class TextValue(
        val value: String
    ) : FeedPostCursorValue {
        override val type = FeedPostCursorValueType.Text
    }

    data class BooleanValue(
        val value: Boolean
    ) : FeedPostCursorValue {
        override val type = FeedPostCursorValueType.Boolean
    }
}

enum class FeedPostCursorValueType {
    Long,
    Text,
    Boolean
}
