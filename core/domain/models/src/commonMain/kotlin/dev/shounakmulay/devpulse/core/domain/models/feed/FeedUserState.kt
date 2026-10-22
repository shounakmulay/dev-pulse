package dev.shounakmulay.devpulse.core.domain.models.feed

import kotlinx.serialization.Serializable

@Serializable
data class FeedUserState(
    val pinned: Boolean = false,
    val lastOpenedAt: Long? = null,
)
