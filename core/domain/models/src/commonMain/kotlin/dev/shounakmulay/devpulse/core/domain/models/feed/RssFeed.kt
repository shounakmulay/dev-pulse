package dev.shounakmulay.devpulse.core.domain.models.feed

data class RssFeed(
    val id: Int,
    val title: String?,
    val link: String?,
    val description: String?,
    val image: RssFeedImage?,
    val lastBuildDate: String?,
    val updatePeriod: String?,
    val items: List<RssFeedItem>,
    val youtubeChannel: RssFeedYoutubeChannel?,
    val createdAt: Long,
    val updatedAt: Long
)