package dev.shounakmulay.devpulse.core.domain.models.raw.parsed

data class ParsedFeedItem(
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
) {
    companion object {
        inline fun build(block: Builder.() -> Unit): ParsedFeedItem {
            return Builder().apply(block).build()
        }

        class Builder {
            var guid: String? = null
            var title: String? = null
            var author: String? = null
            var link: String? = null
            var pubDate: String? = null
            var description: String? = null
            var content: String? = null
            var image: String? = null
            var audio: String? = null
            var video: String? = null
            var sourceName: String? = null
            var sourceUrl: String? = null
            var categories: List<String> = emptyList()
            var commentsUrl: String? = null
            var youtubeItemData: ParsedFeedItemYoutubeData? = null
            var rawEnclosure: ParsedFeedItemRawEnclosure? = null
            var rawMediaContent: ParsedFeedItemMediaContent? = null

            fun build(): ParsedFeedItem {
                return ParsedFeedItem(
                    guid = guid,
                    title = title,
                    author = author,
                    link = link,
                    pubDate = pubDate,
                    description = description,
                    content = content,
                    image = image,
                    audio = audio,
                    video = video,
                    sourceName = sourceName,
                    sourceUrl = sourceUrl,
                    categories = categories,
                    commentsUrl = commentsUrl,
                    youtubeItemData = youtubeItemData,
                    rawEnclosure = rawEnclosure,
                    rawMediaContent = rawMediaContent
                )
            }
        }
    }
}
