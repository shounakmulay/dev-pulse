package dev.shounakmulay.devpulse.core.data.feed.hook

import dev.shounakmulay.devpulse.core.data.feed.hook.model.PostWithIdentity
import org.koin.core.annotation.Factory

@Factory
internal class ContentFeedPostNormalisationHook(
) : CoreItemHook<PostWithIdentity> {
    override suspend fun process(
        post: PostWithIdentity,
    ): PostWithIdentity {
        TODO("Not yet implemented")
    }
}