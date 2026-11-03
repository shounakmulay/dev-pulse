package dev.shounakmulay.devpulse.feature.feed.components.postFilterSort

import dev.shounakmulay.devpulse.feature.feed.model.UIFeedPost

sealed interface PostListEntry {
    val key: String

    data class Post(val post: UIFeedPost) : PostListEntry {
        override val key: String get() = post.id.value
    }

    data class Empty(val search: Boolean, val readStatus: Boolean?, val filtered: Boolean) : PostListEntry {
        override val key: String get() = "empty-posts"
    }
}
