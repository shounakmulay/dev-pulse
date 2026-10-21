package dev.shounakmulay.devpulse.core.data.db.query

import androidx.room3.RoomRawQuery
import androidx.sqlite.SQLiteStatement
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse

object FeedPostQueryFixtures {
    const val DefaultLimit = 20

    fun initialPage(query: LocalFeedPostQuery): RoomRawQuery =
        LocalFeedPostSqlQueryBuilder(query).buildInitialPage(DefaultLimit)

    fun text(value: String): SqlBinding = SqlBinding.Text(value)
    fun long(value: Long): SqlBinding = SqlBinding.LongValue(value)
    fun boolean(value: Boolean): SqlBinding = SqlBinding.LongValue(if (value) 1L else 0L)
}

fun RoomRawQuery.assertSqlContains(fragment: String): RoomRawQuery = apply {
    assertContains(sql.normalizedSql(), fragment.normalizedSql())
}

fun RoomRawQuery.assertSqlExcludes(fragment: String): RoomRawQuery = apply {
    assertFalse(sql.normalizedSql().contains(fragment.normalizedSql()), sql)
}

fun RoomRawQuery.assertOrderBy(vararg terms: String): RoomRawQuery =
    assertSqlContains("ORDER BY ${terms.joinToString()} LIMIT ?")

fun RoomRawQuery.assertBindings(vararg expected: SqlBinding): RoomRawQuery = apply {
    val actual = mutableListOf<SqlBinding>()
    val connection = BundledSQLiteDriver().open(":memory:")
    try {
        val statement = connection.prepare("SELECT 1")
        try {
            val recorder = object : SQLiteStatement by statement {
                override fun bindText(index: Int, value: String) {
                    assertEquals(actual.size + 1, index)
                    actual += SqlBinding.Text(value)
                }
                override fun bindLong(index: Int, value: Long) {
                    assertEquals(actual.size + 1, index)
                    actual += SqlBinding.LongValue(value)
                }
            }
            getBindingFunction().invoke(recorder)
            assertEquals(expected.toList(), actual)
        } finally {
            statement.close()
        }
    } finally {
        connection.close()
    }
}

private fun String.normalizedSql(): String = trim().replace(Regex("\\s+"), " ")
