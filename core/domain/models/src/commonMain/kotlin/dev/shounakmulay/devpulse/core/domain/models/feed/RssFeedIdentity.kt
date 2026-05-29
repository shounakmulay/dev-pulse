package dev.shounakmulay.devpulse.core.domain.models.feed

data class RssFeedIdentity(
    val id: String,
    val title: String?,
    val name: String?,
    val pinned: Boolean,
    val sourceUrl: String,
    val link: String?,
    val createdAt: Long,
    val updatedAt: Long
)