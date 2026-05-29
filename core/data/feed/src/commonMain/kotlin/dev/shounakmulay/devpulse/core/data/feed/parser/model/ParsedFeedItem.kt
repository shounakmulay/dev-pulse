package dev.shounakmulay.devpulse.core.data.feed.parser.model

data class ParsedFeedItem(
    val ordinal: Int,
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
    val youtubeItemData: ParsedFeedItemYoutubeData?,
    val rawEnclosure: ParsedFeedItemRawEnclosure?,
    val rawMediaContent: ParsedFeedItemMediaContent?
)
