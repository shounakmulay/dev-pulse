package dev.shounakmulay.devpulse.core.data.db.query

data class FeedPostQuery(
    val filters: Set<FeedPostFilter> = emptySet(),
    val sort: FeedPostSort = FeedPostSort.PublishedNewest,
    val cursor: FeedPostCursor? = null,
    val pageSize: Int = DEFAULT_PAGE_SIZE,
) {
    companion object {
        const val DEFAULT_PAGE_SIZE = 30
    }
}

data class FeedPostLongRange(
    val min: Long? = null,
    val max: Long? = null
)

data class FeedPostCursor(
    val id: String,
    val sort: FeedPostSort,
    val sortValue: SqlBinding
)
