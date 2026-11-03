package dev.shounakmulay.devpulse.feature.feed.components.postFilterSort

import dev.shounakmulay.devpulse.feature.feed.model.UIFeedPost

sealed interface PostListEntry {
    val key: String

    data class Post(val post: UIFeedPost) : PostListEntry {
        override val key: String get() = post.id.value
    }

    sealed interface Empty : PostListEntry {
        override val key: String get() = "empty-posts"

        data object Search : Empty
        data object Filtered : Empty
        data class ReadStatus(val read: Boolean) : Empty
        data object NoPosts : Empty
    }
}
