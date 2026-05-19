package dev.shounakmulay.devpulse.core.data.feed

interface IdentityGenerator {
    fun generateSortableId(): String
    fun generateHash(string: String): String
}

