package dev.shounakmulay.devpulse.core.data.db.query

import org.koin.core.annotation.Factory

@Factory
class FtsQuerySanitizer {
    fun sanitize(query: String): String {
        val tokens = query
            .split(Regex("[^\\p{L}\\p{N}]+")) // split on any non-letter/digit run
            .filter { it.isNotBlank() }

        if (tokens.isEmpty()) return ""

        return tokens.joinToString(" ")
    }
}