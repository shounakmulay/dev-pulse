package dev.shounakmulay.devpulse.core.data.settings

import dev.shounakmulay.devpulse.core.data.preferences.DevPulsePreferenceKeys
import dev.shounakmulay.devpulse.core.data.preferences.DevPulsePreferences
import dev.shounakmulay.devpulse.core.domain.models.post.FeedsPostListItemVariant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Factory

@Factory
class FeedSettingsRepositoryImpl(
    private val preferences: DevPulsePreferences
) : FeedSettingsRepository {
    override fun observeFeedPostListItemVariant(defaultVariant: FeedsPostListItemVariant): Flow<FeedsPostListItemVariant> {
        return preferences.observe(DevPulsePreferenceKeys.feedPostListItemVariant)
            .map { storedVariant ->
                FeedsPostListItemVariant.entries.firstOrNull { it.name == storedVariant }
                    ?: defaultVariant
            }
            .distinctUntilChanged()
    }

    override suspend fun setFeedPostListItemVariant(variant: FeedsPostListItemVariant) {
        preferences.set(DevPulsePreferenceKeys.feedPostListItemVariant, variant.name)
    }
}
