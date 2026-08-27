package dev.shounakmulay.devpulse.core.domain.models.post

data class RssFeedPostWithExistingIdentity(
    val post: RssFeedPost,
    val identity: RssFeedPostIdentity?
) {
    init {
        if (identity != null) {
            require(post.id == identity.id) {
                "Post ID and identity ID must match for a post with existing identity."
            }
        }
    }
}