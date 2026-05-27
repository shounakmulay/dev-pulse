package dev.shounakmulay.devpulse.core.domain.models.feed

data class RssFeed(
    val id: String,
    val pinned: Boolean,
    val name: String?,
    val sourceUrl: String,
    val title: String?,
    val link: String?,
    val description: String?,
    val image: RssFeedImage?,
    val lastBuildDate: String?,
    val updatePeriod: String?,
    val youtubeChannel: RssFeedYoutubeChannel?,
    val createdAt: Long,
    val updatedAt: Long
)
