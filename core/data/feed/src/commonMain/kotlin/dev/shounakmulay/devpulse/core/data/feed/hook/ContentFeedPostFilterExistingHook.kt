package dev.shounakmulay.devpulse.core.data.feed.hook

import dev.shounakmulay.devpulse.core.data.feed.hook.model.PostWithIdentity
import dev.shounakmulay.devpulse.core.preferences.DevPulsePreferenceKeys
import dev.shounakmulay.devpulse.core.preferences.DevPulsePreferences
import org.koin.core.annotation.Factory

@Factory
internal class ContentFeedPostFilterExistingHook(
    private val preferences: DevPulsePreferences
) :
    CoreBatchHook<PostWithIdentity> {
     override suspend fun process(posts: List<PostWithIdentity>): List<PostWithIdentity> {
        val reImportPosts = preferences.get(DevPulsePreferenceKeys.reImportExistingPosts)
        if (reImportPosts == true) {
            return posts
        }

        return posts.filter { post ->
            post.identity == null
        }
    }

}