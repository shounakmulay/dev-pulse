package dev.shounakmulay.devpulse.core.domain.models.raw.parsed

data class ParsedFeedItemYoutubeData(
    val videoId: String?,
    val title: String?,
    val videoUrl: String?,
    val thumbnailUrl: String?,
    val description: String?,
    val viewsCount: Int?,
    val likesCount: Int?
) {
    companion object {
        internal inline fun build(block: Builder.() -> Unit): ParsedFeedItemYoutubeData {
            return Builder().apply(block).build()
        }

        internal class Builder {
            var videoId: String? = null
            var title: String? = null
            var videoUrl: String? = null
            var thumbnailUrl: String? = null
            var description: String? = null
            var viewsCount: Int? = null
            var likesCount: Int? = null

            fun build(): ParsedFeedItemYoutubeData {
                return ParsedFeedItemYoutubeData(
                    videoId = videoId,
                    title = title,
                    videoUrl = videoUrl,
                    thumbnailUrl = thumbnailUrl,
                    description = description,
                    viewsCount = viewsCount,
                    likesCount = likesCount
                )
            }
        }
    }
}
