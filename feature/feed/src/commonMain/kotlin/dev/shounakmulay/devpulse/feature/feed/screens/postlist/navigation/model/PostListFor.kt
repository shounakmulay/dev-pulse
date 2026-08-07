package dev.shounakmulay.devpulse.feature.feed.screens.postlist.navigation.model

import dev.shounakmulay.devpulse.core.domain.models.common.UUID

sealed interface PostListFor {
    data object All: PostListFor
    data class Feed(val feedId: UUID): PostListFor
}