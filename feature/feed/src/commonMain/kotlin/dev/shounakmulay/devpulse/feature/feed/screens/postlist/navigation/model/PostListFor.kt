package dev.shounakmulay.devpulse.feature.feed.screens.postlist.navigation.model

sealed interface PostListFor {
    data object All: PostListFor
    data class Feed(val feedId: String): PostListFor
}