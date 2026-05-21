package dev.shounakmulay.devpulse.core.domain.models.feed

data class RssFeedItemYoutubeData(
    val videoId: String?,
    val title: String?,
    val videoUrl: String?,
    val thumbnailUrl: String?,
    val description: String?,
    val viewsCount: Int?,
    val likesCount: Int?,
)