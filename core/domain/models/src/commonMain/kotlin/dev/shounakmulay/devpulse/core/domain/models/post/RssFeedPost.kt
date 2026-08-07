package dev.shounakmulay.devpulse.core.domain.models.post

import dev.shounakmulay.devpulse.core.domain.models.common.UUID

data class RssFeedPost(
    val id: UUID,
    val feedId: UUID,
    val fingerprint: String?,
    val guid: String?,
    val title: String?,
    val author: String?,
    val link: String?,
    val publishedAtMillis: Long?,
    val description: String?,
    val content: String?,
    val image: String?,
    val audio: String?,
    val video: String?,
    val sourceName: String?,
    val sourceUrl: String?,
    val categories: List<String>,
    val commentsUrl: String?,
    val bookmarked: Boolean,
    val youtubeItemData: RssFeedPostYoutubeData?,
    val rawEnclosure: RssFeedPostRawEnclosure?,
    val rawMediaContent: RssFeedPostMediaContent? = null,
    val createdAtMillis: Long
)