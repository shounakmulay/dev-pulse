package dev.shounakmulay.devpulse.core.domain.models.post

import dev.shounakmulay.devpulse.core.domain.models.common.UUID

data class RssFeedPostContent(
    val postId: UUID,
    val type: RssFeedPostContentType,
    val content: String
)