package dev.shounakmulay.devpulse.core.domain.models.feed

data class RssFeedPost(
    val id: String,
    val feedId: String,
    val fingerprint: String?,
    val guid: String?,
    val title: String?,
    val author: String?,
    val link: String?,
    val pubDate: String?,
    val description: String?,
    val content: String?,
    val image: String?,
    val audio: String?,
    val video: String?,
    val sourceName: String?,
    val sourceUrl: String?,
    val categories: List<String>,
    val commentsUrl: String?,
    val youtubeItemData: RssFeedPostYoutubeData?,
    val rawEnclosure: RssFeedPostRawEnclosure?,
    val rawMediaContent: RssFeedPostMediaContent? = null,
)
