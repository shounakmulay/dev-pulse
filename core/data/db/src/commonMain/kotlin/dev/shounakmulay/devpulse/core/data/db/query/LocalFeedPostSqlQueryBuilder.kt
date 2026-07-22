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
                    "${FeedColumnsSelector.PUBLISHED_AT_EPOCH_MILLIS} ${FeedPostSortDirection.Descending.sql}",
                    orderByIdClause(FeedPostSortDirection.Descending)
                )
            }

            FeedPostSort.PublishedOldest -> {
                listOf(
                    "${FeedColumnsSelector.PUBLISHED_AT_EPOCH_MILLIS} ${FeedPostSortDirection.Ascending.sql}",
                    orderByIdClause(
                        FeedPostSortDirection.Ascending
                    )
                )
            }

            FeedPostSort.TitleAtoZ -> {
                listOf(
                    "${FeedColumnsSelector.TITLE} ${FeedPostSortDirection.Ascending.sql}",
                    orderByIdClause(FeedPostSortDirection.Descending)
                )
            }

            FeedPostSort.TitleZtoA -> {
                listOf(
                    "${FeedColumnsSelector.TITLE} ${FeedPostSortDirection.Descending.sql}",
                    orderByIdClause(FeedPostSortDirection.Descending)
                )
            }
        }
        return sortTerms.joinToString(", ")
    }

    private fun orderByIdClause(direction: FeedPostSortDirection): String {
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
                ${FeedColumnsSelector.FEED_UPDATED_AT} AS ${FeedColumnsSelector.FEED_UPDATED_AT_ALIAS}
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
                is FeedPostFilter.Author -> {
                    if (filter.values.isNotEmpty()) {
                        clauses += "LOWER(${FeedColumnsSelector.AUTHOR}) IN (${placeholders(filter.values.size)})"
                        bindings += filter.values.bindLowercasedTextPlaceholders()
                    }
                }

                is FeedPostFilter.Bookmarked -> {
                    clauses += "${FeedColumnsSelector.BOOKMARKED} = ?"
                    bindings += SqlBinding.BooleanValue(filter.value)
                }

                is FeedPostFilter.Category -> {
                    if (filter.values.isNotEmpty()) {
                        clauses += "${FeedColumnsSelector.ID} IN (SELECT ${FeedColumnsSelector.POST_ID} FROM LocalRssPostCategory WHERE LOWER(${FeedColumnsSelector.CATEGORY}) IN (${
                            placeholders(
                                filter.values.size
                            )
                        }))"
                        bindings += filter.values.bindLowercasedTextPlaceholders()
                    }
                }

                is FeedPostFilter.CreatedRange -> {
                    val (binding, clause) = filter.range.appendRangeClause(FeedColumnsSelector.CREATED_AT)
                        ?: continue
                    clauses += clause
                    bindings += binding
                }

                is FeedPostFilter.FeedIds -> {
                    if (filter.values.isNotEmpty()) {
                        clauses += "${FeedColumnsSelector.POST_FEED_ID} IN (${placeholders(filter.values.size)})"
                        bindings += filter.values.sorted().map { SqlBinding.Text(it) }
                    }
                }

                is FeedPostFilter.HasAudio -> {
                    clauses += if (filter.value) audioExistsClause() else "NOT (${audioExistsClause()})"
                }

                is FeedPostFilter.HasEnclosure -> {
                    val clause = "(${FeedColumnsSelector.RAW_ENCLOSURE_TYPE} IS NOT NULL AND ${FeedColumnsSelector.RAW_ENCLOSURE_TYPE} != '')"
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
                        "(${FeedColumnsSelector.YOUTUBE_DATA_VIDEO_ID} IS NOT NULL AND ${FeedColumnsSelector.YOUTUBE_DATA_VIDEO_ID} != '')"
                    clauses += if (filter.value) clause else "NOT ($clause)"
                }

                is FeedPostFilter.PinnedFeed -> {
                    clauses += "${FeedColumnsSelector.FEED_PINNED} = ?"
                    bindings += SqlBinding.BooleanValue(filter.value)
                }

                is FeedPostFilter.PublishedRange -> {
                    val (binding, clause) = filter.range.appendRangeClause(FeedColumnsSelector.PUBLISHED_AT_EPOCH_MILLIS)
                        ?: continue
                    clauses += clause
                    bindings += binding
                }

                is FeedPostFilter.SearchText -> {
                    val trimmed = filter.value.trim()
                    if (trimmed.isNotEmpty()) {
                        val escaped = escapeLikePattern(trimmed)
                        val pattern = "%$escaped%"
                        clauses += "(LOWER(${FeedColumnsSelector.TITLE}) LIKE ? ESCAPE '\\' OR LOWER(${FeedColumnsSelector.DESCRIPTION}) LIKE ? ESCAPE '\\')"
                        bindings += SqlBinding.Text(pattern.lowercase())
                        bindings += SqlBinding.Text(pattern.lowercase())
                    }
                }

                is FeedPostFilter.SourceFeed -> {
                    if (filter.values.isNotEmpty()) {
                        clauses += "LOWER(${FeedColumnsSelector.SOURCE_URL}) IN (${placeholders(filter.values.size)})"
                        bindings += filter.values.bindLowercasedTextPlaceholders()
                    }
                }

                is FeedPostFilter.TagIdsAny -> {
                    if (filter.values.isNotEmpty()) {
                        clauses += "${FeedColumnsSelector.ID} IN (SELECT ${FeedColumnsSelector.POST_ID} FROM LocalRssPostToTagMapping WHERE ${FeedColumnsSelector.TAG_ID} IN (${
                            placeholders(
                                filter.values.size
                            )
                        }))"
                        bindings += filter.values.bindLongPlaceholders()
                    }
                }

                is FeedPostFilter.UpdatedRange -> {
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
        val comparator = when {
            reversed && includeCursor -> ">="
            reversed -> ">"
            includeCursor -> "<="
            else -> "<"
        }
        val sortColumnName = cursor.sort.asColumnName()
        val clauses = """
            (
                $sortColumnName $comparator ? OR
                ($sortColumnName = ? AND ${FeedColumnsSelector.ID} $comparator ?)
            )
        """
        val idBinding = SqlBinding.Text(cursor.id)
        bindings += listOf(cursor.sortValue, cursor.sortValue, idBinding)
        return clauses
    }

    private fun FeedPostSort.asColumnName(): String {
        return when (this) {
            FeedPostSort.PublishedNewest,
            FeedPostSort.PublishedOldest -> FeedColumnsSelector.PUBLISHED_AT_EPOCH_MILLIS

            FeedPostSort.TitleAtoZ,
            FeedPostSort.TitleZtoA -> FeedColumnsSelector.TITLE
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

object FeedColumnsSelector {
    const val SOURCE_URL = "p.sourceUrl"
    const val PUBLISHED_AT_EPOCH_MILLIS = "p.publishedAtEpochMillis"
    const val TITLE = "p.title"
    const val ID = "p.id"
    const val AUTHOR = "p.author"
    const val BOOKMARKED = "p.bookmarked"
    const val CREATED_AT = "p.createdAt"
    const val DESCRIPTION = "p.description"
    const val IMAGE = "p.image"
    const val AUDIO = "p.audio"
    const val VIDEO = "p.video"
    const val RAW_ENCLOSURE_TYPE = "p.rawEnclosure_type"
    const val RAW_MEDIA_TYPE = "p.rawMedia_type"
    const val RAW_MEDIA_MEDIUM = "p.rawMedia_medium"
    const val YOUTUBE_DATA_VIDEO_ID = "p.youtubeData_videoId"
    const val YOUTUBE_DATA_VIDEO_URL = "p.youtubeData_videoUrl"
    const val POST_FEED_ID = "p.feedId"
    const val UPDATED_AT = "p.updatedAt"
    const val FEED_ID = "f.id"
    const val FEED_TITLE = "f.title"
    const val FEED_NAME = "f.name"
    const val FEED_PINNED = "f.pinned"
    const val FEED_SOURCE_URL = "f.sourceUrl"
    const val FEED_LINK = "f.link"
    const val FEED_CREATED_AT = "f.createdAt"
    const val FEED_UPDATED_AT = "f.updatedAt"
    const val FEED_ID_ALIAS = "feed_id"
    const val FEED_TITLE_ALIAS = "feed_title"
    const val FEED_NAME_ALIAS = "feed_name"
    const val FEED_PINNED_ALIAS = "feed_pinned"
    const val FEED_SOURCE_URL_ALIAS = "feed_sourceUrl"
    const val FEED_LINK_ALIAS = "feed_link"
    const val FEED_CREATED_AT_ALIAS = "feed_createdAt"
    const val FEED_UPDATED_AT_ALIAS = "feed_updatedAt"
    const val POST_ID = "postId"
    const val CATEGORY = "category"
    const val TAG_ID = "tagId"
}
