package dev.shounakmulay.devpulse.core.domain.models.post

import dev.shounakmulay.devpulse.core.domain.models.common.UUID

data class RssPostCategory(
    val postId: UUID,
    val category: String
)