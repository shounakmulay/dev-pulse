package dev.shounakmulay.devpulse.core.data.settings

import dev.shounakmulay.devpulse.core.domain.models.post.FeedsPostListItemVariant
import kotlinx.coroutines.flow.Flow

interface FeedSettingsRepository {
    fun observeFeedPostListItemVariant(defaultVariant: FeedsPostListItemVariant): Flow<FeedsPostListItemVariant>
    suspend fun setFeedPostListItemVariant(variant: FeedsPostListItemVariant)
    fun observeSyncInBackground(defaultValue: Boolean): Flow<Boolean>
    suspend fun setSyncInBackground(enabled: Boolean)
}
