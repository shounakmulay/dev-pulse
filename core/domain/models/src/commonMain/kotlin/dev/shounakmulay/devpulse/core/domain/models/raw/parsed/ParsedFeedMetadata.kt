package dev.shounakmulay.devpulse.core.domain.models.raw.parsed

data class ParsedFeedMetadata(
    val title: String?,
    val link: String?,
    val description: String?,
    val image: ParsedFeedImage?,
    val lastBuildDate: String?,
    val updatePeriod: String?,
    val etag: String?,
    val youtubeChannel: ParsedFeedYoutubeChannel?
) {
    companion object {
        inline fun build(block: Builder.() -> Unit): ParsedFeedMetadata {
            return Builder().apply(block).build()
        }

        class Builder {
            var title: String? = null
            var link: String? = null
            var description: String? = null
            var image: ParsedFeedImage? = null
            var lastBuildDate: String? = null
            var updatePeriod: String? = null
            var youtubeChannelId: String? = null
            var etag: String? = null

            fun build(): ParsedFeedMetadata {
                return ParsedFeedMetadata(
                    title = title,
                    link = link,
                    description = description,
                    image = image,
                    lastBuildDate = lastBuildDate,
                    updatePeriod = updatePeriod,
                    youtubeChannel = youtubeChannelId?.let { ParsedFeedYoutubeChannel(channelId = it) },
                    etag = etag
                )
            }
        }
    }
}
