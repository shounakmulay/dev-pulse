package dev.shounakmulay.devpulse.core.data.db.model.feed.slices

import dev.shounakmulay.devpulse.core.data.db.model.core.LocalUUID

data class LocalRssFeedIdentitySlice(
    val id: LocalUUID,
    val title: String?,
    val name: String?,
    val pinned: Boolean,
    val sourceUrl: String,
    val link: String?,
    val createdAt: Long,
    val updatedAt: Long,
    val lastOpenedAt: Long?,
)