package dev.shounakmulay.devpulse.core.domain.models.post

import dev.shounakmulay.devpulse.core.domain.models.common.UUID

data class RssFeedPostIdentity(
    val id: UUID,
    val fingerprint: String,
    val bookmarked: Boolean,
    val createdAt: Long,
    val updatedAt: Long
)
