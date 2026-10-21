package dev.shounakmulay.devpulse.core.domain.models.feed

import dev.shounakmulay.devpulse.core.domain.models.common.UUID

data class RssFeedIdentity(
    val id: UUID,
    val title: String?,
    val name: String?,
    val pinned: Boolean,
    val sourceUrl: String,
    val link: String?,
    val createdAt: Long,
    val updatedAt: Long,
    val lastOpenedAt: Long?
)