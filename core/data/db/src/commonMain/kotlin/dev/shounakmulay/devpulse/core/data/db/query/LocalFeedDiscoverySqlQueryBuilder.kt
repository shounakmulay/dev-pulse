package dev.shounakmulay.devpulse.core.data.db.query

import androidx.room3.RoomRawQuery
import dev.shounakmulay.devpulse.core.common.text.HL_END
import dev.shounakmulay.devpulse.core.common.text.HL_START
import dev.shounakmulay.devpulse.core.data.db.model.core.LocalUUID

internal class LocalFeedDiscoverySqlQueryBuilder(
    private val query: LocalFeedDiscoveryQuery,
) {
    private val sortColumn = when (query) {
        is LocalFeedDiscoveryQuery.Collection -> "mapping.position"
        is LocalFeedDiscoveryQuery.Search -> "search.rank"
    }

    fun buildInitialPage(limit: Int) = buildPage(limit = limit)

    fun buildPageAfter(cursor: FeedDiscoveryCursor, limit: Int) =
        buildPage(cursor = cursor, limit = limit)

    fun buildPageBefore(cursor: FeedDiscoveryCursor, limit: Int) =
        buildPage(cursor = cursor, reversed = true, limit = limit)

    fun buildRefreshPageAround(cursor: FeedDiscoveryCursor, limit: Int) =
        buildPage(cursor = cursor, includeCursor = true, limit = limit)

    fun buildAnchorQuery(id: LocalUUID) = buildPage(id = id, limit = 1)

    private fun buildPage(
        cursor: FeedDiscoveryCursor? = null,
        id: LocalUUID? = null,
        reversed: Boolean = false,
        includeCursor: Boolean = false,
        limit: Int,
    ): RoomRawQuery {
        val bindings = mutableListOf<SqlBinding>()
        val projection = buildProjection(bindings)
        val selection = buildSelection(bindings)
        val sql = buildString {
            appendLine("SELECT $projection FROM LocalFeedDiscoveryFeedMetadata feed")
            appendLine(selection)
            id?.let {
                appendLine("AND feed.id = ?")
                bindings += SqlBinding.Text(it.value)
            }
            cursor?.let {
                appendLine("AND ${buildCursorClause(it, reversed, includeCursor, bindings)}")
            }
            val direction = if (reversed) "DESC" else "ASC"
            appendLine("ORDER BY $sortColumn $direction, feed.id $direction")
            append("LIMIT ?")
            bindings += SqlBinding.LongValue(limit.toLong())
        }
        return RoomRawQuery(sql) { statement ->
            bindings.forEachIndexed { index, binding -> binding.bind(statement, index + 1) }
        }
    }

    private fun buildProjection(bindings: MutableList<SqlBinding>): String {
        return when (query) {
            is LocalFeedDiscoveryQuery.Collection -> """
                ${FeedDiscoverySql.METADATA},
                ${FeedDiscoverySql.NO_HIGHLIGHTS},
                mapping.position AS position,
                0.0 AS `rank`
            """.trimIndent()
            is LocalFeedDiscoveryQuery.Search -> {
                repeat(5) {
                    bindings += SqlBinding.Text(HL_START)
                    bindings += SqlBinding.Text(HL_END)
                }
                """
                ${FeedDiscoverySql.METADATA},
                NULLIF(highlight(LocalFeedDiscoveryFeedMetadataFts, 0, ?, ?), feed.title) AS search_highlightedTitle,
                CASE WHEN highlight(LocalFeedDiscoveryFeedMetadataFts, 1, ?, ?) != feed.description
                     THEN snippet(LocalFeedDiscoveryFeedMetadataFts, 1, ?, ?, '...', 30) END AS search_highlightedDescription,
                NULLIF(highlight(LocalFeedDiscoveryFeedMetadataFts, 2, ?, ?), feed.url) AS search_highlightedUrl,
                NULLIF(highlight(LocalFeedDiscoveryFeedMetadataFts, 3, ?, ?), feed.websiteUrl) AS search_highlightedWebsiteUrl,
                0 AS position,
                search.rank AS `rank`
                """.trimIndent()
            }
        }
    }

    private fun buildSelection(bindings: MutableList<SqlBinding>): String {
        return when (query) {
            is LocalFeedDiscoveryQuery.Collection -> {
                bindings += SqlBinding.Text(query.collectionId.value)
                """
                JOIN LocalFeedDiscoveryCollectionToFeedMapping mapping ON mapping.feedId = feed.id
                WHERE mapping.collectionId = ?
                """.trimIndent()
            }
            is LocalFeedDiscoveryQuery.Search -> buildString {
                appendLine("JOIN LocalFeedDiscoveryFeedMetadataFts search ON search.rowid = feed.rowid")
                appendLine("WHERE LocalFeedDiscoveryFeedMetadataFts MATCH ?")
                bindings += SqlBinding.Text(query.text)
                appendLine("AND ${FeedDiscoverySql.MEMBERSHIP}")
                query.collectionId?.let {
                    appendLine("AND EXISTS(SELECT 1 FROM LocalFeedDiscoveryCollectionToFeedMapping membership WHERE membership.feedId = feed.id AND membership.collectionId = ?)")
                    bindings += SqlBinding.Text(it.value)
                }
            }
        }
    }

    private fun buildCursorClause(
        cursor: FeedDiscoveryCursor,
        reversed: Boolean,
        includeCursor: Boolean,
        bindings: MutableList<SqlBinding>,
    ): String {
        val comparison = if (reversed) "<" else ">"
        val idComparison = comparison + if (includeCursor) "=" else ""
        val value = when (cursor) {
            is FeedDiscoveryCursor.Collection -> SqlBinding.LongValue(cursor.position.toLong())
            is FeedDiscoveryCursor.Search -> SqlBinding.DoubleValue(cursor.rank)
        }
        bindings += listOf(value, value, SqlBinding.Text(cursor.id.value))
        return "($sortColumn $comparison ? OR ($sortColumn = ? AND feed.id $idComparison ?))"
    }
}
