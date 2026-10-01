package dev.shounakmulay.devpulse.core.data.db.query

import androidx.room3.RoomRawQuery
import dev.shounakmulay.devpulse.core.logging.logger

class LocalFeedPostSqlQueryBuilder(
    private val query: LocalFeedPostQuery
) {

    private val filterClauses by lazy {
        buildWhereClauses()
    }

    fun buildInitialPage(limit: Int): RoomRawQuery {
        return buildPage(
            cursor = query.cursor,
            includeCursor = false,
            limit = limit
        )
    }

    fun buildPageAfter(
        cursor: FeedPostCursor,
        limit: Int
    ): RoomRawQuery {
        return buildPage(
            cursor = cursor,
            includeCursor = false,
            limit = limit
        )
    }

    fun buildPageBefore(
        cursor: FeedPostCursor,
        limit: Int
    ): RoomRawQuery {
        return buildPage(
            cursor = cursor,
            includeCursor = false,
            reversed = true,
            limit = limit
        )
    }

    fun buildRefreshPageAround(
        cursor: FeedPostCursor,
        limit: Int
    ): RoomRawQuery {
        return buildPage(
            cursor = cursor,
            includeCursor = true,
            limit = limit
        )
    }

    fun buildSearchResults(): RoomRawQuery {
        val search = query.filters.filterIsInstance<LocalFeedPostFilter.SearchText>().single()
        val text = FtsQuerySanitizer().sanitize(search.value)
        require(text.isNotBlank())
        require(search.snippetLength > 0)
        require(search.highlightStart.isNotEmpty() && search.highlightEnd.isNotEmpty())
        require(search.highlightStart != search.highlightEnd)

        val bindings = mutableListOf<SqlBinding>()
        fun bind(value: String) { bindings += SqlBinding.Text(value) }
        fun bind(value: Int) { bindings += SqlBinding.LongValue(value.toLong()) }

        bind(search.highlightStart)
        bind(search.highlightEnd)
        bind(search.highlightStart)
        bind(search.highlightEnd)
        bind(search.snippetLength)
        bind(search.highlightStart)
        bind(search.highlightEnd)
        bind(search.highlightStart)
        bind(search.highlightEnd)
        bind(search.snippetLength)
        bind(search.highlightStart)
        bind(search.highlightEnd)
        bind(text)
        bind(search.highlightStart)
        bind(search.highlightEnd)
        bind(search.snippetLength)
        bind(text)
        bindings += filterClauses.first
        bindings += SqlBinding.LongValue(MAX_SEARCH_RESULTS.toLong())

        val sql = buildString {
            appendLine(
                """
                WITH post_matches AS (
                    SELECT rowid AS postRowId,
                           bm25(LocalRssContentFeedPostFts, 10.0, 3.0, 1.0) AS score,
                           title, description, content,
                           highlight(LocalRssContentFeedPostFts, 0, ?, ?) AS rawTitle,
                           snippet(LocalRssContentFeedPostFts, 1, ?, ?, '...', ?) AS rawDescription,
                           highlight(LocalRssContentFeedPostFts, 1, ?, ?) AS markedDescription,
                           snippet(LocalRssContentFeedPostFts, 2, ?, ?, '...', ?) AS rawContent,
                           highlight(LocalRssContentFeedPostFts, 2, ?, ?) AS markedContent
                    FROM LocalRssContentFeedPostFts
                    WHERE LocalRssContentFeedPostFts MATCH ?
                    LIMIT -1
                ),
                post_hits AS (
                    SELECT postRowId, score,
                           CASE WHEN rawTitle != title THEN rawTitle END AS highlightedTitle,
                           CASE WHEN markedDescription != description THEN rawDescription END AS highlightedDescription,
                           CASE WHEN markedContent != content THEN rawContent END AS highlightedContent
                    FROM post_matches
                ),
                article_matches AS (
                    SELECT postId,
                           rowid AS ftsRowId,
                           bm25(LocalRssPostContentFts) AS score,
                           snippet(LocalRssPostContentFts, 1, ?, ?, '...', ?) AS rawContent
                    FROM LocalRssPostContentFts
                    WHERE LocalRssPostContentFts MATCH ?
                    LIMIT -1
                ),
                ranked_articles AS (
                    SELECT postId, rawContent, score,
                           ROW_NUMBER() OVER (PARTITION BY postId ORDER BY score, ftsRowId) AS rowNumber
                    FROM article_matches
                ),
                article_hits AS (
                    SELECT postId, rawContent, score FROM ranked_articles WHERE rowNumber = 1
                ),
                candidate_ids AS (
                    SELECT p.id FROM post_hits ph
                    JOIN LocalRssContentFeedPost p ON p.rowid = ph.postRowId
                    UNION
                    SELECT postId AS id FROM article_hits
                )
                SELECT p.*,
                       f.id AS feed_id,
                       f.title AS feed_title,
                       f.name AS feed_name,
                       f.pinned AS feed_pinned,
                       f.sourceUrl AS feed_sourceUrl,
                       f.link AS feed_link,
                       f.createdAt AS feed_createdAt,
                       f.updatedAt AS feed_updatedAt,
                       ph.highlightedTitle AS search_highlightedTitle,
                       ph.highlightedDescription AS search_highlightedDescription,
                       COALESCE(ah.rawContent, ph.highlightedContent) AS search_highlightedContent
                FROM candidate_ids candidates
                JOIN LocalRssContentFeedPost p ON p.id = candidates.id
                INNER JOIN LocalRssFeed f ON p.feedId = f.id
                LEFT JOIN post_hits ph ON ph.postRowId = p.rowid
                LEFT JOIN article_hits ah ON ah.postId = p.id
                """.trimIndent()
            )
            if (filterClauses.second.isNotEmpty()) {
                appendLine("WHERE ${filterClauses.second.joinToString(separator = " AND ")}")
            }
            appendLine("ORDER BY CASE WHEN ph.postRowId IS NULL THEN 1 ELSE 0 END,")
            appendLine("         COALESCE(ph.score, ah.score), ${buildOrderBy(query.sort)}")
            append("LIMIT ?")
        }
        return RoomRawQuery(sql = sql) { statement ->
            bindings.forEachIndexed { index, binding -> binding.bind(statement, index + 1) }
        }
    }

    private fun buildPage(
        cursor: FeedPostCursor?,
        includeCursor: Boolean,
        reversed: Boolean = false,
        limit: Int
    ): RoomRawQuery {
        require(query.filters.none { it is LocalFeedPostFilter.SearchText && FtsQuerySanitizer().sanitize(it.value).isNotBlank() })
        val filterBindings = filterClauses.first.toMutableList()
        val filterClauses = filterClauses.second.toMutableList()
        cursor?.let {
            filterClauses += buildCursorClause(
                cursor = it,
                includeCursor = includeCursor,
                reversed = reversed,
                bindings = filterBindings
            )
        }

        val orderBySql = buildOrderBy(sort = if (reversed) query.sort.reversed() else query.sort)

        val sql = buildString {
            appendProjection()
            if (filterClauses.isNotEmpty()) {
                appendLine("WHERE ${filterClauses.joinToString(separator = " AND ")}")
            }
            appendLine("ORDER BY $orderBySql")
            append("LIMIT ?")
        }
        logger().d { "[FEED-PAGING] sql: $sql" }
        filterBindings += SqlBinding.LongValue(limit.toLong())
        return RoomRawQuery(sql = sql) { statement ->
            filterBindings.forEachIndexed { index, binding ->
                binding.bind(statement, index + 1)
            }
        }
    }

    private fun buildOrderBy(sort: LocalFeedPostSort): String {
        val sortTerms = when (sort) {
            LocalFeedPostSort.PublishedNewest -> {
                listOf(
                    "${FeedColumnsSelector.PUBLISHED_AT_EPOCH_MILLIS} ${LocalFeedPostSortDirection.Descending.sql}",
                    orderByIdClause(LocalFeedPostSortDirection.Descending)
                )
            }

            LocalFeedPostSort.PublishedOldest -> {
                listOf(
                    "${FeedColumnsSelector.PUBLISHED_AT_EPOCH_MILLIS} ${LocalFeedPostSortDirection.Ascending.sql}",
                    orderByIdClause(
                        LocalFeedPostSortDirection.Ascending
                    )
                )
            }

            LocalFeedPostSort.TitleAtoZ -> {
                listOf(
                    "${FeedColumnsSelector.TITLE} ${LocalFeedPostSortDirection.Ascending.sql}",
                    orderByIdClause(LocalFeedPostSortDirection.Descending)
                )
            }

            LocalFeedPostSort.TitleZtoA -> {
                listOf(
                    "${FeedColumnsSelector.TITLE} ${LocalFeedPostSortDirection.Descending.sql}",
                    orderByIdClause(LocalFeedPostSortDirection.Descending)
                )
            }
        }
        return sortTerms.joinToString(", ")
    }

    private fun orderByIdClause(direction: LocalFeedPostSortDirection): String {
        return "${FeedColumnsSelector.ID} ${direction.sql}"
    }

    private fun StringBuilder.appendProjection() {
        appendLine(
            """
            SELECT 
                p.*,
                ${FeedColumnsSelector.FEED_ID} AS ${FeedColumnsSelector.FEED_ID_ALIAS},
                ${FeedColumnsSelector.FEED_TITLE} AS ${FeedColumnsSelector.FEED_TITLE_ALIAS},
                ${FeedColumnsSelector.FEED_NAME} AS ${FeedColumnsSelector.FEED_NAME_ALIAS},
                ${FeedColumnsSelector.FEED_PINNED} AS ${FeedColumnsSelector.FEED_PINNED_ALIAS},
                ${FeedColumnsSelector.FEED_SOURCE_URL} AS ${FeedColumnsSelector.FEED_SOURCE_URL_ALIAS},
                ${FeedColumnsSelector.FEED_LINK} AS ${FeedColumnsSelector.FEED_LINK_ALIAS},
                ${FeedColumnsSelector.FEED_CREATED_AT} AS ${FeedColumnsSelector.FEED_CREATED_AT_ALIAS},
                ${FeedColumnsSelector.FEED_UPDATED_AT} AS ${FeedColumnsSelector.FEED_UPDATED_AT_ALIAS},
                NULL AS search_highlightedTitle,
                NULL AS search_highlightedDescription,
                NULL AS search_highlightedContent
            FROM LocalRssContentFeedPost p
            INNER JOIN LocalRssFeed f ON ${FeedColumnsSelector.POST_FEED_ID} = ${FeedColumnsSelector.FEED_ID}
            """.trimIndent()
        )
    }

    private fun buildWhereClauses(): Pair<List<SqlBinding>, List<String>> {
        val bindings = mutableListOf<SqlBinding>()
        val filters = query.filters
        val clauses = mutableListOf<String>()

        for (filter in filters) {
            when (filter) {
                is LocalFeedPostFilter.Author -> {
                    if (filter.values.isNotEmpty()) {
                        clauses += "LOWER(${FeedColumnsSelector.AUTHOR}) IN (${placeholders(filter.values.size)})"
                        bindings += filter.values.bindLowercasedTextPlaceholders()
                    }
                }

                is LocalFeedPostFilter.Bookmarked -> {
                    if (filter.value == null) continue
                    clauses += "${FeedColumnsSelector.BOOKMARKED} = ?"
                    bindings += SqlBinding.BooleanValue(filter.value)
                }

                is LocalFeedPostFilter.Category -> {
                    if (filter.values.isNotEmpty()) {
                        clauses += "${FeedColumnsSelector.ID} IN (SELECT ${FeedColumnsSelector.POST_ID} FROM LocalRssPostCategory WHERE LOWER(${FeedColumnsSelector.CATEGORY}) IN (${
                            placeholders(
                                filter.values.size
                            )
                        }))"
                        bindings += filter.values.bindLowercasedTextPlaceholders()
                    }
                }

                is LocalFeedPostFilter.CreatedRange -> {
                    val (binding, clause) = filter.range.appendRangeClause(FeedColumnsSelector.CREATED_AT)
                        ?: continue
                    clauses += clause
                    bindings += binding
                }

                is LocalFeedPostFilter.FeedIds -> {
                    if (filter.values.isNotEmpty()) {
                        clauses += "${FeedColumnsSelector.POST_FEED_ID} IN (${placeholders(filter.values.size)})"
                        bindings += filter.values.sorted().map { SqlBinding.Text(it) }
                    }
                }

                is LocalFeedPostFilter.HasAudio -> {
                    if (filter.value == null) continue
                    clauses += if (filter.value) audioExistsClause() else "NOT (${audioExistsClause()})"
                }

                is LocalFeedPostFilter.HasEnclosure -> {
                    if (filter.value == null) continue
                    val clause =
                        "(${FeedColumnsSelector.RAW_ENCLOSURE_TYPE} IS NOT NULL AND ${FeedColumnsSelector.RAW_ENCLOSURE_TYPE} != '')"
                    clauses += if (filter.value) clause else "NOT ($clause)"
                }

                is LocalFeedPostFilter.HasImage -> {
                    if (filter.value == null) continue
                    clauses += if (filter.value) imageExistsClause() else "NOT (${imageExistsClause()})"
                }

                is LocalFeedPostFilter.HasVideo -> {
                    if (filter.value == null) continue
                    clauses += if (filter.value) videoExistsClause() else "NOT (${videoExistsClause()})"
                }

                is LocalFeedPostFilter.HasYouTubeData -> {
                    if (filter.value == null) continue
                    val clause =
                        "(${FeedColumnsSelector.YOUTUBE_DATA_VIDEO_ID} IS NOT NULL AND ${FeedColumnsSelector.YOUTUBE_DATA_VIDEO_ID} != '')"
                    clauses += if (filter.value) clause else "NOT ($clause)"
                }

                is LocalFeedPostFilter.PinnedFeed -> {
                    if (filter.value == null) continue
                    clauses += "${FeedColumnsSelector.FEED_PINNED} = ?"
                    bindings += SqlBinding.BooleanValue(filter.value)
                }

                is LocalFeedPostFilter.PublishedRange -> {
                    val (binding, clause) = filter.range.appendRangeClause(FeedColumnsSelector.PUBLISHED_AT_EPOCH_MILLIS)
                        ?: continue
                    clauses += clause
                    bindings += binding
                }

                is LocalFeedPostFilter.SearchText -> {
                    continue
                }

                is LocalFeedPostFilter.SourceFeed -> {
                    if (filter.values.isNotEmpty()) {
                        clauses += "LOWER(${FeedColumnsSelector.SOURCE_URL}) IN (${
                            placeholders(
                                filter.values.size
                            )
                        })"
                        bindings += filter.values.bindLowercasedTextPlaceholders()
                    }
                }

                is LocalFeedPostFilter.TagIdsAny -> {
                    if (filter.values.isNotEmpty()) {
                        clauses += "${FeedColumnsSelector.ID} IN (SELECT ${FeedColumnsSelector.POST_ID} FROM LocalRssPostToTagMapping WHERE ${FeedColumnsSelector.TAG_ID} IN (${
                            placeholders(
                                filter.values.size
                            )
                        }))"
                        bindings += filter.values.bindLongPlaceholders()
                    }
                }

                is LocalFeedPostFilter.UpdatedRange -> {
                    val (binding, clause) = filter.range.appendRangeClause(FeedColumnsSelector.UPDATED_AT)
                        ?: continue
                    clauses += clause
                    bindings += binding
                }
            }
        }

        return bindings to clauses
    }

    private fun FeedPostLongRange.appendRangeClause(
        column: String,
    ): Pair<List<SqlBinding>, String>? {
        var clause: MutableList<String> = mutableListOf()
        val binding: MutableList<SqlBinding> = mutableListOf()
        min?.let {
            clause += "$column >= ?"
            binding += SqlBinding.LongValue(it)
        }
        max?.let {
            clause += "$column <= ?"
            binding += SqlBinding.LongValue(it)
        }

        if (clause.isEmpty() || binding.isEmpty()) {
            return null
        }

        val joinedClause = "(${clause.joinToString(separator = " AND ")})"
        return binding to joinedClause
    }

    private fun imageExistsClause(): String {
        return "(${FeedColumnsSelector.IMAGE} IS NOT NULL AND ${FeedColumnsSelector.IMAGE} != '')"
    }

    private fun audioExistsClause(): String {
        return """
            (
                (${FeedColumnsSelector.AUDIO} IS NOT NULL AND ${FeedColumnsSelector.AUDIO} != '') OR
                (${FeedColumnsSelector.RAW_ENCLOSURE_TYPE} IS NOT NULL AND ${FeedColumnsSelector.RAW_ENCLOSURE_TYPE} LIKE 'audio/%') OR
                (${FeedColumnsSelector.RAW_MEDIA_TYPE} IS NOT NULL AND ${FeedColumnsSelector.RAW_MEDIA_TYPE} LIKE 'audio/%') OR
                (${FeedColumnsSelector.RAW_MEDIA_MEDIUM} IS NOT NULL AND ${FeedColumnsSelector.RAW_MEDIA_MEDIUM} = 'audio')
            )
        """.trimIndent()
    }

    private fun videoExistsClause(): String {
        return """
            (
                (${FeedColumnsSelector.VIDEO} IS NOT NULL AND ${FeedColumnsSelector.VIDEO} != '') OR
                (${FeedColumnsSelector.YOUTUBE_DATA_VIDEO_ID} IS NOT NULL AND ${FeedColumnsSelector.YOUTUBE_DATA_VIDEO_ID} != '') OR
                (${FeedColumnsSelector.YOUTUBE_DATA_VIDEO_URL} IS NOT NULL AND ${FeedColumnsSelector.YOUTUBE_DATA_VIDEO_URL} != '') OR
                (${FeedColumnsSelector.RAW_ENCLOSURE_TYPE} IS NOT NULL AND ${FeedColumnsSelector.RAW_ENCLOSURE_TYPE} LIKE 'video/%') OR
                (${FeedColumnsSelector.RAW_MEDIA_TYPE} IS NOT NULL AND ${FeedColumnsSelector.RAW_MEDIA_TYPE} LIKE 'video/%') OR
                (${FeedColumnsSelector.RAW_MEDIA_MEDIUM} IS NOT NULL AND ${FeedColumnsSelector.RAW_MEDIA_MEDIUM} = 'video')
            )
        """.trimIndent()
    }

    private fun buildCursorClause(
        cursor: FeedPostCursor,
        includeCursor: Boolean,
        reversed: Boolean,
        bindings: MutableList<SqlBinding>
    ): String {
        val effectiveSort = if (reversed) cursor.sort.reversed() else cursor.sort
        val (primaryDirection, idDirection) = effectiveSort.getDirections()
        val primaryOp = primaryDirection.toOperator(includeCursor)
        val idOp = idDirection.toOperator(includeCursor)
        val sortColumnName = cursor.sort.asColumnName()
        val clauses = """
        (
            $sortColumnName $primaryOp ? OR
            ($sortColumnName = ? AND ${FeedColumnsSelector.ID} $idOp ?)
        )
    """.trimIndent()

        val idBinding = SqlBinding.Text(cursor.id.value)
        bindings += listOf(cursor.sortValue, cursor.sortValue, idBinding)
        return clauses
    }

    private fun LocalFeedPostSort.getDirections(): Pair<LocalFeedPostSortDirection, LocalFeedPostSortDirection> {
        return when (this) {
            LocalFeedPostSort.PublishedNewest -> LocalFeedPostSortDirection.Descending to LocalFeedPostSortDirection.Descending
            LocalFeedPostSort.PublishedOldest -> LocalFeedPostSortDirection.Ascending to LocalFeedPostSortDirection.Ascending
            LocalFeedPostSort.TitleAtoZ -> LocalFeedPostSortDirection.Ascending to LocalFeedPostSortDirection.Descending
            LocalFeedPostSort.TitleZtoA -> LocalFeedPostSortDirection.Descending to LocalFeedPostSortDirection.Descending
        }
    }

    private fun LocalFeedPostSortDirection.toOperator(includeCursor: Boolean): String {
        return when (this) {
            LocalFeedPostSortDirection.Ascending -> if (includeCursor) ">=" else ">"
            LocalFeedPostSortDirection.Descending -> if (includeCursor) "<=" else "<"
        }
    }

    private fun LocalFeedPostSort.asColumnName(): String {
        return when (this) {
            LocalFeedPostSort.PublishedNewest,
            LocalFeedPostSort.PublishedOldest -> FeedColumnsSelector.PUBLISHED_AT_EPOCH_MILLIS

            LocalFeedPostSort.TitleAtoZ,
            LocalFeedPostSort.TitleZtoA -> FeedColumnsSelector.TITLE
        }
    }

    private fun Set<String>.bindLowercasedTextPlaceholders(): List<SqlBinding.Text> {
        return sorted().map { SqlBinding.Text(it.lowercase()) }
    }

    private fun Set<Int>.bindLongPlaceholders(): List<SqlBinding.LongValue> {
        return sorted().map { SqlBinding.LongValue(it.toLong()) }
    }

    private fun placeholders(count: Int): String {
        return List(count) { "?" }.joinToString()
    }

    companion object {
        const val MAX_SEARCH_RESULTS = 1000
    }
}
