package dev.shounakmulay.devpulse.core.data.feed.identity

import dev.shounakmulay.devpulse.core.domain.models.common.UUID

interface IdentityGenerator {
    fun generateSortableId(): UUID
    fun generateFingerprint(vararg strings: String): String
}

