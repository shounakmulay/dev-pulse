package dev.shounakmulay.devpulse.core.domain.feed.queue.hooks

import dev.shounakmulay.devpulse.core.data.preferences.DevPulsePreferenceKeys
import dev.shounakmulay.devpulse.core.data.preferences.DevPulsePreferences
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedQueueActionType
import dev.shounakmulay.devpulse.core.domain.models.post.RssFeedPostWithExistingIdentity
import org.koin.core.annotation.Factory

@Factory
class ContentFeedPostFilterExistingHook(
    private val preferences: DevPulsePreferences
) : CorePostsBatchHook {
    override suspend fun process(
        actionType: RssFeedQueueActionType,
        posts: List<RssFeedPostWithExistingIdentity>
    ): List<RssFeedPostWithExistingIdentity> {
        val reImportPosts = preferences.get(DevPulsePreferenceKeys.reImportExistingPosts)
        if (reImportPosts == true) {
            return posts
        }

        return posts.filter { post ->
            post.identity == null
        }
    }

}