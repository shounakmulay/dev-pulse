package dev.shounakmulay.devpulse.core.data.feed.identity

interface IdentityGenerator {
    fun generateSortableId(): String
    fun generateFingerprint(vararg strings: String): String
}

