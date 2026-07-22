package dev.shounakmulay.devpulse.core.data.db.query

enum class LocalFeedPostSortDirection(
    val sql: String
) {
    Ascending("ASC"),
    Descending("DESC");

    fun keysetOperator(includeCursor: Boolean): String {
        return when (this) {
            Ascending -> if (includeCursor) ">=" else ">"
            Descending -> if (includeCursor) "<=" else "<"
        }
    }

    fun reverse(): LocalFeedPostSortDirection {
        return when (this) {
            Ascending -> Descending
            Descending -> Ascending
        }
    }
}