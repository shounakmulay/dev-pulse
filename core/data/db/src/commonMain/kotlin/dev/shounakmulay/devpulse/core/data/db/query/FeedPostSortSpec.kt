package dev.shounakmulay.devpulse.core.data.db.query


enum class FeedPostMissingValuePolicy {
    NonNullColumn
}

val FeedPostSort.spec: FeedPostSortSpec
    get() {
        return when (this) {
            FeedPostSort.PublishedNewest -> FeedPostSortSpec(
                sort = this,
                terms = listOf(
                    longTerm("p.publishedAtEpochMillis", FeedPostSortDirection.Descending),
                    textTerm("p.id", FeedPostSortDirection.Descending)
                )
            )
            FeedPostSort.PublishedOldest -> FeedPostSortSpec(
                sort = this,
                terms = listOf(
                    longTerm("p.publishedAtEpochMillis", FeedPostSortDirection.Ascending),
                    textTerm("p.id", FeedPostSortDirection.Ascending)
                )
            )
            FeedPostSort.TitleAtoZ -> FeedPostSortSpec(
                sort = this,
                terms = listOf(
                    textTerm("p.title", FeedPostSortDirection.Ascending),
                    textTerm("p.id", FeedPostSortDirection.Ascending)
                )
            )
            FeedPostSort.TitleZtoA -> FeedPostSortSpec(
                sort = this,
                terms = listOf(
                    textTerm("p.title", FeedPostSortDirection.Descending),
                    textTerm("p.id", FeedPostSortDirection.Descending)
                )
            )
            FeedPostSort.FeedNameAtoZ -> FeedPostSortSpec(
                sort = this,
                terms = listOf(
                    textTerm("f.name", FeedPostSortDirection.Ascending),
                    longTerm("p.publishedAtEpochMillis", FeedPostSortDirection.Descending),
                    textTerm("p.id", FeedPostSortDirection.Descending)
                )
            )
            FeedPostSort.FeedNameZtoA -> FeedPostSortSpec(
                sort = this,
                terms = listOf(
                    textTerm("f.name", FeedPostSortDirection.Descending),
                    longTerm("p.publishedAtEpochMillis", FeedPostSortDirection.Descending),
                    textTerm("p.id", FeedPostSortDirection.Descending)
                )
            )
        }
    }

private fun longTerm(
    expression: String,
    direction: FeedPostSortDirection
) = FeedPostSortTerm(
    expression = expression,
    direction = direction,
    missingValuePolicy = FeedPostMissingValuePolicy.NonNullColumn,
    cursorValueType = FeedPostCursorValueType.Long
)

private fun textTerm(
    expression: String,
    direction: FeedPostSortDirection
) = FeedPostSortTerm(
    expression = expression,
    direction = direction,
    missingValuePolicy = FeedPostMissingValuePolicy.NonNullColumn,
    cursorValueType = FeedPostCursorValueType.Text
)

private fun booleanTerm(
    expression: String,
    direction: FeedPostSortDirection
) = FeedPostSortTerm(
    expression = expression,
    direction = direction,
    missingValuePolicy = FeedPostMissingValuePolicy.NonNullColumn,
    cursorValueType = FeedPostCursorValueType.Boolean
)
