package dev.shounakmulay.devpulse.core.data.db.query

import androidx.room3.RoomRawQuery

class FeedPostSqlQueryBuilder(
    private val query: FeedPostQuery
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

    private fun buildPage(
        cursor: FeedPostCursor?,
        includeCursor: Boolean,
        reversed: Boolean = false,
        limit: Int
    ): RoomRawQuery {
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

        val orderBySql = buildOrderBy(sort = query.sort)

        val sql = buildString {
            appendProjection()
            if (filterClauses.isNotEmpty()) {
                appendLine("WHERE ${filterClauses.joinToString(separator = " AND ")}")
            }
            appendLine("ORDER BY $orderBySql")
            append("LIMIT ?")
        }
        filterBindings += SqlBinding.LongValue(limit.toLong())
        return RoomRawQuery(sql = sql) { statement ->
            filterBindings.forEachIndexed { index, binding ->
                binding.bind(statement, index + 1)
            }
        }
    }

    private fun buildOrderBy(sort: FeedPostSort): String {
        val sortTerms = when (sort) {
            FeedPostSort.PublishedNewest -> {
                listOf(
                    "p.publishedAtEpochMillis ${FeedPostSortDirection.Descending.sql}",
                    orderByIdClause(FeedPostSortDirection.Descending)
                )
            }

            FeedPostSort.PublishedOldest -> {
                listOf(
                    "p.publishedAtEpochMillis ${FeedPostSortDirection.Ascending.sql}",
                    orderByIdClause(
                        FeedPostSortDirection.Ascending
                    )
                )
            }

            FeedPostSort.TitleAtoZ -> {
                listOf(
                    "p.title ${FeedPostSortDirection.Ascending.sql}",
                    orderByIdClause(FeedPostSortDirection.Descending)
                )
            }
            FeedPostSort.TitleZtoA -> {
                listOf(
                    "p.title ${FeedPostSortDirection.Descending.sql}",
                    orderByIdClause(FeedPostSortDirection.Descending)
                )
            }
        }
        return sortTerms.joinToString(", ")
    }

    private fun orderByIdClause(direction: FeedPostSortDirection): String {
        return "p.id ${direction.sql}"
    }

    private fun StringBuilder.appendProjection() {
        appendLine(
            """
            SELECT 
                p.*,
                f.id AS feed_id,
                f.title AS feed_title,
                f.name AS feed_name,
                f.pinned AS feed_pinned,
                f.sourceUrl AS feed_sourceUrl,
                f.link AS feed_link,
                f.createdAt AS feed_createdAt,
                f.updatedAt AS feed_updatedAt
            FROM LocalRssContentFeedPost p
            INNER JOIN LocalRssFeed f ON p.feedId = f.id
            """.trimIndent()
        )
    }

    private fun buildWhereClauses(): Pair<List<SqlBinding>, List<String>> {
        val bindings = mutableListOf<SqlBinding>()
        val filters = query.filters
        val clauses = mutableListOf<String>()

        for (filter in filters) {
            when (filter) {
                is FeedPostFilter.Author -> {
                    if (filter.values.isNotEmpty()) {
                        clauses += "LOWER(p.author) IN (${placeholders(filter.values.size)})"
                        bindings += filter.values.bindLowercasedTextPlaceholders()
                    }
                }

                is FeedPostFilter.Bookmarked -> {
                    clauses += "p.bookmarked = ?"
                    bindings += SqlBinding.BooleanValue(filter.value)
                }

                is FeedPostFilter.Category -> {
                    if (filter.values.isNotEmpty()) {
                        clauses += "p.id IN (SELECT postId FROM LocalRssPostCategory WHERE LOWER(category) IN (${
                            placeholders(
                                filter.values.size
                            )
                        }))"
                        bindings += filter.values.bindLowercasedTextPlaceholders()
                    }
                }

                is FeedPostFilter.CreatedRange -> {
                    val (binding, clause) = filter.range.appendRangeClause("p.createdAt")
                        ?: continue
                    clauses += clause
                    bindings += binding
                }

                is FeedPostFilter.FeedIds -> {
                    if (filter.values.isNotEmpty()) {
                        clauses += "p.feedId IN (${placeholders(filter.values.size)})"
                        bindings += filter.values.sorted().map { SqlBinding.Text(it) }
                    }
                }

                is FeedPostFilter.HasAudio -> {
                    clauses += if (filter.value) audioExistsClause() else "NOT (${audioExistsClause()})"
                }

                is FeedPostFilter.HasEnclosure -> {
                    val clause = "(p.rawEnclosure_type IS NOT NULL AND p.rawEnclosure_type != '')"
                    clauses += if (filter.value) clause else "NOT ($clause)"
                }

                is FeedPostFilter.HasImage -> {
                    clauses += if (filter.value) imageExistsClause() else "NOT (${imageExistsClause()})"
                }

                is FeedPostFilter.HasVideo -> {
                    clauses += if (filter.value) videoExistsClause() else "NOT (${videoExistsClause()})"
                }

                is FeedPostFilter.HasYouTubeData -> {
                    val clause =
                        "(p.youtubeData_videoId IS NOT NULL AND p.youtubeData_videoId != '')"
                    clauses += if (filter.value) clause else "NOT ($clause)"
                }

                is FeedPostFilter.PinnedFeed -> {
                    clauses += "f.pinned = ?"
                    bindings += SqlBinding.BooleanValue(filter.value)
                }

                is FeedPostFilter.PublishedRange -> {
                    val (binding, clause) = filter.range.appendRangeClause("p.publishedAtEpochMillis")
                        ?: continue
                    clauses += clause
                    bindings += binding
                }

                is FeedPostFilter.SearchText -> {
                    val trimmed = filter.value.trim()
                    if (trimmed.isNotEmpty()) {
                        val escaped = escapeLikePattern(trimmed)
                        val pattern = "%$escaped%"
                        clauses += "(LOWER(p.title) LIKE ? ESCAPE '\\' OR LOWER(p.description) LIKE ? ESCAPE '\\')"
                        bindings += SqlBinding.Text(pattern.lowercase())
                        bindings += SqlBinding.Text(pattern.lowercase())
                    }
                }

                is FeedPostFilter.SourceFeed -> {
                    if (filter.values.isNotEmpty()) {
                        clauses += "LOWER(p.sourceUrl) IN (${placeholders(filter.values.size)})"
                        bindings += filter.values.bindLowercasedTextPlaceholders()
                    }
                }

                is FeedPostFilter.TagIdsAny -> {
                    if (filter.values.isNotEmpty()) {
                        clauses += "p.id IN (SELECT postId FROM LocalRssPostToTagMapping WHERE tagId IN (${
                            placeholders(
                                filter.values.size
                            )
                        }))"
                        bindings += filter.values.bindLongPlaceholders()
                    }
                }

                is FeedPostFilter.UpdatedRange -> {
                    val (binding, clause) = filter.range.appendRangeClause("p.updatedAt")
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
    ): Pair<SqlBinding, String>? {
        val clause: String
        val binding: SqlBinding
        min?.let {
            clause = "$column >= ?"
            binding = SqlBinding.LongValue(it)
            return binding to clause
        }
        max?.let {
            clause = "$column <= ?"
            binding = SqlBinding.LongValue(it)
            return binding to clause
        }
        return null
    }

    private fun imageExistsClause(): String {
        return "(p.image IS NOT NULL AND p.image != '')"
    }

    private fun audioExistsClause(): String {
        return """
            (
                (p.audio IS NOT NULL AND p.audio != '') OR
                (p.rawEnclosure_type IS NOT NULL AND p.rawEnclosure_type LIKE 'audio/%') OR
                (p.rawMedia_type IS NOT NULL AND p.rawMedia_type LIKE 'audio/%') OR
                (p.rawMedia_medium IS NOT NULL AND p.rawMedia_medium = 'audio')
            )
        """.trimIndent()
    }

    private fun videoExistsClause(): String {
        return """
            (
                (p.video IS NOT NULL AND p.video != '') OR
                (p.youtubeData_videoId IS NOT NULL AND p.youtubeData_videoId != '') OR
                (p.youtubeData_videoUrl IS NOT NULL AND p.youtubeData_videoUrl != '') OR
                (p.rawEnclosure_type IS NOT NULL AND p.rawEnclosure_type LIKE 'video/%') OR
                (p.rawMedia_type IS NOT NULL AND p.rawMedia_type LIKE 'video/%') OR
                (p.rawMedia_medium IS NOT NULL AND p.rawMedia_medium = 'video')
            )
        """.trimIndent()
    }

    private fun buildCursorClause(
        cursor: FeedPostCursor,
        includeCursor: Boolean,
        reversed: Boolean,
        bindings: MutableList<SqlBinding>
    ): String {
        val spec = query.sort.spec
        return buildKeysetPredicate(
            terms = spec.terms,
            cursorValues = cursor.values,
            includeCursor = includeCursor,
            reversed = reversed,
            bindings = bindings
        )
    }

    private fun buildKeysetPredicate(
        terms: List<FeedPostSortTerm>,
        cursorValues: List<FeedPostCursorValue>,
        includeCursor: Boolean,
        reversed: Boolean,
        bindings: MutableList<SqlBinding>
    ): String {
        require(terms.size == cursorValues.size) {
            "Cursor value count (${cursorValues.size}) must match sort term count (${terms.size})"
        }
        val clauses = mutableListOf<String>()
        for (pivot in terms.indices) {
            val term = terms[pivot]
            val cursorValue = cursorValues[pivot]
            val direction = if (reversed) term.direction.reverse() else term.direction
            val isLastTerm = pivot == terms.lastIndex
            val useInclusive = isLastTerm && includeCursor
            val strictOp = direction.keysetOperator(includeCursor = false)
            val eqOrInclusiveOp = direction.keysetOperator(includeCursor = useInclusive)

            val equalityPrefix = (0 until pivot).joinToString(separator = " AND ") { i ->
                "${terms[i].expression} = ?"
            }

            if (pivot < terms.lastIndex) {
                val strictClause = buildString {
                    if (equalityPrefix.isNotEmpty()) append("$equalityPrefix AND ")
                    append("${term.expression} $strictOp ?")
                }
                val eqBindings = (0 until pivot).map { cursorValues[it].toBinding() }
                bindings += eqBindings
                bindings += cursorValue.toBinding()
                clauses += strictClause
            } else {
                val finalClause = buildString {
                    if (equalityPrefix.isNotEmpty()) append("$equalityPrefix AND ")
                    append("${term.expression} $eqOrInclusiveOp ?")
                }
                val eqBindings = (0 until pivot).map { cursorValues[it].toBinding() }
                bindings += eqBindings
                bindings += cursorValue.toBinding()
                clauses += finalClause
            }
        }
        return "(\n${clauses.joinToString(separator = " OR\n")}\n)"
    }

    private fun FeedPostCursorValue.toBinding(): SqlBinding {
        return when (this) {
            is FeedPostCursorValue.LongValue -> SqlBinding.LongValue(value)
            is FeedPostCursorValue.TextValue -> SqlBinding.Text(value)
            is FeedPostCursorValue.BooleanValue -> SqlBinding.BooleanValue(value)
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

    private fun escapeLikePattern(raw: String): String {
        return raw.replace("\\", "\\\\")
            .replace("%", "\\%")
            .replace("_", "\\_")
    }
}
