package dev.shounakmulay.devpulse.core.data.db.query

import androidx.sqlite.SQLiteStatement

sealed interface SqlBinding {
    fun bind(statement: SQLiteStatement, index: Int)

    data class Text(
        val value: String
    ) : SqlBinding {
        override fun bind(statement: SQLiteStatement, index: Int) {
            statement.bindText(index, value)
        }
    }

    data class LongValue(
        val value: Long
    ) : SqlBinding {
        override fun bind(statement: SQLiteStatement, index: Int) {
            statement.bindLong(index, value)
        }
    }

    data class BooleanValue(
        val value: Boolean
    ) : SqlBinding {
        override fun bind(statement: SQLiteStatement, index: Int) {
            statement.bindLong(index, if (value) 1L else 0L)
        }
    }
}
