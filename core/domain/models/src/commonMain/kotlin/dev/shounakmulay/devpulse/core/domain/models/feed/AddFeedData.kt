package dev.shounakmulay.devpulse.core.domain.models.feed

data class AddFeedData(
    val url: String,
    val name: String?,
    val type: RssFeedType,
    val tags: List<Int>,
    val folders: List<Int>
)
