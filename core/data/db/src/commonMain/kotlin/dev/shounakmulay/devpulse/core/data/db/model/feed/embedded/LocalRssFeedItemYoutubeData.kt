package dev.shounakmulay.devpulse.core.data.db.model.feed.embedded

data class LocalRssFeedItemYoutubeData(
    val videoId: String?,
    val title: String?,
    val videoUrl: String?,
    val thumbnailUrl: String?,
    val description: String?,
    val viewsCount: Int?,
    val likesCount: Int?,
)