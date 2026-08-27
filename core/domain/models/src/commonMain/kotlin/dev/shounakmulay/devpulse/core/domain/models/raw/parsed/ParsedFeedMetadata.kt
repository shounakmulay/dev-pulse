package dev.shounakmulay.devpulse.core.data.feed.parser.model

data class ParsedFeedMetadata(
    val title: String?,
    val link: String?,
    val description: String?,
    val image: ParsedFeedImage?,
    val lastBuildDate: String?,
    val updatePeriod: String?,
    val youtubeChannel: ParsedFeedYoutubeChannel?
) {
    companion object {
        internal inline fun build(block: Builder.() -> Unit): ParsedFeedMetadata {
            return Builder().apply(block).build()
        }

        internal class Builder {
            var title: String? = null
            var link: String? = null
            var description: String? = null
            var image: ParsedFeedImage? = null
            var lastBuildDate: String? = null
            var updatePeriod: String? = null
            var youtubeChannelId: String? = null

            fun build(): ParsedFeedMetadata {
                return ParsedFeedMetadata(
                    title = title,
                    link = link,
                    description = description,
                    image = image,
                    lastBuildDate = lastBuildDate,
                    updatePeriod = updatePeriod,
                    youtubeChannel = youtubeChannelId?.let { ParsedFeedYoutubeChannel(channelId = it) }
                )
            }
        }
    }
}
