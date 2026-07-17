package dev.shounakmulay.devpulse.core.data.db.query

import androidx.room3.RoomRawQuery
import androidx.sqlite.SQLiteStatement
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal object FeedPostQueryFixtures {
    const val DefaultLimit = 20

    val approvedFiltersQuery = FeedPostQuery(
        filters = setOf(
            FeedPostFilter.FeedIds(values = setOf("feed-1", "feed-2")),
            FeedPostFilter.TagIdsAll(values = setOf(1, 2)),
            FeedPostFilter.Bookmarked(value = true),
            FeedPostFilter.PinnedFeed(value = false),
            FeedPostFilter.PublishedRange(range = FeedPostLongRange(min = 10L, max = 20L)),
            FeedPostFilter.UpdatedRange(range = FeedPostLongRange(min = 30L, max = 40L)),
            FeedPostFilter.HasAudio(value = true),
            FeedPostFilter.HasVideo(value = true)
        )
    )

    val publishedCursor = FeedPostCursor(
        sort = FeedPostSort.PublishedNewest,
        values = listOf(
            FeedPostCursorValue.LongValue(100L),
            FeedPostCursorValue.LongValue(90L),
            FeedPostCursorValue.TextValue("post-1")
        )
    )

    val titleAscendingQuery = FeedPostQuery(
        sort = FeedPostSort.TitleAtoZ
    )

    val titleCursor = FeedPostCursor(
        sort = FeedPostSort.TitleAtoZ,
        values = listOf(
            FeedPostCursorValue.TextValue("A title"),
            FeedPostCursorValue.TextValue("post-1")
        )
    )

    fun initialPage(
        query: FeedPostQuery = FeedPostQuery(),
        limit: Int = DefaultLimit
    ): FeedPostQueryAssertion {
        return assertion(FeedPostSqlQueryBuilder(query).buildInitialPage(limit = limit))
    }

    fun pageAfter(
        query: FeedPostQuery = FeedPostQuery(),
        cursor: FeedPostCursor = publishedCursor,
        limit: Int = DefaultLimit
    ): FeedPostQueryAssertion {
        return assertion(
            FeedPostSqlQueryBuilder(query).buildPageAfter(
                cursor = cursor,
                limit = limit
            )
        )
    }

    fun refreshPageAround(
        query: FeedPostQuery = FeedPostQuery(),
        cursor: FeedPostCursor = publishedCursor,
        limit: Int = DefaultLimit
    ): FeedPostQueryAssertion {
        return assertion(
            FeedPostSqlQueryBuilder(query).buildRefreshPageAround(
                cursor = cursor,
                limit = limit
            )
        )
    }

    fun assertion(rawQuery: RoomRawQuery): FeedPostQueryAssertion {
        return FeedPostQueryAssertion(rawQuery)
    }

    fun rawQuery(
        sql: String,
        vararg bindings: FeedPostBoundValue
    ): RoomRawQuery {
        return RoomRawQuery(sql = sql) { statement ->
            bindings.forEachIndexed { index, value ->
                value.bind(
                    statement = statement,
                    index = index + 1
                )
            }
        }
    }

    fun text(value: String): FeedPostBoundValue {
        return FeedPostBoundValue.Text(value)
    }

    fun long(value: Long): FeedPostBoundValue {
        return FeedPostBoundValue.LongValue(value)
    }

    fun double(value: Double): FeedPostBoundValue {
        return FeedPostBoundValue.DoubleValue(value)
    }

    fun blob(value: List<Byte>): FeedPostBoundValue {
        return FeedPostBoundValue.Blob(value)
    }

    fun boolean(value: Boolean): FeedPostBoundValue {
        return FeedPostBoundValue.LongValue(if (value) 1L else 0L)
    }

    fun nullValue(): FeedPostBoundValue {
        return FeedPostBoundValue.NullValue
    }
}

internal class FeedPostQueryAssertion(
    private val rawQuery: RoomRawQuery
) {
    val sql = rawQuery.sql
    val normalizedSql = sql.normalizedSql()
    val bindings = rawQuery.captureBindings()

    fun assertSqlContains(vararg fragments: String): FeedPostQueryAssertion {
        fragments.forEach { fragment ->
            assertContains(normalizedSql, fragment.normalizedSql())
        }
        return this
    }

    fun assertSqlExcludes(vararg fragments: String): FeedPostQueryAssertion {
        fragments.forEach { fragment ->
            assertFalse(
                normalizedSql.contains(fragment.normalizedSql()),
                "Expected SQL to exclude <$fragment> but was <$normalizedSql>"
            )
        }
        return this
    }

    fun assertSqlContainsInOrder(vararg fragments: String): FeedPostQueryAssertion {
        var nextStartIndex = 0
        fragments.forEach { fragment ->
            val normalizedFragment = fragment.normalizedSql()
            val fragmentIndex = normalizedSql.indexOf(
                string = normalizedFragment,
                startIndex = nextStartIndex
            )
            assertTrue(
                fragmentIndex >= 0,
                "Expected SQL to contain <$fragment> after index $nextStartIndex but was <$normalizedSql>"
            )
            nextStartIndex = fragmentIndex + normalizedFragment.length
        }
        return this
    }

    fun assertOrderBy(vararg expressions: String): FeedPostQueryAssertion {
        val fragments = listOf("ORDER BY") + expressions.toList() + "LIMIT ?"
        return assertSqlContainsInOrder(*fragments.toTypedArray())
    }

    fun assertBindings(vararg expected: FeedPostBoundValue): FeedPostQueryAssertion {
        assertEquals(
            expected = expected.toList(),
            actual = bindings.map { it.value }
        )
        return this
    }

    fun assertIndexedBindings(vararg expected: FeedPostSqlBinding): FeedPostQueryAssertion {
        assertEquals(
            expected = expected.toList(),
            actual = bindings
        )
        return this
    }

    fun assertPlaceholderCountMatchesBindings(): FeedPostQueryAssertion {
        assertEquals(
            expected = sql.count { it == '?' },
            actual = bindings.size
        )
        return this
    }
}

internal data class FeedPostSqlBinding(
    val index: Int,
    val value: FeedPostBoundValue
)

internal sealed interface FeedPostBoundValue {
    fun bind(
        statement: SQLiteStatement,
        index: Int
    )

    data class Text(
        val value: String
    ) : FeedPostBoundValue {
        override fun bind(
            statement: SQLiteStatement,
            index: Int
        ) {
            statement.bindText(index, value)
        }
    }

    data class LongValue(
        val value: Long
    ) : FeedPostBoundValue {
        override fun bind(
            statement: SQLiteStatement,
            index: Int
        ) {
            statement.bindLong(index, value)
        }
    }

    data class DoubleValue(
        val value: Double
    ) : FeedPostBoundValue {
        override fun bind(
            statement: SQLiteStatement,
            index: Int
        ) {
            statement.bindDouble(index, value)
        }
    }

    data class Blob(
        val value: List<Byte>
    ) : FeedPostBoundValue {
        override fun bind(
            statement: SQLiteStatement,
            index: Int
        ) {
            statement.bindBlob(index, value.toByteArray())
        }
    }

    data object NullValue : FeedPostBoundValue {
        override fun bind(
            statement: SQLiteStatement,
            index: Int
        ) {
            statement.bindNull(index)
        }
    }
}

private fun RoomRawQuery.captureBindings(): List<FeedPostSqlBinding> {
    val statement = RecordingSQLiteStatement()
    getBindingFunction().invoke(statement)
    return statement.bindings
        .toSortedMap()
        .map { (index, value) ->
            FeedPostSqlBinding(
                index = index,
                value = value
            )
        }
}

private fun String.normalizedSql(): String {
    return trim()
        .lineSequence()
        .map { it.trim() }
        .filter { it.isNotEmpty() }
        .joinToString(separator = " ")
}

private class RecordingSQLiteStatement : SQLiteStatement {
    val bindings = mutableMapOf<Int, FeedPostBoundValue>()

    override fun bindBlob(index: Int, value: ByteArray) {
        bindings[index] = FeedPostBoundValue.Blob(value.toList())
    }

    override fun bindDouble(index: Int, value: Double) {
        bindings[index] = FeedPostBoundValue.DoubleValue(value)
    }

    override fun bindFloat(index: Int, value: Float) {
        bindDouble(index = index, value = value.toDouble())
    }

    override fun bindLong(index: Int, value: Long) {
        bindings[index] = FeedPostBoundValue.LongValue(value)
    }

    override fun bindInt(index: Int, value: Int) {
        bindLong(index = index, value = value.toLong())
    }

    override fun bindBoolean(index: Int, value: Boolean) {
        bindLong(index = index, value = if (value) 1L else 0L)
    }

    override fun bindText(index: Int, value: String) {
        bindings[index] = FeedPostBoundValue.Text(value)
    }

    override fun bindNull(index: Int) {
        bindings[index] = FeedPostBoundValue.NullValue
    }

    override fun getBlob(index: Int): ByteArray {
        unsupportedRead()
    }

    override fun getDouble(index: Int): Double {
        unsupportedRead()
    }

    override fun getFloat(index: Int): Float {
        unsupportedRead()
    }

    override fun getLong(index: Int): Long {
        unsupportedRead()
    }

    override fun getInt(index: Int): Int {
        unsupportedRead()
    }

    override fun getBoolean(index: Int): Boolean {
        unsupportedRead()
    }

    override fun getText(index: Int): String {
        unsupportedRead()
    }

    override fun isNull(index: Int): Boolean {
        unsupportedRead()
    }

    override fun getColumnCount(): Int {
        unsupportedRead()
    }

    override fun getColumnName(index: Int): String {
        unsupportedRead()
    }

    override fun getColumnNames(): List<String> {
        unsupportedRead()
    }

    override fun getColumnType(index: Int): Int {
        unsupportedRead()
    }

    override fun reset() {
    }

    override fun clearBindings() {
        bindings.clear()
    }

    override fun step(): Boolean {
        unsupportedRead()
    }

    override fun close() {
    }

    private fun unsupportedRead(): Nothing {
        error("RecordingSQLiteStatement only supports bind calls")
    }
}
