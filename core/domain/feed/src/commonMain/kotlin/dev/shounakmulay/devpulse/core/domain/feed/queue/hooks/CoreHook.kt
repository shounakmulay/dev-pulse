package dev.shounakmulay.devpulse.core.domain.feed.queue.hooks

import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueActionType
import dev.shounakmulay.devpulse.core.domain.models.post.RssFeedPostWithExistingIdentity

fun interface CorePostsItemHook {
    suspend fun process(
        actionType: RssFeedQueueActionType,
        post: RssFeedPostWithExistingIdentity
    ): RssFeedPostWithExistingIdentity
}

fun interface CorePostsBatchHook {
    suspend fun process(
        actionType: RssFeedQueueActionType,
        posts: List<RssFeedPostWithExistingIdentity>
    ): List<RssFeedPostWithExistingIdentity>
}
