package dev.shounakmulay.devpulse.core.data.db.query

data class LocalFeedPostQuery(
    val filters: Set<LocalFeedPostFilter> = emptySet(),
    val sort: LocalFeedPostSort = LocalFeedPostSort.PublishedNewest,
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
    val sort: LocalFeedPostSort,
    val sortValue: SqlBinding
)
