package dev.shounakmulay.devpulse.core.data.feed.parser.model

data class ParsedFeedMetadata(
    val title: String?,
    val link: String?,
    val description: String?,
    val image: ParsedFeedImage?,
    val lastBuildDate: String?,
    val updatePeriod: String?,
    val youtubeChannel: ParsedFeedYoutubeChannel?
)
