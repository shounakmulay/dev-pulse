package dev.shounakmulay.devpulse.core.domain.models.post

import kotlinx.serialization.Serializable

@Serializable
data class PostUserState(
    val isRead: Boolean = false,
    val bookmarked: Boolean = false,
)
